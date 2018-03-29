package com.wayd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.Participant;
import com.wayd.bean.PushAndroidMessage;
import com.wayd.bean.ReceiverGCM;
import com.wayd.listadapter.ParticipantAdapter;


import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;

public class DetailActivite extends MenuDrawerNew implements
        AsyncTaches.AsyncGetActiviteFull.Async_GetActiviteFullListener,
        AsyncTaches.AsyncAddParticipation.Async_AddParticipationListener,
        AsyncTaches.AsyncEffaceActivite.Async_EffaceActiviteListener,
        AsyncTaches.AsyncEffaceParticipation.Async_EffaceParticipationListener,
        ParticipantAdapter.ParticipantAdapterListener, AsyncTaches.AsyncUpdateActivite.AsyncUpdateActiviteListener, ReceiverGCM.GCMMessageListener {

    private int idactivite;
    private ImageView photop;
    private TextView TV_age;
    private TextView TV_pseudo;
    private TextView TV_sexe;
    private TextView TV_description;
    private TextView TV_Titre;
    private TextView TV_TermineDans,TV_Adresse;
    private TextView TV_NbrInscrit, TV_SignalerActivite;
    private RatingBar ratingBar;
    private Activite activiteSelectionne;
    private ImageView iconActivite;
    private ImageButton IB_Inscription, IB_Map, IB_Message;
    public static final int ACTION_DETAIL_ACTIVITE = 1021;
    public final static int ACTION_REMOVE_PARTICIPATION = 0;
    public final static int ACTION_REMOVE_ACTIVITE = 1;
    public final static int ACTION_MODIFIEE_ACTIVITE = 2;

    public final static String ACTION = "action";
    private ParticipantAdapter participantAdapter;
    private final ArrayList<Participant> listparticipant = new ArrayList<>();
    private Participant participantAeffacer;
    private SwipeRefreshLayout swipeContainer;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailactivite);
        InitDrawarToolBar();
        initTableauDeBord();


        idactivite = getIntent().getIntExtra("idactivite", 0);
        // verifie que la donéne ne vient pas de la notification

        if (getIntent().getStringExtra("idactiviteFromNotification") != null) {
            idactivite = Integer.valueOf(getIntent().getStringExtra("idactiviteFromNotification"));

        }
        photop = (ImageView) findViewById(R.id.iconactivite);
        TV_pseudo = (TextView) findViewById(R.id.pseudo);
        TV_age = (TextView) findViewById(R.id.age);
        TV_sexe = (TextView) findViewById(R.id.sexe);
        ratingBar = (RatingBar) findViewById(R.id.noteprofil);
        TV_description = (TextView) findViewById(R.id.description);
        TV_Titre = (TextView) findViewById(R.id.titre);
        TV_Adresse = (TextView) findViewById(R.id.adresse);
        TV_TermineDans = (TextView) findViewById(R.id.terminedans);
        TV_NbrInscrit = (TextView) findViewById(R.id.nbrinscrit);
        TV_SignalerActivite = (TextView) findViewById(R.id.signaleractivite);
        IB_Inscription = (ImageButton) findViewById(R.id.inscription);
        IB_Map = (ImageButton) findViewById(R.id.map);
        IB_Message = (ImageButton) findViewById(R.id.messageami);
        participantAdapter = new ParticipantAdapter(getBaseContext(), listparticipant);
        TwoWayView LV_Participant = (TwoWayView) findViewById(R.id.listeparticipant);

        scrollView = (ScrollView) findViewById(R.id.scroll);
        LV_Participant.setAdapter(participantAdapter);

        iconActivite = (ImageView) findViewById(R.id.iconActivite);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivite(true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        new AsyncTaches.AsyncGetActiviteFull(this, idactivite, true, DetailActivite.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Outils.LOOP_BACK_RECEIVER_GCM.addGCMMessageListener(this);
        TV_description.setMovementMethod(new ScrollingMovementMethod());
        getIntent().putExtra("refresh", false);
        setResult(1020, getIntent());


    }

    private void ouvrePopUpModifier() {// Ouvre le popup pour la saisie du profil

        // Récupere le layour et le champ profil dans le layout modifie profil
        LayoutInflater factory = LayoutInflater.from(DetailActivite.this);
        View alertDialogView = factory.inflate(R.layout.popup_modifieractivite, null);
        final EditText ET_Description = (EditText) alertDialogView.findViewById(R.id.commentaire);
        final EditText ET_Titre = (EditText) alertDialogView.findViewById(R.id.dateactivite);
        final Spinner SP_MaxWaydeurs = (Spinner) alertDialogView.findViewById(R.id.nbMaxWaydeurs);

        List<String> ListnbrParticipant = new ArrayList<>();
        int nbrParticipant = listparticipant.size();
        if (nbrParticipant == 1) nbrParticipant = 2;

        for (int f = nbrParticipant; f < 10; f++)
            ListnbrParticipant.add("" + f);

        ArrayAdapter<String> dureeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ListnbrParticipant);
        SP_MaxWaydeurs.setAdapter(dureeAdapter);

        //  for (int f =0  ; f < 10-nbrParticipant; f++){
        //  if ((Integer.parseInt(SP_MaxWaydeurs.getItemAtPosition(f).toString())==activiteSelectionne.getNbmaxwaydeur()))  {
        Log.d("detailActivite", "" + (activiteSelectionne.getNbmaxwaydeur() - nbrParticipant));
        SP_MaxWaydeurs.setSelection(activiteSelectionne.getNbmaxwaydeur() - nbrParticipant);

        dureeAdapter.notifyDataSetChanged();
        //  }

        //  }


        //**************************************
        ET_Description.setText(TV_description.getText().toString());// Charge avec la valeur de la description
        ET_Titre.setText(TV_Titre.getText().toString());// Charge avec la valeur de la description

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(alertDialogView);
        adb.setTitle(R.string.popup_modificationActivite_titre);
        adb.setIcon(android.R.drawable.ic_menu_edit);
        adb.setPositiveButton(R.string.popup_modificationActivite_Sauve, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String libelle = ET_Description.getText().toString().trim();
                String titre = ET_Titre.getText().toString().trim();
                if (libelle.isEmpty()) libelle = " ";
                if (titre.isEmpty()) {
                    messageErreurModificationTitreVide();
                    return;
                }
                int nbmaxwaydeur = Integer.parseInt((String) SP_MaxWaydeurs.getSelectedItem());
                updateActivite(Outils.personneConnectee.getId(), activiteSelectionne, titre, libelle, nbmaxwaydeur);


            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        adb.setNegativeButton(R.string.popup_modificationActivite_Annule, new DialogInterface.OnClickListener() {
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

    private void messageErreurModificationTitreVide() {
        Toast toast = Toast.makeText(getBaseContext(), R.string.proposeactivite_err_titre, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void messageErreurModificationTitreMax() {
        Toast toast = Toast.makeText(getBaseContext(), R.string.proposeactivite_err_titreLong, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void updateActivite(int idpersonne, Activite activiteSelectionne, String titre, String libelle, int nbmaxWaydeurs) {
        new AsyncTaches.AsyncUpdateActivite(this, idpersonne, activiteSelectionne.getId(), titre, libelle, nbmaxWaydeurs, DetailActivite.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void getActivite(boolean afficheProgress) {
        new AsyncTaches.AsyncGetActiviteFull(this, idactivite, afficheProgress, DetailActivite.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        swipeContainer.setRefreshing(false);
    }

    private void gestionArchive() {
        IB_Message.setImageResource(R.mipmap.ic_envelloppenok);
        IB_Inscription.setImageResource(R.mipmap.ic_close);
    }

    @Override
    public void loopBack_AddParticipation(MessageServeur messageserveur) {

        if (messageserveur.isReponse()) {
            Toast toast = Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            //   Outils.fermeActiviteEnCours(this);

            new AsyncTaches.AsyncGetActiviteFull(this, idactivite, true, DetailActivite.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            Toast toast = Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    @Override
    public void loopBack_EffaceActivite(MessageServeur messageserveur) {
        Toast toast = null;
        if (messageserveur.isReponse()) {
            toast = Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            getIntent().putExtra("refresh", true);
            getIntent().putExtra(ACTION, ACTION_REMOVE_ACTIVITE);
            getIntent().putExtra("idactivite", idactivite);
            setResult(ACTION_DETAIL_ACTIVITE, getIntent());
            finish();
        } else {
            toast = Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    public void loopBack_EffaceParticipation(MessageServeur messageserveur) {
        if (messageserveur.isReponse()) {

            if (Outils.personneConnectee.getId() == activiteSelectionne.getIdorganisateur()) {// si je suis l'organisateur je modfie la liste des participants
                listparticipant.remove(participantAeffacer);
                participantAdapter.notifyDataSetChanged();
                getIntent().putExtra("refresh", true);
                getIntent().putExtra(ACTION, ACTION_REMOVE_PARTICIPATION);
                getIntent().putExtra("idactivite", activiteSelectionne.getId());
                setResult(ACTION_DETAIL_ACTIVITE, getIntent());

            } else {
                //Sinon je ferme l'activite
                Toast toast = Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                getIntent().putExtra("refresh", true);
                getIntent().putExtra(ACTION, ACTION_REMOVE_ACTIVITE);
                getIntent().putExtra("idactivite", idactivite);
                setResult(ACTION_DETAIL_ACTIVITE, getIntent());

                finish();

            }


        } else {
            Toast toast = Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    private void gestionInscription() {

        IB_Inscription.setVisibility(View.VISIBLE);
        IB_Inscription.setImageResource(R.mipmap.ic_checkcouleur);
        IB_Message.setImageResource(R.mipmap.ic_envelloppenok);
        IB_Map.setImageResource(R.mipmap.ic_gpsok);
        IB_Message.setOnClickListener(null);

        IB_Inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivite.this);
                builder.setTitle(R.string.ConfirmeParticipation_Titre);
                builder.setMessage(R.string.ConfirmeParticipation_Message);
                builder.setPositiveButton(R.string.ConfirmeParticipation_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        participeActivite();
                    }
                });
                builder.setNegativeButton(R.string.ConfirmeParticipation_No, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
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


        });


    }

    private void participeActivite() {
        new AsyncTaches.AsyncAddParticipation(this, activiteSelectionne.getIdorganisateur(), activiteSelectionne.getId(), DetailActivite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        getIntent().putExtra("refresh", true);
        setResult(1020, getIntent());
    }

    private void gestionOrganisateur() {

        FloatingActionButton FloatButton_modifieActivite = (FloatingActionButton) findViewById(R.id.modifActivite);
        FloatButton_modifieActivite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrePopUpModifier();
            }
        });
        FloatButton_modifieActivite.setVisibility(View.VISIBLE);

        IB_Inscription.setImageResource(R.mipmap.ic_uncheckcouleur);
        IB_Inscription.setVisibility(View.VISIBLE);
        IB_Message.setImageResource(R.mipmap.ic_envelloppeok);
        IB_Map.setImageResource(R.mipmap.ic_gpsok);
        IB_Inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivite.this);
                builder.setTitle(R.string.AnnuleSonActivite_Titre);
                builder.setMessage(R.string.AnnuleSonActivite_Message);
                builder.setPositiveButton(R.string.AnnuleSonActivite_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        annuleActivite();
                    }
                });
                builder.setNegativeButton(R.string.AnnuleSonActivite_No, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
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


        });

        IB_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appel = new Intent(DetailActivite.this, MesMessagesActvite.class);
                appel.putExtra("idactivite", activiteSelectionne.getId());
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                finish();//
            }
        });

    }

    private void annuleActivite() {

        new AsyncTaches.AsyncEffaceActivite(this, Outils.personneConnectee.getId(), idactivite, DetailActivite.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    private void gestionDejaInscrit() {
        IB_Inscription.setImageResource(R.mipmap.ic_uncheckcouleur);
        IB_Inscription.setVisibility(View.VISIBLE);

        IB_Message.setImageResource(R.mipmap.ic_envelloppeok);
        IB_Map.setImageResource(R.mipmap.ic_gpsok);

        IB_Inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivite.this);
                builder.setTitle(R.string.AnnuleSaParticipation_Titre);
                builder.setMessage(R.string.AnnuleSaParticipation_Message);
                builder.setPositiveButton(R.string.AnnuleSaParticipation_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        annuleParticipation();
                    }
                });
                builder.setNegativeButton(R.string.AnnuleSaParticipation_No, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
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


        });

        IB_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appel = new Intent(DetailActivite.this, MesMessagesActvite.class);
                appel.putExtra("idactivite", activiteSelectionne.getId());
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                finish();//
            }
        });

    }

    private void annuleParticipation() {

        new AsyncTaches.AsyncEffaceParticipation(this, Outils.personneConnectee.getId(), Outils.personneConnectee.getId(), idactivite, DetailActivite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // continue with delete
    }

    @Override
    public void loopBack_GetActiviteFull(final Activite activite, ArrayList<Participant> vlistParticipant) {

        if (activite == null) {
            Toast toast = Toast.makeText(DetailActivite.this, R.string.activiteSupprimee, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();

        } else

        {
            activiteSelectionne = activite;
            photop.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(), activite.getPhoto()));
            TV_pseudo.setText(activite.getPseudoOrganisateur());

            photop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DetailActivitePro.this","click icon");
                    Intent appel = new Intent(DetailActivite.this, UnProfil.class);
                    appel.putExtra("idpersonne", activiteSelectionne.getIdorganisateur());
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(appel);

                }
            });
            TV_age.setText(activite.getAgeStr());
            TV_sexe.setText(activite.getSexeOrganisateur());
            TV_description.setText(convertLibelleActivite(activite.getLibelleUnicode()));
            TV_Titre.setText(activite.getTitreUnicode());
            TV_Adresse.setText(activite.getAdresse());
            iconActivite.setImageResource(Outils.getActiviteMipMap(activite.getIdTypeActite(),activite.getTypeUser()));

            if (activite.getNbrparticipant() == activite.getNbmaxwaydeur()) {// Si l'acitivet complete
                TV_NbrInscrit.setText(R.string.s_detailactivite_complet);

            } else {
                TV_NbrInscrit.setText(getString(R.string.s_detailactivite_nbrinscrit) + activite.getNbrparticipant() + "/" + activite.getNbmaxwaydeur());
            }

            TV_TermineDans.setText(activite.getTpsrestant());
            ratingBar.setMax(5);
            ratingBar.setStepSize(0.5f);
            ratingBar.setRating((float) activite.getNote());

            if (activite.isArchive()) {

                gestionArchive();
            }
            if (activite.isDejainscrit() && !activite.isArchive())
                gestionDejaInscrit();

            if (activite.isOrganisateur(Outils.personneConnectee.getId()) && !activite.isArchive()) {
                gestionOrganisateur();
            }

            if (!activite.isDejainscrit() && !activite.isOrganisateur(Outils.personneConnectee.getId()) && !activite.isArchive())
                gestionInscription();

            // Activiet le bouton de la map aprés le chargement de l'activite

            IB_Map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent appel;
                    appel = new Intent(DetailActivite.this, Map_MontreActivite.class);
                    appel.putExtra("latitude", activite.getLatitude());
                    appel.putExtra("longitude", activite.getLongitude());
                    appel.putExtra("typeActivite",activite.getIdTypeActite());
                    appel.putExtra("typeUser",activite.getTypeUser());
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(appel);
                }
            });

            if (Outils.personneConnectee.getId() == activite.getIdorganisateur())
                TV_SignalerActivite.setVisibility(View.INVISIBLE);
            else
                TV_SignalerActivite.setVisibility(View.VISIBLE);

            TV_SignalerActivite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signaleActivite();
                }
            });

        }

        if (vlistParticipant != null) {
            listparticipant.clear();
            listparticipant.addAll(vlistParticipant);
            participantAdapter.notifyDataSetChanged();
            participantAdapter.addListener(this);
        }
        scrollView.scrollTo(0, 0);

    }

    private void signaleActivite() {
        Intent appel = new Intent(DetailActivite.this, SignalerActivite.class);
        appel.putExtra("idactivite", activiteSelectionne.getId());
        appel.putExtra("titreActivite", activiteSelectionne.getTitre());
        appel.putExtra("libelleActivite", activiteSelectionne.getLibelle());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);
    }

    @Override
    public void onLongClickPartiticipant(Participant participant, int position) {
        if (activiteSelectionne.getIdorganisateur() == Outils.personneConnectee.getId() && participant.getId() != activiteSelectionne.getIdorganisateur())
            dialogEffaceParticipant(participant);
    }

    @Override
    public void onClickParticipant(Participant participant, int position) {
        Intent appel;
        appel = new Intent(DetailActivite.this, UnProfil.class);
        Log.d("Detail activirt", "un");
        appel.putExtra("idpersonne", participant.getId());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);

    }

    private void dialogEffaceParticipant(final Participant participant) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivite.this);
        builder.setTitle(R.string.SupprimerParticipant_Titre);
        builder.setMessage(R.string.SupprimerParticipant_Message);
        builder.setPositiveButton(R.string.SupprimerParticipant_OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                effaceParticipant(participant);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.SupprimerParticipant_No, new DialogInterface.OnClickListener() {

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

    private void effaceParticipant(Participant participant) {
        participantAeffacer = participant;
        new AsyncTaches.AsyncEffaceParticipation(this, Outils.personneConnectee.getId(), participant.getId(), activiteSelectionne.getId(), DetailActivite.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loopBack_UpdateActivite(MessageServeur messageserveur, String titre, String libelle, int nbMaxWaydeurs) {
        //Si la mise à jour de l'activite est réussie


        if (messageserveur != null) {
            if (messageserveur.isReponse()) {

                TV_Titre.setText(titre);
                TV_description.setText(convertLibelleActivite(libelle));
                this.activiteSelectionne.setNbmaxwaydeur(nbMaxWaydeurs);
                TV_NbrInscrit.setText(getString(R.string.s_detailactivite_nbrinscrit) + activiteSelectionne.getNbrparticipant() + "/" + nbMaxWaydeurs);
                Toast toast = Toast.makeText(DetailActivite.this, messageserveur.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                getIntent().putExtra("refresh", true);
                getIntent().putExtra(ACTION, ACTION_MODIFIEE_ACTIVITE);
                getIntent().putExtra("idactivite", activiteSelectionne.getId());
                setResult(ACTION_DETAIL_ACTIVITE, getIntent());

            }


        }


    }


    public String convertLibelleActivite (String libelle){

        if (libelle==null ||libelle.length()==0)

            return (getString(R.string.s_detail_pas_detail_activite));

        return libelle;
    }
    @Override
    public void loopBackReceiveGCM(Bundle bundle) {
        if (bundle == null) return;

        int idMessageGcm = 0;

        try {

            idMessageGcm = Integer.parseInt(bundle.getString("id"));
        } catch (Exception e) {

            //   e.printStackTrace();
            return;
        }


        switch (idMessageGcm) {

            case PushAndroidMessage.UPDATE_ACTIVITE:
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                int idactivite = Integer.parseInt(bundle.getString("idactivite"));

                if (activiteSelectionne.getId() == idactivite) {
                    getActivite(false);
                }
        }

    }

    public void onDestroy() {

        Outils.LOOP_BACK_RECEIVER_GCM.removeGCMMessageListener(this);
        super.onDestroy();
    }
}
