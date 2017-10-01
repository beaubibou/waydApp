package com.wayd.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.Outils;
import com.wayd.bean.Profil;
import com.wayd.bean.PushAndroidMessage;
import com.wayd.bean.ReceiverGCM;
import com.wayd.comparator.ComparateurFinActivite;
import com.wayd.comparator.ComparatorDistanceActivite;
import com.wayd.listadapter.ActiviteAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class MesSuggestions extends MenuDrawerNew implements AdapterView.OnItemClickListener, AsyncTaches.AsyncGetListActivitePref.AsyncGetListActivitePrefListener, ReceiverGCM.GCMMessageListener {
    private final ArrayList<Activite> listeActivite = new ArrayList<>();
    private ActiviteAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private Button TV_AllerPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.messuggestions);
        InitDrawarToolBar();
        initTableauDeBord();
        adapter = new ActiviteAdapter(this, listeActivite);
        ListView listViewActivite = (ListView) findViewById(R.id.LV_listeActivite);
        listViewActivite.setAdapter(adapter);
        listViewActivite.setOnItemClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Outils.LOOP_BACK_RECEIVER_GCM.addGCMMessageListener(this);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSuggestions();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        TV_AllerPreference = (Button) findViewById(R.id.id_mesprefnces);

        new AsyncTaches.AsyncGetListActivitePref(this, Outils.personneConnectee.getId(), MesSuggestions.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    public void getSuggestions() {
        new AsyncTaches.AsyncGetListActivitePref(this, Outils.personneConnectee.getId(), MesSuggestions.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loopBack_GetListActivitePref(ArrayList<Activite> result) {

        if (result != null) {
            this.listeActivite.clear();
            this.listeActivite.addAll(result);
            Collections.sort(listeActivite, new ComparateurFinActivite());
            adapter.notifyDataSetChanged();
        }
        gestionDefaultLMessage();
        swipeContainer.setRefreshing(false);
    }

    private void gestionDefaultLMessage() {// Permet d'afficher un message si il n'y a pas de suggestion
        TextView TV_MessageDefaut = (TextView) findViewById(R.id.id_messagebalise);
         ListView LV_ListSuggestions = (ListView) findViewById(R.id.LV_listeActivite);

        if (listeActivite != null) {
            if (listeActivite.isEmpty())
            {
                TV_MessageDefaut.setText(R.string.MesSuggestions_noResutat);
                TV_MessageDefaut.setVisibility(View.VISIBLE);
                TV_AllerPreference.setVisibility(View.VISIBLE);
                LV_ListSuggestions.setVisibility(View.GONE);
                swipeContainer.setVisibility(View.GONE);
                TV_AllerPreference.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ouvreSuggestion();//ouvre le formulaire de suggestion

                    }
                });

            }
            else
                {
                TV_MessageDefaut.setText("");
                TV_MessageDefaut.setVisibility(View.GONE);
                TV_AllerPreference.setVisibility(View.GONE);
                LV_ListSuggestions.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.VISIBLE);
                    TV_MessageDefaut.setOnClickListener(null);
            }
        }

    }

    private void ouvreSuggestion() {
        Outils.fermeActiviteEnCours(this);
        Intent appel = new Intent(MesSuggestions.this,
                MesPreferences.class);
        appel.putExtra("onglet", MesPreferences.ONGLET_PREFERENCES);
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_liste_tri_activite, menu);
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Activite activite = (Activite) view.getTag();

        Intent appel;
        switch (activite.getTypeUser()) {

            case Profil.WAYDEUR:
                appel = new Intent(MesSuggestions.this, DetailActivite.class);
                appel.putExtra("idactivite", activite.getId());
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(appel, DetailActivite.ACTION_DETAIL_ACTIVITE);
                break;

            case Profil.PRO:
                appel = new Intent(MesSuggestions.this, DetailActivitePro.class);
                appel.putExtra("idactivite", activite.getId());
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(appel, DetailActivite.ACTION_DETAIL_ACTIVITE);
                break;

        }

    //    Intent appel = new Intent(MesSuggestions.this, DetailActivite.class);
     //   appel.putExtra("idactivite", activite.getId());
     //   appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
     //   startActivityForResult(appel, 1020);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1020) {
            boolean refresh = data.getBooleanExtra("refresh", false);
            if (refresh)
                new AsyncTaches.AsyncGetListActivitePref(this, Outils.personneConnectee.getId(),
                        MesSuggestions.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tri_distance:
                // Comportement du bouton "A Propos"
                Collections.sort(listeActivite, new ComparatorDistanceActivite());
                adapter.notifyDataSetChanged();

                return true;

            case R.id.tri_temps:
                // Comportement du bouton "Aide"
                Collections.sort(listeActivite, new ComparateurFinActivite());
                adapter.notifyDataSetChanged();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

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

            case PushAndroidMessage.NBR_SUGGESTION:
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                getSuggestions();
                break;

            case PushAndroidMessage.EFFACE_SUGGESTION:// Chaque fois q'une personne efface une activité, ce message est envoyé au gens interssé par cette activité.
                //dans ce cas on efface la suggestion.

                int idActivite = Integer.parseInt(extras.getString("idactivite"));

                Activite aEffacer = null;
                for (Activite activite : listeActivite) {
                    if (activite.getId() == idActivite)
                        aEffacer = activite;
                }
               listeActivite.remove(aEffacer);
               adapter.notifyDataSetChanged();
               gestionDefaultLMessage();
                 break;

        }


    }

    protected void onDestroy() {
        Outils.LOOP_BACK_RECEIVER_GCM.removeGCMMessageListener(this);
        super.onDestroy();
    }

}


