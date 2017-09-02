package com.wayd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.Message;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.PhotoWaydeur;
import com.wayd.bean.PushAndroidMessage;
import com.wayd.bean.ReceiverGCM;
import com.wayd.bean.RetourMessage;
import com.wayd.busMessaging.MessageBus;
import com.wayd.listadapter.MessageAdapter;

import org.apache.commons.lang3.StringEscapeUtils;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MesMessages extends MenuDrawerNew implements AsyncTaches.AsyncAddMessage.AsyncAddMessageListener,
        AsyncTaches.AsyncEffaceMessageRecu.AsyncEffaceMessageRecuListener,
        AsyncTaches.AsyncEffaceMessageEmis.AsyncEffaceMessageEmisListener,
        AsyncTaches.AsyncGetListMessageNext.AsyncGetListMessageNextListener,
        AsyncTaches.AsyncAcquitMessage.AsyncAcquitMessageListener,
        AsyncTaches.AsyncAcquitDiscussion.AsyncAcquitDiscussionListener,
        ReceiverGCM.GCMMessageListener,
        AsyncTaches.AsyncGetListMessageFull.AsyncGetListMessageListenerFull {


    private ListView listViewMessage;
    private final ArrayList<Message> listeMessage = new ArrayList<>();
    private MessageAdapter adapter;
    private int idmessageselection;
    private int idemetteurMessage;
    private int idxmessage;
    private int idemetteur;
    private EditText chatText;
    private String message;
    private Message aEffacer;
    private String idDiscussion;
    private final Map<Integer, Bitmap> listPhoto = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Identifie la discussion à l'origine si elle vient
        idemetteur = getIntent().getIntExtra("idemetteur", 0);
        idDiscussion = getIntent().getStringExtra(MesDiscussions.CLE_DISCUSSION);
        setContentView(R.layout.mes_messages);
        InitDrawarToolBar();
        initTableauDeBord();
        adapter = new MessageAdapter(this, listeMessage);
        listViewMessage = (ListView) findViewById(R.id.LV_listeMessages);
        ImageButton Send = (ImageButton) findViewById(R.id.id_envoyer);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validator()) envoiMessage();
            }
        });
        listViewMessage.setAdapter(adapter);
        adapter.addListener(new MessageAdapter.MessageAdapterListener() {
            @Override
            public void onClickMessage(Message item, int position) {

                if (item.isLu() || item.getIdemetteur() == Outils.personneConnectee.getId()) return;
                idmessageselection = item.getIdmessage();
                litMessage(idmessageselection);
            }

            @Override
            public void onLongClickMessage(Message item, int position) {
                dialogEffaceMessage(item);
            }

            @Override
            public void onPhotoClickMessage(Message item, int position) {

                if (item.getIdemetteur() != Outils.personneConnectee.getId()) {
                    Intent appel;
                    appel = new Intent(MesMessages.this, UnProfil.class);
                    appel.putExtra("idpersonne", item.getIdemetteur());
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(appel);
                }
            }

        });
        Outils.LOOP_BACK_RECEIVER_GCM.addGCMMessageListener(this);
        chatText = (EditText) findViewById(R.id.id_reponse);
        chatText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (validator()) envoiMessage();

                    handled = true;
                }
                return handled;
            }
        });

        new AsyncTaches.AsyncGetListMessageFull(this, Outils.personneConnectee.getId(), idemetteur, MesMessages.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void litMessage(int idmessageselection) {
        new AsyncTaches.AsyncAcquitMessage(this, Outils.personneConnectee.getId(), idmessageselection, MesMessages.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getMessageNext(boolean visibleProgress) {
        new AsyncTaches.AsyncGetListMessageNext(this, Outils.personneConnectee.getId(), idxmessage,visibleProgress, MesMessages.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void envoiMessage() {

        new AsyncTaches.AsyncAddMessage(this, Outils.personneConnectee.getId(), message, idemetteur, MesMessages.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loopBack_AddMessage(RetourMessage result) {


        if (result != null) {
            Toast toast = null;
            switch (result.getId()) {

                case RetourMessage.NON_AUTORISE:

                    toast = Toast.makeText(getBaseContext(), "Accés refusé", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    finish();
                    break;

                case RetourMessage.PLUS_SON_AMI:

                    toast = Toast.makeText(getBaseContext(), "Tu n'es plus son ami", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    finish();
                    break;

                default:
                    Message monmessage = new Message(message, new Date(result.getDateCreation()), Outils.personneConnectee.getPseudo(),
                            Outils.personneConnectee.getNom(), Outils.personneConnectee.getId(), result.getId());
                    monmessage.setPhoto(listPhoto.get(Outils.personneConnectee.getId()));
                    listeMessage.add(listeMessage.size(), monmessage);
                    adapter.notifyDataSetChanged();
                    listViewMessage.setSelection(listeMessage.size());
                    chatText.setText("");
                    updateRetourMessage();


            }


        }

        gestionDefaultLMessage();// Affiche le message par defaut si il n'y plus de message
    }

    private void dialogEffaceMessage(Message item) {
        aEffacer = item;
        idmessageselection = item.getIdmessage();
        idemetteurMessage = item.getIdemetteur();
        AlertDialog.Builder builder = new AlertDialog.Builder(MesMessages.this);
        builder.setTitle(R.string.SupprimeMessage_Titre);
        builder.setMessage(R.string.SupprimeMessage_Message);
        builder.setPositiveButton(R.string.SupprimeMessage_OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                // ************************************************
                if (idemetteurMessage == Outils.personneConnectee.getId())
                    effaceMessageEmis();
                else
                    effaceMessageRecu();


                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.SupprimeMessage_No, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button buttonNon = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNon.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.altertDialog_Fondbutton));
        buttonNon.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.altertDialog_Textbutton));
        Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOui.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.altertDialog_Fondbutton));
        buttonOui.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.altertDialog_Textbutton));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 0, 15, 0);
        buttonOui.setLayoutParams(params);
    }

    private void effaceMessageEmis() {

        new AsyncTaches.AsyncEffaceMessageEmis(this, Outils.personneConnectee.getId(), idmessageselection, MesMessages.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void effaceMessageRecu() {
        new AsyncTaches.AsyncEffaceMessageRecu(this, Outils.personneConnectee.getId(), idmessageselection, MesMessages.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loopBack_EffaceMessageEmis(MessageServeur messageserveur) {
        if (messageserveur != null) {

            if (messageserveur.isReponse()) {
                listeMessage.remove(aEffacer);
                updateRetourMessage();
                adapter.notifyDataSetChanged();

            }

        }
        gestionDefaultLMessage();// Affiche le message par defaut si il n'y plus de message
    }

    private void updateRetourMessage() {

        MessageBus messageBus = new MessageBus(idDiscussion, null, "");

        if (listeMessage.size() > 0) {
            messageBus.setMessage(listeMessage.get(listeMessage.size() - 1).getCorps());
            messageBus.setDatecreation(listeMessage.get(listeMessage.size() - 1).getDatecreation());
            messageBus.setIdDiscussion(idDiscussion);
        }

        Outils.busMessaging.sendMessage(messageBus);

    }

    @Override
    public void loopBack_EffaceMessageRecu(MessageServeur messageserveur) {
        if (messageserveur != null) {
            if (messageserveur.isReponse()) {
                listeMessage.remove(aEffacer);
                updateRetourMessage();
                adapter.notifyDataSetChanged();
            }
        }
        gestionDefaultLMessage();// Affiche le message par defaut si il n'y plus de message
    }

    private void gestionDefaultLMessage() {// Permet d'afficher un message si le nbr d'ami est null
        TextView TV_MessageDefaut = (TextView) findViewById(R.id.id_messagebalise);
        ListView LV_ListAmis = (ListView) findViewById(R.id.LV_listeMessages);

        if (listeMessage != null) {
            if (listeMessage.isEmpty()) {
                TV_MessageDefaut.setText(R.string.s_mesmessages_noResutat);

                TV_MessageDefaut.setVisibility(View.VISIBLE);
                LV_ListAmis.setVisibility(View.GONE);

            } else {
                TV_MessageDefaut.setText("");
                TV_MessageDefaut.setVisibility(View.GONE);
                LV_ListAmis.setVisibility(View.VISIBLE);

            }
        }

    }


    @Override
    public void loopBack_GetListMessageNext(ArrayList<Message> listemessage) {
        if (listemessage != null) {
            for (Message message : listemessage) {

                if (message.getIdemetteur() == idemetteur) {// Verifie que le message est bien dans le chat en cours

                    boolean ajout = true;
                    for (Message messageExisant : listeMessage) {// Verifie que le message n'existe pas peut se produire si les 2 threads sont lancé en même temps
                        if (messageExisant.getIdmessage() == message.getIdmessage())// Si le message existe ajoute=false
                            ajout = false;
                    }

                    if (ajout) {
                        message.setPhoto(listPhoto.get(message.getIdemetteur()));
                        listeMessage.add(listeMessage.size(), message);
                        adapter.notifyDataSetChanged();
                        idxmessage = message.getIdmessage();
                        listViewMessage.setSelection(listeMessage.size());// met le focus
                    }

                }

            }
            updateRetourMessage();
        }
        gestionDefaultLMessage();// Affiche le message par defaut si il n'y plus de message

    }

    @Override
    public void loopBack_AcquitMessage(MessageServeur messageserveur) {
        if (messageserveur != null) {
            Iterator<Message> it = listeMessage.iterator();
            while (it.hasNext()) {
                Message message = it.next();
                if (message.getIdmessage() == idmessageselection) message.setLu(true);
            }
            adapter.notifyDataSetChanged();
        }

    }


    private boolean validator() {

        message = (StringEscapeUtils.escapeJava(chatText.getText().toString())).trim();
        return !message.isEmpty();
    }

    protected void onDestroy() {

        new AsyncTaches.AsyncAcquitDiscussion(this, Outils.personneConnectee.getId(), idemetteur, Outils.jeton, MesMessages.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Outils.LOOP_BACK_RECEIVER_GCM.removeGCMMessageListener(this);
        super.onDestroy();
    }


    @Override
    public void loopBack_AcquitDiscussion(MessageServeur messageserveur) {

    }

    @Override
    public void loopBackReceiveGCM(Bundle extras) {

        //int idpersonne = 0;

        if (extras == null) return;

        int idMessageGcm = 0;

        try {

            idMessageGcm = Integer.parseInt(extras.getString("id"));
        } catch (Exception e) {

            //   e.printStackTrace();
            return;
        }


        switch (idMessageGcm) {

            case PushAndroidMessage.NBR_MESSAGE_NONLU:
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                getMessageNext(false);
                break;
        }
    }


    @Override
    public void loopBack_GetListeMessageFull(ArrayList<Message> plistemessage, ArrayList<PhotoWaydeur> listPhotoWaydeur) {
        // Met les photos dans la map
        for (PhotoWaydeur photoWaydeur : listPhotoWaydeur) {
            listPhoto.put(photoWaydeur.getId(), photoWaydeur.getPhoto());
        }
        //
        if (plistemessage != null) {

            this.listeMessage.clear();
            this.listeMessage.addAll(plistemessage);

            for (Message message : listeMessage) {
                message.setPhoto(listPhoto.get(message.getIdemetteur()));
            }

            adapter.notifyDataSetChanged();
            if (this.listeMessage.size() > 0) {
                idxmessage = this.listeMessage.get(listeMessage.size() - 1).getIdmessage();
                this.listViewMessage.setSelection(listeMessage.size());
            }

            updateRetourMessage();

        }

        gestionDefaultLMessage();// Affiche le message par defaut si il n'y plus de message
    }
}
