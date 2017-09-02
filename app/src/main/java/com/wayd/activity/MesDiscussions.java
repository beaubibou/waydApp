package com.wayd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.application.wayd.R;

import com.wayd.bean.Discussion;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.PushAndroidMessage;
import com.wayd.bean.ReceiverGCM;
import com.wayd.busMessaging.BusMessaging;
import com.wayd.busMessaging.MessageBus;
import com.wayd.comparator.ComparateurDiscussionDate;
import com.wayd.listadapter.DiscussionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class MesDiscussions extends MenuDrawerNew implements AsyncTaches.AsyncGetListDiscussion.Async_GetListDiscussionListener,
        ReceiverGCM.GCMMessageListener
        , AsyncTaches.AsyncEffaceDiscussion.Async_EffaceDiscussionListener, BusMessaging.BusMessagesListener {
    private boolean inPause;
    private static final int RETOUR_MESSAGE = 1023;
    private final ArrayList<Discussion> listeDiscussion = new ArrayList<>();
    private DiscussionAdapter adapter;
    public final static String CLE_DISCUSSION = "clediscussion";
    private int idemetteurdiscussion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mes_discussions);

        InitDrawarToolBar();
        initTableauDeBord();
        adapter = new DiscussionAdapter(this, listeDiscussion);
        ListView listViewMessage = (ListView) findViewById(R.id.LV_listeMessages);
        listViewMessage.setAdapter(adapter);
        boolean afficheProgress = true;
        new AsyncTaches.AsyncGetListDiscussion(this, Outils.personneConnectee.getId(),
                MesDiscussions.this, afficheProgress).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        adapter.addListener(new DiscussionAdapter.DiscussionAdapterListener() {
            @Override
            public void onClickDiscussion(Discussion discussion, int position) {//Gestion de la lecture du message
                Intent appel;
                switch (discussion.getType()) {

                    case Discussion.STAND_ALONE:
                        appel = new Intent(MesDiscussions.this, MesMessages.class);
                        appel.putExtra("idemetteur", discussion.getIdemetteur());
                        appel.putExtra(CLE_DISCUSSION, discussion.getId());
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(appel, RETOUR_MESSAGE);//
                        break;

                    case Discussion.GROUP_TALK:
                        appel = new Intent(MesDiscussions.this, MesMessagesActvite.class);
                        appel.putExtra("idactivite", discussion.getIdactivite());
                        appel.putExtra(CLE_DISCUSSION, discussion.getId());
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(appel, RETOUR_MESSAGE);//
                        break;
                }

            }

            @Override
            public void onLongClickDiscussion(Discussion item, int position) {
                switch (item.getType()) {
                    case Discussion.STAND_ALONE:

                        dialogEffaceDiscussion(item);
                        break;

                    case Discussion.GROUP_TALK:

                        break;
                }


            }// Gestion de la suppression

        });
        Outils.LOOP_BACK_RECEIVER_GCM.addGCMMessageListener(this);
        Outils.busMessaging.addBusMessageListener(this);
    }



    protected void onDestroy() {
        Outils.LOOP_BACK_RECEIVER_GCM.removeGCMMessageListener(this);
        Outils.busMessaging.removeBusMessageListener(this);
        super.onDestroy();
    }

    private void updateDiscussion() {
        new AsyncTaches.AsyncGetListDiscussion(this, Outils.personneConnectee.getId(), MesDiscussions.this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loopBack_GetListDiscussion(ArrayList<Discussion> result) {
        if (result != null) {

                for (Discussion discussion : result)
                    updateListDiscussion(discussion);//Rafraichit la discussion ou ajoute le nouvel item.

        }

        Collections.sort(listeDiscussion, new ComparateurDiscussionDate());
        adapter.notifyDataSetChanged();
        gestionDefaultLMessage();// affiche les message par defaut

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
                if (inPause) return;// Si on est en pause on ne met pas à jour les discussion
                // économie de BP
                updateDiscussion();

                break;
            case PushAndroidMessage.Annule_Activite:
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                // A enlzver dans la version finale. Pour eviter les prb du au mêm num GSM dans le cas de l'emulateur

                int idactivite = Integer.parseInt(extras.getString("idactivite"));
                supprimeDiscussion(idactivite);
                break;

            case PushAndroidMessage.Annule_PARTICIPATION:
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                // A enlzver dans la version finale. Pour eviter les prb du au mêm num GSM dans le cas de l'emulateur
                idactivite = Integer.parseInt(extras.getString("idactivite"));
                supprimeDiscussion(idactivite);
                break;
        }
    }

    @Override
    public void envoiBusMessage(MessageBus message) {

        updateDiscussion(message);

    }

    private void updateListDiscussion(Discussion vdiscussion) {

        for (Discussion discussion : listeDiscussion) {
            if (discussion.isEgale(vdiscussion)) {
                discussion.updateDiscussion(vdiscussion);
                return;
            }
        }
        listeDiscussion.add(0, vdiscussion);
        adapter.notifyDataSetChanged();
    }

    protected void onResume() {
        super.onResume();
        //Permet de ne pas mettre en jour les discussion si on a pas le focus
        inPause = false;
    }

    protected void onPause() {

        super.onPause();
        //Permet de ne pas mettre en jour les discussion si on a pas le focus
        inPause = true;

    }


    @Override
    public void loopBack_EffaceDiscussion(MessageServeur result) {

        if (result != null) {
            Iterator<Discussion> it = listeDiscussion.iterator();
            while (it.hasNext()) {
                Discussion discussion = it.next();
                if (discussion.getIdemetteur() == idemetteurdiscussion) it.remove();
            }
            adapter.notifyDataSetChanged();

        }
        gestionDefaultLMessage();
    }


    private void dialogEffaceDiscussion(Discussion item) {
        idemetteurdiscussion = item.getIdemetteur();
        AlertDialog.Builder builder = new AlertDialog.Builder(MesDiscussions.this);
        builder.setTitle(R.string.SupprimeDiscussion_Titre);
        builder.setMessage(R.string.SupprimeDiscussion_Message);
        builder.setPositiveButton(R.string.SupprimeDiscussion_OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                // ************************************************
                effaceDiscussion();
                // new Ws_effaceDiscussion().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.SupprimeDiscussion_No, new DialogInterface.OnClickListener() {

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

    private void effaceDiscussion() {
        new AsyncTaches.AsyncEffaceDiscussion(this, Outils.personneConnectee.getId(), idemetteurdiscussion,
                MesDiscussions.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void supprimeDiscussion(int idactivite) {
        Iterator<Discussion> it = listeDiscussion.iterator();
        while (it.hasNext()) {
            Discussion discussion = it.next();
            if (discussion.getIdactivite() == idactivite) it.remove();
        }
        adapter.notifyDataSetChanged();
    }

    private void updateDiscussion(MessageBus messagebus) {

        for (Discussion discussion : listeDiscussion) {
            if (discussion.getId().equals(messagebus.getIdDiscussion())) {
                discussion.update(messagebus);
            }
        }
        Collections.sort(listeDiscussion, new ComparateurDiscussionDate());
        adapter.notifyDataSetChanged();

    }

    private void gestionDefaultLMessage(){// Permet d'afficher un message si le nbr d'ami est null

        TextView TV_MessageDefaut = (TextView) findViewById(R.id.id_messagebalise);
        ListView LV_ListAmis = (ListView) findViewById(R.id.LV_listeMessages);
        if (listeDiscussion!=null){
            if (listeDiscussion.isEmpty()){
                // Affiche le message par defaut
                TV_MessageDefaut.setText(R.string.MesDiscussions_noResutat);
                TV_MessageDefaut.setVisibility(View.VISIBLE);
                LV_ListAmis.setVisibility(View.GONE);

            }
            else{
                // Affiche la liste de discussion
                TV_MessageDefaut.setText("");
                TV_MessageDefaut.setVisibility(View.GONE);
                LV_ListAmis.setVisibility(View.VISIBLE);

            }
        }

    }


}
