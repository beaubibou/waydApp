package com.wayd.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Profil;

public class F_Stats extends Fragment {

    private static final String ARG_NBR_AVIS ="nbravis";
    private static final String ARG_NBR_AMIS ="nbramis" ;
    private static final String ARG_NBR_PARTICIPATION ="nbrparticipation" ;
    private static final String ARG_NBR_ACTIVITE ="nbractivite" ;


    public F_Stats() {
    }
    public static Fragment newInstance(Profil profil) {

        F_Stats fragment = new F_Stats();
        Bundle args = new Bundle();
        args.putInt(ARG_NBR_AVIS, profil.getNbravis());
        args.putInt(ARG_NBR_AMIS, profil.getNbrami());
        args.putInt(ARG_NBR_PARTICIPATION, profil.getNbrparticipation());
        args.putInt(ARG_NBR_ACTIVITE, profil.getNbractivite());
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_stats, container, false);
        int nbrAvis=getArguments().getInt(ARG_NBR_AVIS,0);
        int nbrAmis=getArguments().getInt(ARG_NBR_AMIS,0);
        int nbrParticipation=getArguments().getInt(ARG_NBR_PARTICIPATION,0);
        int nbrActivite=getArguments().getInt(ARG_NBR_ACTIVITE,0);

        TextView  TV_nbramis = (TextView) rootView.findViewById(R.id.nbramis);
        TextView  TV_nbravis = (TextView) rootView.findViewById(R.id.nbravis);
        TextView  TV_nbractivite = (TextView) rootView.findViewById(R.id.nbractivite);
        TextView  TV_nbrparticipation = (TextView) rootView.findViewById(R.id.nbrparticipation);

      //  TextView  LibTV_nbramis = (TextView) rootView.findViewById(R.id.libactivite);
        TextView  LibTV_nbravis = (TextView) rootView.findViewById(R.id.libnbravis);
        TextView  LibTV_nbractivite = (TextView) rootView.findViewById(R.id.libnbractivite);
        TextView  LibTV_nbrparticipation = (TextView) rootView.findViewById(R.id.libnbrparticipation);

        if (nbrAvis>1)LibTV_nbravis.setText(R.string.f_stats_commentaires);
        else
        LibTV_nbravis.setText(R.string.f_stats_commentaire);

        if (nbrActivite>1)LibTV_nbractivite.setText(R.string.f_stats_activites);
        else
            LibTV_nbractivite.setText(R.string.f_stats_activite);

        if (nbrParticipation>1)LibTV_nbrparticipation.setText(R.string.f_stats_participations);
        else
            LibTV_nbrparticipation.setText(R.string.f_stats_participation);

        TV_nbravis.setText(String.valueOf(nbrAvis));
        TV_nbrparticipation.setText(String.valueOf(nbrParticipation));
        TV_nbractivite.setText(String.valueOf(nbrActivite));
        TV_nbramis.setText(String.valueOf(nbrAmis));

        return rootView;

    }

  }
