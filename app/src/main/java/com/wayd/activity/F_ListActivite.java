package com.wayd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.application.wayd.R;
import com.google.android.gms.maps.model.LatLng;
import com.wayd.bean.Activite;
import com.wayd.bean.CritereRechercheActivite;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.Profil;
import com.wayd.comparator.ComparateurFinActivite;
import com.wayd.comparator.ComparatorDistanceActivite;
import com.wayd.listadapter.ActiviteAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


/**
 * Created by bibou on 13/01/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class F_ListActivite extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AsyncTaches.AsyncEffaceActivite.Async_EffaceActiviteListener{
    private ActiviteAdapter adapter;
    private Activite aEffacer;
    private TextView TV_MessageDefaut,messageActivite;
    private ImageView IM_flechebas;
    private boolean longClick = false;
    private ListView listViewActivite;
    private SwipeRefreshLayout swipeContainer;
    FloatingActionButton Float_Plus;
    boolean RECHERCHE3HEURES=true;

    public F_ListActivite() {

    }
    public static Fragment newInstance() {

        return new F_ListActivite();
    }

    public void setRecherche3heures(boolean recherche3heures){
        this.RECHERCHE3HEURES=recherche3heures;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_listeactivite, container, false);
        adapter = new ActiviteAdapter(getContext(), ((RechercheActiviteNew) getActivity()).getListeActivite());
        listViewActivite = (ListView) rootView.findViewById(R.id.listeActivite);
        messageActivite = (TextView) rootView.findViewById(R.id.messageactivite);
        messageActivite.setText("Click pour voir les activités dans les 3 Heures");
        listViewActivite.setAdapter(adapter);
        TV_MessageDefaut = (TextView) rootView.findViewById(R.id.id_messagebalise);
        IM_flechebas = (ImageView) rootView.findViewById(R.id.id_flechebas);
        listViewActivite.setOnItemClickListener(this);
        listViewActivite.setOnItemLongClickListener(this);
        adapter.notifyDataSetChanged();
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               ((RechercheActiviteNew) getActivity()).updateListeActivite(RechercheActiviteNew.FROM_SWIPE,F_Map_ListActivite.CENTRER_NOCHANGE);// DEmande de la part du swipe
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

         Float_Plus = (FloatingActionButton) rootView.findViewById(R.id.plus);


        Float_Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RECHERCHE3HEURES) {
                    ((RechercheActiviteNew) getActivity()).getCritereRechercheActivite().setCommenceDans(180);
                    ((RechercheActiviteNew) getActivity()).updateListeActivite(RechercheActiviteNew.FROM_PLUS, F_Map_ListActivite.CENTRER_NOCHANGE);// DEmande de la part du swipe
                 //   messageActivite.setText(((RechercheActiviteNew) getActivity()).getCritereRechercheActivite().getCommencantDans());
                //    messageActivite.setText("Click pour voir les activités en cours");
                    RECHERCHE3HEURES=!RECHERCHE3HEURES;
                }

                else
                 {
                     ((RechercheActiviteNew) getActivity()).getCritereRechercheActivite().setCommenceDans(0);
                     ((RechercheActiviteNew) getActivity()).updateListeActivite(RechercheActiviteNew.FROM_PLUS, F_Map_ListActivite.CENTRER_NOCHANGE);// DEmande de la part du swipe
                    // messageActivite.setText(((RechercheActiviteNew) getActivity()).getCritereRechercheActivite().getCommencantDans());
               //      messageActivite.setText("Click pour voir les activités en cours");
                     RECHERCHE3HEURES=!RECHERCHE3HEURES;

                }
            }
        });


        return rootView;
    }




    public void updateListe() {

        Log.d("F_listActivie", "taille" + ((RechercheActiviteNew) getActivity()).getListeActivite().size());
        adapter.notifyDataSetChanged();
        gestionDefaultLMessage();
        swipeContainer.setRefreshing(false);
        Log.d("F_listActivie", " updatelie réussie");


        if (RECHERCHE3HEURES) {
              messageActivite.setText("Click pour voir les activités dans 3 heures");


            Float_Plus.setImageResource(android.R.drawable.ic_media_ff);
       }

        else
        {

             messageActivite.setText("Click pour voir les activités en cours");
            Float_Plus.setImageResource(android.R.drawable.ic_media_rew);
        }


      //  messageActivite.setText(  ((RechercheActiviteNew) getActivity()).getCritereRechercheActivite().getCommencantDans() );
    }

    private void gestionDefaultLMessage() {// Permet d'afficher un message si il n'y a pas de suggestion

        if (((RechercheActiviteNew) getActivity()).getListeActivite() != null) {

            if (((RechercheActiviteNew) getActivity()).getListeActivite().isEmpty() && !RechercheActiviteNew.balise) {// Si il n'y a pas d'activité
                TV_MessageDefaut.setText(R.string.F_listActivite_noResutat);
                TV_MessageDefaut.setVisibility(View.GONE);
                IM_flechebas.setVisibility(View.GONE);
                listViewActivite.setVisibility(View.GONE);
                Float_Plus.setVisibility(View.VISIBLE);
            }

            else if (((RechercheActiviteNew) getActivity()).getListeActivite().isEmpty() && RechercheActiviteNew.balise) {// Si il n'y a pas d'activité et que l'on activte la fonction ennuie/balise
                // affichage de la fléche
                TV_MessageDefaut.setText(R.string.F_listActivite_noResutatBalise);
                TV_MessageDefaut.setVisibility(View.VISIBLE);
                listViewActivite.setVisibility(View.GONE);
                IM_flechebas.setVisibility(View.VISIBLE);
                Float_Plus.setVisibility(View.GONE);
            }
            else{
                  if (!((RechercheActiviteNew) getActivity()).getListeActivite().isEmpty() && RechercheActiviteNew.balise) {// Si il y a au moins une activité et que l'on activte la fonction ennuie/balise

                TV_MessageDefaut.setText(R.string.F_listActivite_noResutatBalise);
                TV_MessageDefaut.setVisibility(View.GONE);
                listViewActivite.setVisibility(View.VISIBLE);
                IM_flechebas.setVisibility(View.GONE);
                 Float_Plus.setVisibility(View.GONE);

            }

            else if (!((RechercheActiviteNew) getActivity()).getListeActivite().isEmpty() && !RechercheActiviteNew.balise) {// Recherche classique avec resultat
                IM_flechebas.setVisibility(View.GONE);
                TV_MessageDefaut.setVisibility(View.GONE);
                listViewActivite.setVisibility(View.VISIBLE);
                Float_Plus.setVisibility(View.VISIBLE);
            }
            }
        }

    }

    public void triDistance() {

        Collections.sort(((RechercheActiviteNew) getActivity()).getListeActivite(), new ComparatorDistanceActivite());
        adapter.notifyDataSetChanged();
    }

    public void triTemps() {

        Collections.sort(((RechercheActiviteNew) getActivity()).getListeActivite(), new ComparateurFinActivite());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!longClick) {
            Activite activite = (Activite) view.getTag();

            Intent appel;
            switch (activite.getTypeUser()) {

                case Profil.WAYDEUR:
                    appel = new Intent(getActivity(), DetailActivite.class);
                    appel.putExtra("idactivite", activite.getId());
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(appel, DetailActivite.ACTION_DETAIL_ACTIVITE);
                    break;

                case Profil.PRO:
                    appel = new Intent(getActivity(), DetailActivitePro.class);
                    appel.putExtra("idactivite", activite.getId());
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(appel, DetailActivite.ACTION_DETAIL_ACTIVITE);
                    break;

            }


        }
        longClick = false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

       System.out.println("************************ "+position);
        Activite activite = (Activite) view.getTag();
        longClick = true;
        if (Outils.personneConnectee.isAdmin()) dialogEffaceActivite(activite);
        return false;
    }

    private void dialogEffaceActivite(Activite item) {
        aEffacer = item;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.suppression_activiteadminTitre);
        builder.setMessage(R.string.suppression_activiteadminMessage);
        builder.setPositiveButton(R.string.suppression_activiteadminOK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                effaceActivite(aEffacer);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.suppression_activiteadminNo, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button buttonNon = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.altertDialog_Fondbutton));
        buttonNon.setTextColor(ContextCompat.getColor(getContext(), R.color.altertDialog_Textbutton));
        Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOui.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.altertDialog_Fondbutton));
        buttonOui.setTextColor(ContextCompat.getColor(getContext(), R.color.altertDialog_Textbutton));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 0, 15, 0);
        buttonOui.setLayoutParams(params);
    }

    private void effaceActivite(Activite activite) {
        new AsyncTaches.AsyncEffaceActivite(this, Outils.personneConnectee.getId(), activite.getId(), getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void loopBack_EffaceActivite(MessageServeur messageserveur) {

        ((RechercheActiviteNew) getActivity()).getListeActivite().remove(aEffacer);
        adapter.notifyDataSetChanged();

    }
    public void updateActivite(Activite resultActivite) {

        for (int f = 0; f < ((RechercheActiviteNew) getActivity()).getListeActivite().size(); f++) {
            if (((RechercheActiviteNew) getActivity()).getListeActivite().get(f).getId() == resultActivite.getId())
                ((RechercheActiviteNew) getActivity()).getListeActivite().set(f, resultActivite);
        }
        adapter.notifyDataSetChanged();
    }




}
