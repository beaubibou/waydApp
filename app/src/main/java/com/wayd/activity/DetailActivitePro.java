package com.wayd.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.Participant;
import com.wayd.bean.PhotoActivite;
import com.wayd.bean.PushAndroidMessage;
import com.wayd.bean.ReceiverGCM;
import com.wayd.listadapter.PhotoActiviteAdapter;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class DetailActivitePro extends MenuDrawerNew implements
        AsyncTaches.AsyncGetActiviteFull.Async_GetActiviteFullListener,
        AsyncTaches.AsyncUpdateActivite.AsyncUpdateActiviteListener, ReceiverGCM.GCMMessageListener, AsyncTaches.AsyncAddInteret.Async_AddInteretListener, AsyncTaches.AsyncGetListPhotoActivite.Async_GetListPhotoActiviteListener {

    private int idactivite;
    private ImageView photop;
    private TextView TV_pseudo;
    private TextView TV_description;
    private TextView TV_Titre,TV_Adresse;
    private TextView TV_Horaire, TV_SignalerActivite;
    private Activite activiteSelectionne;
    private ImageView iconActivite;
    private ImageButton IB_Map;
    private Button B_Interet;
    public static final int ACTION_DETAIL_ACTIVITE = 1021;
    public final static int ACTION_MODIFIEE_ACTIVITE = 2;
    public final static String ACTION = "action";
    private SwipeRefreshLayout swipeContainer;
    TwoWayView LV_PhotoActivite ;
    private PhotoActiviteAdapter photoActiviteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailactivitepro);
        InitDrawarToolBar();
        initTableauDeBord();
        idactivite = getIntent().getIntExtra("idactivite", 0);
        // verifie que la donéne ne vient pas de la notification
        if (getIntent().getStringExtra("idactiviteFromNotification") != null) {
            idactivite = Integer.valueOf(getIntent().getStringExtra("idactiviteFromNotification"));

        }
        photop = (ImageView) findViewById(R.id.iconactivite);
        TV_pseudo = (TextView) findViewById(R.id.pseudo);
        TV_description = (TextView) findViewById(R.id.description);
        TV_Titre = (TextView) findViewById(R.id.titre);
        TV_Horaire = (TextView) findViewById(R.id.horaire);
        B_Interet = (Button) findViewById(R.id.interet);
        TV_SignalerActivite = (TextView) findViewById(R.id.signaleractivite);
        IB_Map = (ImageButton) findViewById(R.id.map);
        TV_Adresse = (TextView) findViewById(R.id.adresse);


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

        new AsyncTaches.AsyncGetActiviteFull(this, idactivite, true, DetailActivitePro.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Outils.LOOP_BACK_RECEIVER_GCM.addGCMMessageListener(this);
        TV_description.setMovementMethod(new ScrollingMovementMethod());
        getIntent().putExtra("refresh", false);
        setResult(1020, getIntent());


        B_Interet.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             addInteret();

                                         }
                                     }
        );

        LV_PhotoActivite = (TwoWayView) findViewById(R.id.photoactivite);

    }

    private void addInteret() {

        new AsyncTaches.AsyncAddInteret
                (this, Outils.personneConnectee.getId(), idactivite, 0, DetailActivitePro.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void getActivite(boolean afficheProgress) {
        new AsyncTaches.AsyncGetActiviteFull(this, idactivite, afficheProgress, DetailActivitePro.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        swipeContainer.setRefreshing(false);
    }


    @Override
    public void loopBack_GetActiviteFull(final Activite activite, ArrayList<Participant> vlistParticipant) {

        if (activite == null) {
            Toast toast = Toast.makeText(DetailActivitePro.this, R.string.activiteSupprimee, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();

        } else

        {
            B_Interet.setEnabled(!activite.isInteret());
            activiteSelectionne = activite;
            photop.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(), activite.getPhoto()));
            TV_pseudo.setText(activite.getPseudoOrganisateur());
            TV_description.setText(convertLibelleActivite(activite.getLibelleUnicode()));
            TV_Titre.setText(activite.getTitreUnicode());
            TV_Horaire.setText(activite.getHoraire());
            TV_Adresse.setText(activite.getAdresse());
            iconActivite.setImageResource(Outils.getActiviteMipMap(activite.getIdTypeActite(), activite.getTypeUser()));
            Log.d("DetailActivitePro.this", "clik incon");
            photop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DetailActivitePro.this", "click icon");
                    Intent appel = new Intent(DetailActivitePro.this, UnProfilPro.class);
                    appel.putExtra("idpersonne", activiteSelectionne.getIdorganisateur());
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(appel);

                }
            });


            TV_pseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DetailActivitePro.this", "click icon");
                Intent appel = new Intent(DetailActivitePro.this, UnProfilPro.class);
                appel.putExtra("idpersonne", activiteSelectionne.getIdorganisateur());
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);

            }
        });

            IB_Map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent appel;
                    appel = new Intent(DetailActivitePro.this, Map_MontreActivite.class);
                    appel.putExtra("latitude", activite.getLatitude());
                    appel.putExtra("longitude", activite.getLongitude());
                    appel.putExtra("typeActivite", activite.getIdTypeActite());
                    appel.putExtra("typeUser", activite.getTypeUser());

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

            new AsyncTaches.AsyncGetListPhotoActivite(this, idactivite,DetailActivitePro.this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    }

    public String convertLibelleActivite(String libelle) {

        if (libelle == null || libelle.length() == 0)

            return (getString(R.string.s_detail_pas_detail_activite));

        return libelle;
    }

    private void signaleActivite() {
        Intent appel = new Intent(DetailActivitePro.this, SignalerActivitePro.class);
        appel.putExtra("idactivite", activiteSelectionne.getId());
        appel.putExtra("titreActivite", activiteSelectionne.getTitre());
        appel.putExtra("libelleActivite", activiteSelectionne.getLibelle());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);
    }


    @Override
    public void loopBack_UpdateActivite(MessageServeur messageserveur, String titre, String libelle, int nbMaxWaydeurs) {
        //Si la mise à jour de l'activite est réussie
        if (messageserveur != null) {
            if (messageserveur.isReponse()) {

                TV_Titre.setText(titre);
                TV_description.setText(convertLibelleActivite(libelle));

                Toast toast = Toast.makeText(DetailActivitePro.this, messageserveur.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                getIntent().putExtra("refresh", true);
                getIntent().putExtra(ACTION, ACTION_MODIFIEE_ACTIVITE);
                getIntent().putExtra("idactivite", activiteSelectionne.getId());
                setResult(ACTION_DETAIL_ACTIVITE, getIntent());

            }


        }

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

    @Override
    public void loopBack_AddInteret(MessageServeur messageserveur) {

        if (messageserveur != null) {
            B_Interet.setEnabled(false);
            Toast toast = Toast.makeText(DetailActivitePro.this, messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }
    }

    @Override
    public void loopBack_GetListPhotoActivite(ArrayList<PhotoActivite> listPhoto) {
        // Appellé aprés le chargement de l'activité pour ne pas figer l'ecran

        photoActiviteAdapter=new PhotoActiviteAdapter(getBaseContext(),listPhoto);
        LV_PhotoActivite.setAdapter(photoActiviteAdapter);
        photoActiviteAdapter.notifyDataSetChanged();
    }
}
