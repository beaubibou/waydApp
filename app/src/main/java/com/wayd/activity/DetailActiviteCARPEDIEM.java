package com.wayd.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.Participant;
import com.wayd.bean.PushAndroidMessage;
import com.wayd.bean.ReceiverGCM;
import com.wayd.listadapter.PhotoActiviteAdapter;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class DetailActiviteCARPEDIEM extends MenuDrawerNew implements
        AsyncTaches.AsyncGetActiviteFull.Async_GetActiviteFullListener,
        AsyncTaches.AsyncUpdateActivite.AsyncUpdateActiviteListener, ReceiverGCM.GCMMessageListener, AsyncTaches.AsyncAddInteret.Async_AddInteretListener {  private int idactivite;
    private ImageView photop;
    private TextView TV_pseudo;
    private TextView TV_description;
    private TextView TV_Titre;
    private TextView TV_Horaire, TV_Adresse;
    private Activite activiteSelectionne;
    private ImageView iconActivite;
    private ImageButton IB_Map;
    private Button B_Interet;
    private TextView B_LienFaceBook;
    public static final int ACTION_DETAIL_ACTIVITE = 1021;
    public final static int ACTION_MODIFIEE_ACTIVITE = 2;
    public final static String ACTION = "action";
    private SwipeRefreshLayout swipeContainer;
    TwoWayView LV_PhotoActivite ;
    private RelativeLayout imageFond;
    private PhotoActiviteAdapter photoActiviteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailactivitecarpdiem);
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
        B_LienFaceBook = (TextView) findViewById(R.id.lienfacebook);
        TV_Adresse = (TextView) findViewById(R.id.adresse);
        IB_Map = (ImageButton) findViewById(R.id.map);
        imageFond = (RelativeLayout) findViewById(R.id.rlbandeauhaut);




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

        new AsyncTaches.AsyncGetActiviteFull(this, idactivite, true, DetailActiviteCARPEDIEM.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                (this, Outils.personneConnectee.getId(), idactivite, 0, DetailActiviteCARPEDIEM.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void getActivite(boolean afficheProgress) {
        new AsyncTaches.AsyncGetActiviteFull(this, idactivite, afficheProgress, DetailActiviteCARPEDIEM.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        swipeContainer.setRefreshing(false);
    }


    @Override
    public void loopBack_GetActiviteFull(final Activite activite, ArrayList<Participant> vlistParticipant) {

        if (activite == null) {
            Toast toast = Toast.makeText(DetailActiviteCARPEDIEM.this, R.string.activiteSupprimee, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();

        } else

        {
            B_Interet.setEnabled(!activite.isInteret());
            activiteSelectionne = activite;
          //  photop.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(), activite.getPhoto()));
            TV_pseudo.setText(activite.getPseudoOrganisateur());

            TV_description.setText(activite.getFulldescription().replace("&#039;","'").trim());
            TV_Titre.setText(activite.getTitre().replace("&#039;","'"));
            TV_Horaire.setText(activite.getHoraire());
            iconActivite.setImageResource(Outils.getActiviteMipMap(activite.getIdTypeActite(), activite.getTypeUser()));
            imageFond.setBackground(new BitmapDrawable(getResources(), activite.getPhoto()));
            TV_Adresse.setText(activite.getAdresse().replace("&#039;","'"));


            TV_pseudo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DetailActivitePro.this", "click icon");
                    Intent appel = new Intent(DetailActiviteCARPEDIEM.this, UnProfilPro.class);
                    appel.putExtra("idpersonne", activiteSelectionne.getIdorganisateur());
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(appel);

                }
            });

            IB_Map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent appel;
                    appel = new Intent(DetailActiviteCARPEDIEM.this, Map_MontreActivite.class);
                    appel.putExtra("latitude", activite.getLatitude());
                    appel.putExtra("longitude", activite.getLongitude());
                    appel.putExtra("typeActivite", activite.getIdTypeActite());
                    appel.putExtra("typeUser", activite.getTypeUser());

                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(appel);
                }
            });

            final String lienFacebook=activite.getLienfacebook();

            if (lienFacebook==null)
                B_LienFaceBook.setVisibility(View.INVISIBLE);
            else
                B_LienFaceBook.setVisibility(View.VISIBLE);

            B_LienFaceBook.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {

                                                            String url = activite.getLienfacebook();
                                                      Intent i = new Intent(Intent.ACTION_VIEW);
                                                      i.setData(Uri.parse(url));
                                                      startActivity(i);
                                                  }
                                              }
            );

        }

    }

    public String convertLibelleActivite(String libelle) {

        if (libelle == null || libelle.length() == 0)

            return (getString(R.string.s_detail_pas_detail_activite));

        return libelle;
    }




    @Override
    public void loopBack_UpdateActivite(MessageServeur messageserveur, String titre, String libelle, int nbMaxWaydeurs) {
        //Si la mise à jour de l'activite est réussie
        if (messageserveur != null) {
            if (messageserveur.isReponse()) {

                TV_Titre.setText(titre);
                TV_description.setText(convertLibelleActivite(libelle));

                Toast toast = Toast.makeText(DetailActiviteCARPEDIEM.this, messageserveur.getMessage(), Toast.LENGTH_LONG);
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
            Toast toast = Toast.makeText(DetailActiviteCARPEDIEM.this, messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }
    }


}
