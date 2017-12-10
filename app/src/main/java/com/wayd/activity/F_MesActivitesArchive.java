package com.wayd.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.application.wayd.R;
import com.google.android.gms.internal.ac;
import com.wayd.bean.Activite;
import com.wayd.bean.Outils;
import com.wayd.bean.Profil;
import com.wayd.listadapter.ActiviteAdapter;

import java.util.ArrayList;

public class F_MesActivitesArchive extends Fragment implements AsyncTaches.AsyncGetMesActiviteArchive.AsyncGetMesActiveArchiveListener {
    private final ArrayList<Activite> listeActivite = new ArrayList<>();
    private ActiviteAdapter adapter;
    private boolean firstInit=false;

    public F_MesActivitesArchive() {

    }
    public static Fragment newInstance() {
        return new F_MesActivitesArchive();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_activiteencours, container, false);
        adapter = new ActiviteAdapter(getActivity(), listeActivite);
        ListView listViewActivite = (ListView) rootView.findViewById(R.id.LV_listeActivite);
        listViewActivite.setAdapter(adapter);


        listViewActivite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Activite activite = (Activite) view.getTag();

                System.out.println("******afiche user"+ activite.getTypeUser());

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
        });

        return rootView;
    }

    @Override
    public void loopBack_GetMesActiviteArchive(ArrayList<Activite> listeactivite) {

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
    }
    public void initArchive(){
       if (!firstInit)
        new AsyncTaches.AsyncGetMesActiviteArchive(this,Outils.personneConnectee.getId(),getActivity()).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        firstInit=true;

    }

}
