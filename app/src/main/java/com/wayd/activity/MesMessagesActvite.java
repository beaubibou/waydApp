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

public class MesMessagesActvite extends MenuDrawerNew implements AsyncTaches.AsyncAddMessageByAct.AsyncAddMessageByActListener,
        AsyncTaches.AsyncEffaceMessageRecuByAct.AsyncEffaceMessageRecuListenerByAct,
        AsyncTaches.AsyncEffaceMessageEmisByAct.AsyncEffaceMessageEmisListenerByAct,
        AsyncTaches.AsyncGetListMessageNextByAct.AsyncGetListMessageNextListenerByAct,
        AsyncTaches.AsyncAcquitMessageByAct.AsyncAcquitMessageListenerByAct,
        AsyncTaches.AsyncAcquitDiscussionByAct.AsyncAcquitDiscussionListenerByAct, ReceiverGCM.GCMMessageListener, AsyncTaches.AsyncGetListPhotoWaydeur.Async_GetListPhotoWaydeurListener, AsyncTaches.AsyncGetPhotoWaydeur.AsyncGetPhotoWaydeurListener, AsyncTaches.AsyncGetListMessageFullByAct.AsyncGetListMessageFullListenerByAct {

    private ListView listViewMessage;
    private final ArrayList<Message> listeMessage = new ArrayList<>();
    private MessageAdapter adapter;
    private int idmessageselection;
    private int idemetteurMessage;
    private int idxmessage;
    private int idactivite;
    private EditText chatText;
    private String idDiscussion;
    private int idNewMessage = 0;
    private String message;
    private final Map<Integer, Bitmap> listPhoto = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialsie le retour de l'activiet pour la mise à jour de la discussion
        idactivite = getIntent().getIntExtra("idactivite", 0);
        idDiscussion = getIntent().getStringExtra(MesDiscussions.CLE_DISCUSSION);

        //*******************************************

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
                    appel = new Intent(MesMessagesActvite.this, UnProfil.class);
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

    //    new AsyncTaches.AsyncGetListMessageByAct(this, Outils.personneConnectee.getId(), idactivite, MesMessagesActvite.this)
        //        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new AsyncTaches.AsyncGetListMessageFullByAct(this, Outils.personneConnectee.getId(), idactivite, MesMessagesActvite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    private void getMessageNext(boolean visibleProgress) {
        new AsyncTaches.AsyncGetListMessageNextByAct(this, Outils.personneConnectee.getId(), idxmessage, idactivite,visibleProgress, MesMessagesActvite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void litMessage(int idmessageselection) {
        new AsyncTaches.AsyncAcquitMessageByAct(this, Outils.personneConnectee.getId(), idmessageselection, MesMessagesActvite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }



    @Override
    public void loopBack_GetListMessageNextByAct(ArrayList<Message> result) {

        if (result != null) {
            for (Message message : result) {

                if (message.getIdactivite() == idactivite) {// Verifie que le message est bien dans le chat en cours

                    // mise à jour photo message


                    if (!listPhoto.containsKey(message.getIdemetteur())) {
                        idNewMessage = message.getIdmessage();// On recoit un nouveau message  on regarde si on a la photo cela se produit
                        // si une personne entre dans l'activité en cours
                        new AsyncTaches.AsyncGetPhotoWaydeur(this, message.getIdemetteur(), MesMessagesActvite.this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    } else

                    message.setPhoto(listPhoto.get(message.getIdemetteur()));

                    boolean ajout=true;
                    for (Message messageExisant:listeMessage){// Verifie que le message n'existe pas peut se produire si les 2 threads sont lancé en même temps
                       if  (messageExisant.getIdmessage()==message.getIdmessage())// Si le message existe ajoute=false
                           ajout=false;
                    }

                    if (ajout) {// Si il n'existe pas on l'ajoute
                        listeMessage.add(listeMessage.size(), message);// ajoute à la fin
                        adapter.notifyDataSetChanged();
                        idxmessage = message.getIdmessage();
                        listViewMessage.setSelection(listeMessage.size());
                    }
                    }

                updateRetourMessage();

            }
        }
        gestionDefaultLMessage();// affiche le message par défaut si par d'activite
    }



    @Override
    public void loopBack_EffaceMessageEmisByAct(MessageServeur result) {
        if (result != null) {

            Iterator<Message> it = listeMessage.iterator();
            while (it.hasNext()) {
                Message message = it.next();
                if (message.getIdmessage() == idmessageselection) it.remove();

            }

            updateRetourMessage();
            adapter.notifyDataSetChanged();
        }
        gestionDefaultLMessage();// affiche le message par défaut si par d'activite
    }

    @Override
    public void loopBack_EffaceMessageRecuByAct(MessageServeur result) {
        if (result != null) {

            Iterator<Message> it = listeMessage.iterator();
            while (it.hasNext()) {
                Message message = it.next();
                if (message.getIdmessage() == idmessageselection) it.remove();
            }

            updateRetourMessage();
            adapter.notifyDataSetChanged();

        }
        gestionDefaultLMessage();// affiche le message par défaut si par d'activite
    }

    @Override
    public void loopBack_AddMessageByAct(RetourMessage result) {

        if (result != null) {

            Toast toast=null;
            switch (result.getId()) {

                case RetourMessage.NON_AUTORISE:
                    toast =Toast.makeText(getBaseContext(),R.string.accesRefuse, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    finish();
                    break;
                case RetourMessage.PLUS_INSCRIT:
                     toast =Toast.makeText(getBaseContext(),R.string.noInscription, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    finish();
                    break;

                default:
                    Message monmessage = new Message(message, new Date(result.getDateCreation()), Outils.personneConnectee.getPseudo(),
                            Outils.personneConnectee.getNom(), Outils.personneConnectee.getId(), result.getId());
                    monmessage.setPhoto(listPhoto.get(Outils.personneConnectee.getId()));
                    listeMessage.add(listeMessage.size(), monmessage);
                    listViewMessage.setSelection(listeMessage.size());
                    chatText.setText("");
                    updateRetourMessage();


            }

        }
        gestionDefaultLMessage();// affiche le message par défaut si par d'activite
    }

    @Override
    public void loopBack_AcquitDiscussionByAct(MessageServeur messageserveur) {

    }

    @Override
    public void loopBack_AcquitMessageByAct(MessageServeur messageserveur) {

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
        message = StringEscapeUtils.escapeJava(chatText.getText().toString()).trim();
        return !message.isEmpty();
    }

    private void effaceMessageRecu() {
        new AsyncTaches.AsyncEffaceMessageRecuByAct(this, Outils.personneConnectee.getId(), idmessageselection, MesMessagesActvite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void dialogEffaceMessage(Message item) {
        idmessageselection = item.getIdmessage();
        idemetteurMessage = item.getIdemetteur();
        AlertDialog.Builder builder = new AlertDialog.Builder(MesMessagesActvite.this);
        builder.setTitle(R.string.SupprimeMessageActivite_Titre);
        builder.setMessage(R.string.SupprimeMessageActivite_Message);
        builder.setPositiveButton(R.string.SupprimeMessageActivite_OK, new DialogInterface.OnClickListener() {

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

        builder.setNegativeButton(R.string.SupprimeMessageActivite_No, new DialogInterface.OnClickListener() {

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

        new AsyncTaches.AsyncEffaceMessageEmisByAct(this, Outils.personneConnectee.getId(), idmessageselection, MesMessagesActvite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    protected void onDestroy() {
        new AsyncTaches.AsyncAcquitDiscussionByAct(this, Outils.personneConnectee.getId(), idactivite, MesMessagesActvite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Outils.LOOP_BACK_RECEIVER_GCM.removeGCMMessageListener(this);
        super.onDestroy();
    }

    private void envoiMessage() {
        new AsyncTaches.AsyncAddMessageByAct(this, Outils.personneConnectee.getId(), message, idactivite, MesMessagesActvite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    public void loopBackReceiveGCM(Bundle extras) {

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
               boolean progressVisible=false;
               getMessageNext(progressVisible);
                break;


        }


    }

    @Override
    public void loopBack_GetListProfilDiscussion(ArrayList<PhotoWaydeur> listPhotoWaydeur) {

        for (PhotoWaydeur photoWaydeur : listPhotoWaydeur) {
            listPhoto.put(photoWaydeur.getId(), photoWaydeur.getPhoto());
        }
        //Etape 1 - on charge les messages
        // Dans listphoto on charge distinctement tous les participants.
        // SUr le serveur on récupére leurs Profildiscussion (id,photo)
        // On rechage les messages avec la photo.
        for (Message message : listeMessage) {
            message.setPhoto(listPhoto.get(message.getIdemetteur()));
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void loopBack_GetProfilDiscussion(PhotoWaydeur photoWaydeur) {// Est activiet lorsque l'on demande de récupere un id et une photo pour
        // mettre la photo dans le message. Cette méthode est appellé pour tous les nouveaux waydeurs chattant dans une activité.

        listPhoto.put(photoWaydeur.getId(), photoWaydeur.getPhoto());// charge la photo et l'id
        for (Message message : listeMessage) {
           if (message.getIdmessage() >= idNewMessage)
                message.setPhoto(listPhoto.get(message.getIdemetteur()));
            // Evite d'affecter la photo à tous les messages, n'affecte que pour les messages depuis que le méthode à été appellée
        }
        adapter.notifyDataSetChanged();
    }

    private void gestionDefaultLMessage(){// Permet d'afficher un message si le nbr de message est null
        TextView TV_MessageDefaut = (TextView) findViewById(R.id.id_messagebalise);
        ListView LV_ListAmis = (ListView) findViewById(R.id.LV_listeMessages);
        if (listeMessage!=null){
            if (listeMessage.isEmpty()){

                TV_MessageDefaut.setText(R.string.s_mesmessagesactivite_noResutat);
                TV_MessageDefaut.setVisibility(View.VISIBLE);
                LV_ListAmis.setVisibility(View.GONE);

            }
            else{
                TV_MessageDefaut.setText("");
                TV_MessageDefaut.setVisibility(View.GONE);
                LV_ListAmis.setVisibility(View.VISIBLE);

            }
        }

    }

    @Override
    public void loopBack_GetListeMessageFullByAct(ArrayList<Message> plistemessage, ArrayList<PhotoWaydeur> listPhotoWaydeur) {

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

