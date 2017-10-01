package com.wayd.main;

import com.application.wayd.R;

import com.wayd.activity.MenuDrawerNew;
import com.wayd.activity.MyLifecycleHandler;
import com.wayd.activity.ProposeActivites;
import com.wayd.activity.ProposeActivitesPro;
import com.wayd.activity.RechercheActiviteNew;
import com.wayd.activity.Statistique;


import com.wayd.bean.GPSTracker;

import com.wayd.bean.Outils;
import com.wayd.bean.Profil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends MenuDrawerNew implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getApplication(). registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        Outils.principal = this;// Declare l'activité principal. Pour la pointer pour pouvoir la fermer lors de la deconnexion
        setContentView(R.layout.menu_principal);

        if (Outils.gps==null)
        Outils.gps = new GPSTracker(getBaseContext(), MainActivity.this);
        else
        {


            Outils.gps.setActivite( MainActivity.this);

        }

        if (!Outils.gps.canGetLocation())
            Outils.gps.showSettingsAlert(getBaseContext(), MainActivity.this);

        Outils.gps.clearListener();
        Outils.gps.addListenerGPS(Outils.personneConnectee);
        if (Outils.gps.canGetLocation()) {
            Outils.personneConnectee.setPositionGps(Outils.gps.getLatitude(), Outils.gps.getLongitude());
            Outils.personneConnectee.setGps(true);
        }

        InitDrawarToolBar();
        initTableauDeBord();
        Outils.LOOP_BACK_RECEIVER_GCM.addGCMMessageListener(Outils.tableaudebord);
        Button b_proposerActivite = (Button) findViewById(R.id.propose);
        Button b_administrateur = (Button) findViewById(R.id.admin);

        // ***********************************Gestion lors du chargement d'une personne*****************
        b_proposerActivite.setOnClickListener(this);
        Button b_rechercherActivite = (Button) findViewById(R.id.recherche);
        Button b_ennuie = (Button) findViewById(R.id.ennuie);
        b_rechercherActivite.setOnClickListener(this);
        b_ennuie.setOnClickListener(this);

       //************** Gestion du mode administrateur***********************

       // Outils.personneConnectee.setAdmin(true);
        if (Outils.personneConnectee.isAdmin()){
           b_administrateur.setVisibility(View.VISIBLE);
           b_administrateur.setOnClickListener(this);

        }
        //**************************************************************

    }

    protected void onPause() {
     //   Outils.gps.stopUsingGPS();
        super.onPause();
    }

    protected void onResume() {
    //    Outils.gps.getLocation();
        super.onResume();

    }

    protected void onDestroy() {
        Outils.LOOP_BACK_RECEIVER_GCM.removeGCMMessageListener(Outils.tableaudebord);
        Outils.gps.removeListenerGPS(Outils.personneConnectee);
        Outils.fermeActiviteEnCours(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent appel;

        switch (v.getId()) {

            case R.id.propose:

                switch (Outils.personneConnectee.getTypeUser()){

                    case Profil.WAYDEUR:
                        appel = new Intent(MainActivity.this,
                                ProposeActivites.class);
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(appel);
                        break;

                    case Profil.PRO:
                        appel = new Intent(MainActivity.this,
                                ProposeActivitesPro.class);
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(appel);
                        break;



                }



                break;


            case R.id.recherche:
                appel = new Intent(MainActivity.this,
                        RechercheActiviteNew.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                break;

            case R.id.ennuie:

                appel = new Intent(MainActivity.this,
                        RechercheActiviteNew.class);
                appel.putExtra("ennuie", true);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
             //   Outils.afficheTuto(MainActivity.this, TutorielBean.ENNUIE);
                break;

           case R.id.admin:
                appel = new Intent(MainActivity.this,
                        Statistique.class);
               appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                 break;

        }

    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);


        } else {

            // moveTaskToBack(true);  // "Hide" your current Activity

        }


    }


}