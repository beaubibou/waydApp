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
import com.squareup.picasso.Picasso;
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

public class DetailActiviteCARPEDIEM extends MenuDrawerNew implements
        AsyncTaches.AsyncGetActiviteFull.Async_GetActiviteFullListener,
        AsyncTaches.AsyncUpdateActivite.AsyncUpdateActiviteListener, ReceiverGCM.GCMMessageListener, AsyncTaches.AsyncAddInteret.Async_AddInteretListener {

    private int idactivite;
    private ImageView photop;
    private TextView TV_pseudo;
    private TextView TV_description;
    private TextView TV_Titre;
    private TextView TV_Horaire, TV_SignalerActivite;
    private Activite activiteSelectionne;
    private ImageView iconActivite;
    private ImageButton IB_Map;
    private Button B_Interet;
     private SwipeRefreshLayout swipeContainer;
    TwoWayView LV_PhotoActivite ;
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
       //  photop = (ImageView) findViewById(R.id.iconactivite);
      //  TV_pseudo = (TextView) findViewById(R.id.pseudo);
        TV_description = (TextView) findViewById(R.id.description);
     //   TV_Titre = (TextView) findViewById(R.id.titre);
     //   TV_Horaire = (TextView) findViewById(R.id.horaire);
     //   IB_Map = (ImageButton) findViewById(R.id.map);


        iconActivite = (ImageView) findViewById(R.id.iconActivite);
      //  Picasso.with(getBaseContext()).load("http://lyon.carpediem.cd/data/afisha/bp/9c/03/9c0302e834.jpg").into(iconActivite);



         new AsyncTaches.AsyncGetActiviteFull(this, idactivite, true, DetailActiviteCARPEDIEM.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      //  Outils.LOOP_BACK_RECEIVER_GCM.addGCMMessageListener(this);
      //  TV_description.setMovementMethod(new ScrollingMovementMethod());
      //  getIntent().putExtra("refresh", false);
      //  setResult(1020, getIntent());





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

            activiteSelectionne = activite;
            iconActivite.setImageBitmap( activite.getPhoto());
            TV_description.setText(convertLibelleActivite(activite.getLibelleUnicode()));


        }


    }



    public String convertLibelleActivite(String libelle) {

        if (libelle == null || libelle.length() == 0)

            return (getString(R.string.s_detail_pas_detail_activite));

        return libelle;
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



    @Override
    public void loopBack_UpdateActivite(MessageServeur messageserveur, String titre, String libelle, int maxWaydeurs) {

    }

    @Override
    public void loopBackReceiveGCM(Bundle bundle) {

    }
}
