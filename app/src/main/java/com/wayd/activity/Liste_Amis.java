package com.wayd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Ami;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.listadapter.AmiAdapter;

import java.util.ArrayList;

import java.util.List;

public class Liste_Amis extends MenuDrawerNew implements AmiAdapter.AmiAdapterListener, AsyncTaches.AsyncGetListAmis.Async_GetListAmis, AsyncTaches.AsyncEffaceAmi.Async_EffaceAmiListener {
    private final ArrayList<Ami> listeAmis = new ArrayList<>();
    private AmiAdapter amiAdapter;
    private Ami aEffacer;
    private AutoCompleteTextView inputseartch;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_amis);
        InitDrawarToolBar();
        initTableauDeBord();
        inputseartch = (AutoCompleteTextView) findViewById(R.id.id_search);
        amiAdapter = new AmiAdapter(this, listeAmis);
        amiAdapter.addListener(this);
        amiAdapter.setSearch(inputseartch);
        ListView listViewamis = (ListView) findViewById(R.id.LV_listeAmis);
        listViewamis.setAdapter(amiAdapter);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
          getListeAmis();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        new AsyncTaches.AsyncGetListAmis(this, Outils.personneConnectee.getId(), Liste_Amis.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getListeAmis() {
        new AsyncTaches.AsyncGetListAmis(this, Outils.personneConnectee.getId(), Liste_Amis.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClickAmi(Ami ami, int position) {
        Intent appel;
        appel = new Intent(Liste_Amis.this, UnProfil.class);
        appel.putExtra("idpersonne", ami.getId());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);
    }

    @Override
    public void onLongClickAmi(Ami ami, int position) {
        aEffacer = ami;
        dialogEffaceAmi(ami);
    }

    @Override
    public void onClickInformation(Ami ami, int position) {
        Intent appel;
        appel = new Intent(Liste_Amis.this, UnProfil.class);
        appel.putExtra("idpersonne", ami.getId());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);
    }

    @Override
    public void onClickMessage(Ami ami, int position) {
        Intent appel;
        appel = new Intent(Liste_Amis.this, MesMessages.class);
        appel.putExtra("idemetteur", ami.getId());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);
    }

    private void dialogEffaceAmi(final Ami ami) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Liste_Amis.this);
        builder.setTitle(R.string.SupprimeAmi_Titre);
        builder.setMessage(R.string.SupprimeAmi_Message);
        builder.setPositiveButton(R.string.SupprimeAmi_OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                effaceAmi(ami);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.SupprimeAmi_No, new DialogInterface.OnClickListener() {

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

    private void effaceAmi(Ami ami) {
        new AsyncTaches.AsyncEffaceAmi(this, Outils.personneConnectee.getId(), ami.getId(), Liste_Amis.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    private void gestionDefaultLMessage(){// Permet d'afficher un message si le nbr d'ami est null
        TextView TV_MessageDefaut = (TextView) findViewById(R.id.id_messagebalise);
        ListView LV_ListAmis = (ListView) findViewById(R.id.LV_listeAmis);
      if (listeAmis!=null){
          if (listeAmis.isEmpty()){

              TV_MessageDefaut.setText(R.string.Liste_Amis_noResutat);
              TV_MessageDefaut.setVisibility(View.VISIBLE);
              LV_ListAmis.setVisibility(View.GONE);

          }
          else{
              TV_MessageDefaut.setText("");

              TV_MessageDefaut.setVisibility(View.GONE);
              LV_ListAmis.setVisibility(View.VISIBLE);

          }
      }

    }

    @Override
    public void loopBack_getListAmis(ArrayList<Ami> listami) {// Sur reception de la liste Asynctache

        if (listami != null) {
            this.listeAmis.clear();
            this.listeAmis.addAll(listami);
            amiAdapter.setFiltre();
            amiAdapter.notifyDataSetChanged();
            List<String> listpseudo = new ArrayList<>();
            for (Ami ami : listeAmis) {
                listpseudo.add(ami.getPseudo());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>
                    (this, android.R.layout.simple_list_item_1, listpseudo);
            inputseartch.setAdapter(adapter);
        }
        gestionDefaultLMessage();
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void loopBack_EffaceAmi(MessageServeur messageserveur) {

        if (messageserveur != null) {

            if (messageserveur.isReponse()) {
                listeAmis.remove(aEffacer);
                amiAdapter.setFiltre();
                amiAdapter.notifyDataSetChanged();

                // Recharge la liste de pseudo pour la liste autocompletion
                List<String> listpseudo = new ArrayList<>();
                for (Ami ami : listeAmis) {
                    listpseudo.add(ami.getPseudo());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>
                        (this, android.R.layout.simple_list_item_1, listpseudo);
                inputseartch.setAdapter(adapter);
            }

        }
        gestionDefaultLMessage();//

    }
}
