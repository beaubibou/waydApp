package com.wayd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.Participant;
import com.wayd.bean.Profil;
import com.wayd.bean.PushAndroidMessage;
import com.wayd.bean.ReceiverGCM;
import com.wayd.listadapter.MesActiviteAdapter;

import java.util.ArrayList;
import java.util.Iterator;

public class F_MesActivitesEnCours extends Fragment implements MesActiviteAdapter.ActiviteAdapterListener, AsyncTaches.AsyncGetMesActiviteEnCours.AsyncGetMesActiveEnCoursListener, AsyncTaches.AsyncEffaceActivite.Async_EffaceActiviteListener, AsyncTaches.AsyncEffaceParticipation.Async_EffaceParticipationListener, ReceiverGCM.GCMMessageListener, AsyncTaches.AsyncGetActiviteFull.Async_GetActiviteFullListener {
    private final ArrayList<Activite> listeActivite = new ArrayList<>();
    private MesActiviteAdapter adapter;
    //   private final int RETOUR_DETAIL_ACTIVITE=1020;
    private Activite aEffacer;
    private TextView TV_MessageDefaut;
    private ListView LV_ListActivite;
    private SwipeRefreshLayout swipeContainer;

    public F_MesActivitesEnCours() {

    }

    public static Fragment newInstance() {
        return new F_MesActivitesEnCours();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_activiteencours, container, false);
        adapter = new MesActiviteAdapter(getActivity(), listeActivite);
        adapter.addListener(this);
        ListView listViewActivite = (ListView) rootView.findViewById(R.id.LV_listeActivite);
        listViewActivite.setAdapter(adapter);
        TV_MessageDefaut = (TextView) rootView.findViewById(R.id.id_messagebalise);
        LV_ListActivite = (ListView) rootView.findViewById(R.id.LV_listeActivite);
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListeActiviteEncours();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        new AsyncTaches.AsyncGetMesActiviteEnCours(this, Outils.personneConnectee.getId(), getActivity()).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Outils.LOOP_BACK_RECEIVER_GCM.addGCMMessageListener(this);

        return rootView;
    }

    private void getListeActiviteEncours() {
        new AsyncTaches.AsyncGetMesActiviteEnCours(this, Outils.personneConnectee.getId(), getActivity()).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loopBack_GetMesActiviteEnCours(ArrayList<Activite> listeactivite) {

        if (listeactivite==null){

            Toast toast = Toast.makeText(getActivity().getBaseContext(),"Vous avez été désactivé"
                    , Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

         return;
        }

        listeActivite.clear();
        listeActivite.addAll(listeactivite);
        adapter.notifyDataSetChanged();
        gestionDefaultLMessage();
        swipeContainer.setRefreshing(false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == DetailActivite.ACTION_DETAIL_ACTIVITE) {
            int actionDetailActivite = data.getIntExtra("action", -1);
            if (actionDetailActivite == DetailActivite.ACTION_REMOVE_ACTIVITE ||
                    actionDetailActivite == DetailActivite.ACTION_REMOVE_PARTICIPATION) {
                listeActivite.remove(aEffacer);
                adapter.notifyDataSetChanged();
            }

            if (actionDetailActivite == DetailActivite.ACTION_MODIFIEE_ACTIVITE) {
                getListeActiviteEncours();
            }


        }
    }


    @Override
    public void onLongClickActivite(Activite activite, int position) {

        dialogEffaceActivite(activite);
    }

    @Override
    public void onClickMessage(Activite activite, int position) {

        Intent appel = new Intent(getActivity(), MesMessagesActvite.class);
        appel.putExtra("idactivite", activite.getId());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);
        getActivity().finish();//

    }

    @Override
    public void onClickInformation(Activite activite, int position) {
        // Activite activite = (Activite) view.getTag();
        aEffacer = activite;
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
        @Override
        public void onClickAtivite (Activite activite,int position){
            aEffacer = activite;
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

    private void dialogEffaceActivite(final Activite activite) {
        aEffacer = activite;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.suppression_activiteTitre);
        builder.setMessage(R.string.suppression_activiteMessage);
        builder.setPositiveButton(R.string.suppression_activiteOK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                effaceActivite(activite);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.suppression_activiteNo, new DialogInterface.OnClickListener() {

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

        if (activite.isOrganisateur(Outils.personneConnectee.getId())) {

            new AsyncTaches.AsyncEffaceActivite(this, Outils.personneConnectee.getId(), activite.getId(), getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            new AsyncTaches.AsyncEffaceParticipation(this, Outils.personneConnectee.getId(), Outils.personneConnectee.getId(), activite.getId(), getActivity())
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        gestionDefaultLMessage();
    }

    @Override
    public void loopBack_EffaceActivite(MessageServeur messageserveur) {
        listeActivite.remove(aEffacer);
        adapter.notifyDataSetChanged();
        gestionDefaultLMessage();
    }

    @Override
    public void loopBack_EffaceParticipation(MessageServeur messageserveur) {
        listeActivite.remove(aEffacer);
        adapter.notifyDataSetChanged();
        gestionDefaultLMessage();
    }

    private void gestionDefaultLMessage() {// Permet d'afficher un message si le nbr d'ami est null
        if (listeActivite != null) {
            if (listeActivite.isEmpty()) {

                TV_MessageDefaut.setText(R.string.F_MesActivitesEnCours_noResutat);
                TV_MessageDefaut.setVisibility(View.VISIBLE);
                LV_ListActivite.setVisibility(View.GONE);

            } else {
                TV_MessageDefaut.setText("");

                TV_MessageDefaut.setVisibility(View.GONE);
                LV_ListActivite.setVisibility(View.VISIBLE);

            }
        }

    }

    @Override
    public void loopBackReceiveGCM(Bundle bundle) {// Reception des messageGCM. Si un participant à son activité supprimé
        // un message GCM de type AnnuleParticipation est envoyé. On supprime l'activité


        if (bundle == null) return;

        int idMessageGcm = 0;

        try {

            idMessageGcm = Integer.parseInt(bundle.getString("id"));
        } catch (Exception e) {

            //   e.printStackTrace();
            return;
        }


        switch (idMessageGcm) {

            case PushAndroidMessage.Annule_PARTICIPATION:// Si l'organisteur te supprile tu recois un GCM du coup du supprime l'activité
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                int idactivite = Integer.parseInt(bundle.getString("idactivite"));
                int idpersonne = Integer.parseInt(bundle.getString("idpersonne"));

                if (idpersonne == Outils.personneConnectee.getId()) {
                    Iterator<Activite> it = listeActivite.iterator();
                    while (it.hasNext()) {
                        Activite activite = it.next();
                        if (activite.getId() == idactivite) it.remove();
                    }
                    adapter.notifyDataSetChanged();

                }
                break;


            case PushAndroidMessage.Annule_Activite://// Si l'organisteur annule un GCM du coup du supprime l'activité
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                idactivite = Integer.parseInt(bundle.getString("idactivite"));
                Iterator<Activite> it = listeActivite.iterator();
                while (it.hasNext()) {
                    Activite activite = it.next();
                    if (activite.getId() == idactivite) it.remove();

                    adapter.notifyDataSetChanged();

                }
                break;

            case PushAndroidMessage.UPDATE_ACTIVITE://// Si l'organisteur annule un GCM du coup du supprime l'activité
                // On envoie dans le mgc l'id de la personne en plus de son numero GSM.
                idactivite = Integer.parseInt(bundle.getString("idactivite"));
                new AsyncTaches.AsyncGetActiviteFull(this, idactivite, false, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                break;


        }


    }

    public void onDestroy() {

        Outils.LOOP_BACK_RECEIVER_GCM.removeGCMMessageListener(this);
        super.onDestroy();
    }

    @Override
    public void loopBack_GetActiviteFull(Activite activite_, ArrayList<Participant> listParticipant) {

        if (activite_ != null) {
            for (Activite activite : listeActivite) {

                if (activite.getId() == activite_.getId()) {

                    activite.updateActivite(activite_);
                }
            }

            adapter.notifyDataSetChanged();
        }

    }
}

