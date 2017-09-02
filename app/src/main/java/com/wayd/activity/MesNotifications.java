package com.wayd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Notification;
import com.wayd.bean.Outils;
import com.wayd.bean.ProfilNotation;
import com.wayd.bean.PushAndroidMessage;
import com.wayd.bean.ReceiverGCM;
import com.wayd.comparator.ComparatorTempsNotification;
import com.wayd.listadapter.NotificationAdapter;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class MesNotifications extends MenuDrawerNew implements AsyncTaches.AsyncAcquitAllNotification.AsyncAcquitTouteNotificationListener, AsyncTaches.AsyncAcquitNotification.AsyncAcquitNotificationListener,
        AsyncTaches.AsyncEffaceNotification.AsyncEffaceNotificationListener, AsyncTaches.AsyncGetListNotification.AsyncGetListNotificationListener,
        AsyncTaches.AsyncGetNextListNotification.AsyncGetNextListNotificationListener, ReceiverGCM.GCMMessageListener, AsyncTaches.AsyncGetDonneAvisFull.Async_GetDonneAvisListenerFull, AsyncTaches.AsyncAddNotation.Async_AddNotationListener {

    private int idnotificationselection;
    private final ArrayList<Notification> listeNotification = new ArrayList<>();
    private NotificationAdapter adapter;
    private final static int RETOUR_NOTATION = 1030;
    private int idxnotification;
    private SwipeRefreshLayout swipeContainer;
    private boolean isAmi;
    private int idactivite, idpersonnenotee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mes_notifications);
        InitDrawarToolBar();
        initTableauDeBord();

        adapter = new NotificationAdapter(this, listeNotification);
        ListView listViewNotification = (ListView) findViewById(R.id.LV_listeMessages);
        listViewNotification.setAdapter(adapter);

        adapter.addListener(new NotificationAdapter.NotificationAdapterListener() {
            @Override
            public void onClickNotification(Notification notification, int position) {

                idnotificationselection = notification.getIdnotification();


                if (!notification.isLu())
                    acquitNotification();

                switch (notification.getIdtypenotification()) {

                    case Notification.DONNE_AVIS:
                        idpersonnenotee = notification.getIdpersonne();
                        idactivite = notification.getIdactivite();
                        getFullAvis(idpersonnenotee, idactivite);

                        break;

                    case Notification.RECOIT_AVIS:
                        ouvreAvis();
                        break;


                }

            }

            @Override
            public void onLongClickMessage(Notification item, int position) {

                if (item.getIdtypenotification() != Notification.DONNE_AVIS)
                    dialogEffaceMessage(item);
            }
        });
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNextNotification(true);
                // DEmande de la part du swipe
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        Outils.LOOP_BACK_RECEIVER_GCM.addGCMMessageListener(this);
        new AsyncTaches.AsyncGetListNotification(this, Outils.personneConnectee.getId(), MesNotifications.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void getFullAvis(int idpersonnenotee, int idactivite) {// Recuepre les elements nécessaire pour afficher le popuponotation

        new AsyncTaches.AsyncGetDonneAvisFull(this, Outils.personneConnectee.getId(), idpersonnenotee, idactivite, MesNotifications.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void ouvrePopupNotation(final ProfilNotation profil, final Activite activite) {

        // Récupere le layour et le champ profil dans le layout modifie profil
        LayoutInflater factory = LayoutInflater.from(MesNotifications.this);
        View alertDialogView = factory.inflate(R.layout.popup_donneavis, null);
        final TextView TV_PSEUDO = (TextView) alertDialogView.findViewById(R.id.pseudo);
        final TextView TV_DATE = (TextView) alertDialogView.findViewById(R.id.dateactivite);
        final TextView TV_TITRE = (TextView) alertDialogView.findViewById(R.id.titre);
        final EditText ET_Commentaires = (EditText) alertDialogView.findViewById(R.id.commentaire);

        final ImageView photo = (ImageView) alertDialogView.findViewById(R.id.iconactivite);

        final RatingBar RT_NoteProfil = (RatingBar) alertDialogView.findViewById(R.id.note);

        final Switch SW_AjoutAmi = (Switch) alertDialogView.findViewById(R.id.ajoutAmi);

        if (isAmi) {
            SW_AjoutAmi.setChecked(true);
            SW_AjoutAmi.setVisibility(View.GONE);
        } else {
            SW_AjoutAmi.setChecked(true);
            SW_AjoutAmi.setVisibility(View.VISIBLE);
        }


        //   RT_NoteProfil = (RatingBar) findViewById(R.id.noteprofil);
        RT_NoteProfil.setMax(5);
        RT_NoteProfil.setNumStars(5);
        RT_NoteProfil.setStepSize(0.5f);
        RT_NoteProfil.setProgress(10);

        //**************************************

        TV_PSEUDO.setText(profil.getPseudo());
        TV_TITRE.setText(activite.getTitreUnicode());
        TV_DATE.setText(activite.getDatedebutStr());
        photo.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(),profil.getPhoto()));

        //  ET_Commentaires.setText(TV_Commentaires.getText().toString());// Charge avec la valeur du profil du TextView
        AlertDialog.Builder adb = new AlertDialog.Builder(MesNotifications.this);
        adb.setView(alertDialogView);
        adb.setTitle(R.string.pop_donneavis_titre);
        adb.setMessage(R.string.DescriptionProfil_Message);
        adb.setIcon(android.R.drawable.ic_menu_info_details);
        adb.setPositiveButton(R.string.pop_donneavis_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //    TV_Commentaires.setText(ET_Commentaires.getText().toString());
                boolean ami = SW_AjoutAmi.isChecked();
                String commentaires = (StringEscapeUtils.escapeJava(ET_Commentaires.getText().toString())).trim();
                float note = RT_NoteProfil.getRating();
                notePersonne(ami, idpersonnenotee, idactivite, commentaires, note);

            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        adb.setNegativeButton(R.string.pop_donneavis_No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Lorsque l'on cliquera sur annuler on quittera l'application

            }
        });
        AlertDialog alertDialog = adb.create();
        alertDialog.show();
        Button buttonNon = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNon.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Fondbutton));
        buttonNon.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Textbutton));
        Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOui.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Fondbutton));
        buttonOui.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Textbutton));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 0, 15, 0);
        buttonOui.setLayoutParams(params);

    }

    private void ouvreAvis() {
        Intent appel;
        Outils.fermeActiviteEnCours(this);
        appel = new Intent(MesNotifications.this,
                UnProfil.class);
        appel.putExtra("idpersonne", Outils.personneConnectee.getId());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);

    }

    private void getNextNotification(boolean visibleProgressBar) {
        new AsyncTaches.AsyncGetNextListNotification(this, Outils.personneConnectee.getId(), idxnotification, visibleProgressBar,MesNotifications.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void acquitNotification() {
        new AsyncTaches.AsyncAcquitNotification(this, Outils.personneConnectee.getId(), idnotificationselection, MesNotifications.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void loopBack_AcquitTouteNotification(MessageServeur messageserveur) {

    }

    @Override
    public void loopBack_AcquitNotification(MessageServeur messageserveur) {

        if (messageserveur != null) {

            Iterator<Notification> it = listeNotification.iterator();
            while (it.hasNext()) {
                Notification notification = it.next();
                if (notification.getIdnotification() == idnotificationselection)
                    notification.setLu(true);
            }
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void loopBack_EffaceNotification(MessageServeur result) {
        if (result != null) {
            Iterator<Notification> it = listeNotification.iterator();
            while (it.hasNext()) {
                Notification notification = it.next();
                if (notification.getIdnotification() == idnotificationselection) it.remove();
            }
            adapter.notifyDataSetChanged();

        }

        gestionDefaultLMessage();// Affiche le message par défaut

    }

    @Override
    public void loopBack_GetListNotification(ArrayList<Notification> result) {

        if (result != null) {


            listeNotification.clear();
            listeNotification.addAll(result);
            if (listeNotification.size() > 0)
                idxnotification = listeNotification.get(0).getIdnotification();
            adapter.notifyDataSetChanged();

        }

        Collections.sort(listeNotification, new ComparatorTempsNotification());
        gestionDefaultLMessage();// Affiche le message par défaut
    }

    @Override
    public void loopBack_GetNextListNotification(ArrayList<Notification> result) {

        if (result != null) {


            for (Notification notification : result) {
                if (notification.getIddestinataire() == Outils.personneConnectee.getId()) {// Verifie que la notification est bien pour le destinataire

                    boolean ajout = true;

                    for (Notification notificationExistant : listeNotification) {// Verifie que le message n'existe pas peut se produire si les 2 threads sont lancé en même temps
                        if (notificationExistant.getIdnotification() == notification.getIdnotification())// Si le message existe ajoute=false
                            ajout = false;
                    }

                    if (ajout) {
                        listeNotification.add(0, notification);
                        idxnotification = notification.getIdnotification();

                    }
                }

            }
            swipeContainer.setRefreshing(false);
            adapter.notifyDataSetChanged();

        }
        Collections.sort(listeNotification, new ComparatorTempsNotification());
        gestionDefaultLMessage();// Affiche le message par défaut
    }


    private void dialogEffaceMessage(Notification item) {
        idnotificationselection = item.getIdnotification();

        AlertDialog.Builder builder = new AlertDialog.Builder(MesNotifications.this);
        builder.setTitle(R.string.SupprimeNotification_Titre);
        builder.setMessage(R.string.SupprimeNotification_Message);
        builder.setPositiveButton(R.string.SupprimeNotification_OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                // ************************************************

                effaceNotification();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.SupprimeNotification_No, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button buttonNon = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNon.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Fondbutton));
        buttonNon.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Textbutton));
        Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOui.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Fondbutton));
        buttonOui.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Textbutton));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 0, 15, 0);
        buttonOui.setLayoutParams(params);
    }

    private void effaceNotification() {
        new AsyncTaches.AsyncEffaceNotification(this, Outils.personneConnectee.getId(), idnotificationselection, MesNotifications.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;

        if (requestCode == RETOUR_NOTATION) {
            boolean supprimenotification = data.getBooleanExtra("supprime", false);

            if (supprimenotification) {
                int idactivite = data.getIntExtra("idactivite", 0);
                int idpersonnenotee = data.getIntExtra("idpersonnenotee", 0);
                Iterator<Notification> it = listeNotification.iterator();
                while (it.hasNext()) {
                    Notification notification = it.next();
                    if (notification.getIdactivite() == idactivite && notification.getIdpersonne() == idpersonnenotee
                            && notification.getIdtypenotification() == Notification.DONNE_AVIS)
                        it.remove();
                }

                adapter.notifyDataSetChanged();

            }
        }
    }


    protected void onDestroy() {
        new AsyncTaches.AsyncAcquitAllNotification(this, Outils.personneConnectee.getId(), MesNotifications.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Outils.LOOP_BACK_RECEIVER_GCM.removeGCMMessageListener(this);
        super.onDestroy();
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

            case PushAndroidMessage.UPDATE_NOTIFICATION:
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.

                getNextNotification(false);
                break;

        }
    }

    private void gestionDefaultLMessage() {// Permet d'afficher un message si le nbr de message est null

        TextView TV_MessageDefaut = (TextView) findViewById(R.id.id_messagebalise);
        ListView LV_ListAmis = (ListView) findViewById(R.id.LV_listeMessages);
        if (listeNotification != null) {
            if (listeNotification.isEmpty()) {
                TV_MessageDefaut.setText(R.string.MesNotifications_noResutat);
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
    public void loopBack_GetDonneAvisFull(ProfilNotation profil, Activite activite) {

        if (activite != null && profil != null) {
            isAmi = profil.isAmi();
            ouvrePopupNotation(profil, activite);
        }

    }

    private void notePersonne(boolean ami, int idpersonnenotee, int idactivite, String commentaires, float note) {

        new AsyncTaches.AsyncAddNotation(this, idpersonnenotee, idactivite, commentaires, note, ami, MesNotifications.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loopBack_AddNotation(MessageServeur messageserveur) {

        if (messageserveur != null) {

            if (messageserveur.isReponse()) {

                Toast toast = Toast.makeText(MesNotifications.this, messageserveur.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                Iterator<Notification> it = listeNotification.iterator();
                while (it.hasNext()) {
                    Notification notification = it.next();
                    if (notification.getIdactivite() == idactivite && notification.getIdpersonne() == idpersonnenotee
                            && notification.getIdtypenotification() == Notification.DONNE_AVIS)
                        it.remove();
                }

                adapter.notifyDataSetChanged();


            } else {
                Toast toast = Toast.makeText(MesNotifications.this, messageserveur.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        } else {

            Toast toast = Toast.makeText(MesNotifications.this, messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }
        gestionDefaultLMessage();
    }
}
