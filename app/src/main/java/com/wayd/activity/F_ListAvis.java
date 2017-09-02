package com.wayd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.application.wayd.R;
import com.wayd.bean.Avis;
import com.wayd.bean.Profil;
import com.wayd.listadapter.AvisAdapter;

import java.util.ArrayList;


/**
 * Created by bibou on 13/01/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class F_ListAvis extends Fragment {

    private static ArrayList<Avis> listeAvis = new ArrayList<>();
    private static final String ARG_ID_PROFIL = "idprofil";


    public F_ListAvis() {
    }

    public static Fragment newInstance(Profil profil, ArrayList<Avis> listeAvis) {

        F_ListAvis fragment = new F_ListAvis();
        F_ListAvis.listeAvis = listeAvis;
        Bundle args = new Bundle();
        args.putInt(ARG_ID_PROFIL, profil.getIdpersonne());
        F_ListAvis.listeAvis = new ArrayList<>(listeAvis);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.f_listavis, container, false);
        AvisAdapter adapter = new AvisAdapter(getContext(), listeAvis);
        ListView listViewavis = (ListView) rootView.findViewById(R.id.listeavis);
        listViewavis.setAdapter(adapter);
        listViewavis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Avis avis = (Avis) view.getTag();
                Intent appel = new Intent(getActivity(), UnProfil.class);
                appel.putExtra("idpersonne", avis.getIdpersonnenotateur());
                startActivity(appel);
                getActivity().finish();

            }
        });
        adapter.notifyDataSetChanged();
        return rootView;

    }


}
