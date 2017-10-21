package com.wayd.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Profil;
import com.wayd.bean.ProfilPro;

public class F_DetailProfilPro extends Fragment {
    private static final String ARG_SECTION_COMMENTAIRE = "commentaire";
    private static final String ARG_TELEPHONE = "telephone";
    private static final String ARG_SIRET = "siret";
    private static final String ARG_SITEWEB = "siteweb";
    private static final String ARG_ADRESSE= "adresse";

    public F_DetailProfilPro() {
    }

    public static Fragment newInstance(ProfilPro profil) {
        F_DetailProfilPro fragment = new F_DetailProfilPro();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_COMMENTAIRE, profil.getCommentaire());
        args.putString(ARG_TELEPHONE, profil.getTelephone());
        args.putString(ARG_SIRET, profil.getSiret());
        args.putString(ARG_SITEWEB, profil.getSiteweb());
        args.putString(ARG_ADRESSE ,profil.getAdresse());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_detailprofilpro, container, false);
        String descriptionProfil = getArguments().getString(ARG_SECTION_COMMENTAIRE);
        String telephone = getArguments().getString(ARG_TELEPHONE);
        String siret = getArguments().getString(ARG_SIRET);
        String siteweb = getArguments().getString(ARG_SITEWEB);
        String adresse = getArguments().getString(ARG_ADRESSE);

        TextView TV_description = (TextView) rootView.findViewById(R.id.descriptionprofil);
        TextView TV_siret = (TextView) rootView.findViewById(R.id.siret);
        TextView TV_Adresse = (TextView) rootView.findViewById(R.id.adresse);
        TextView TV_Telephone = (TextView) rootView.findViewById(R.id.telephone);
        // TextView TV_siteweb = (TextView) rootView.findViewById(R.id.siteweb);

        // TextView TV_siteweb = (TextView) rootView.findViewById(R.id.siteweb);

        TV_siret.setText(siret);
     //   TV_siteweb.setText(siteweb);
        TV_Telephone.setText(telephone);
        TV_Adresse.setText(adresse);
        if (descriptionProfil.isEmpty()) {

            TV_description.setText(R.string.UnProfil_noDescriptionPro);

            }

        else

            TV_description.setText(descriptionProfil);


        return rootView;

    }

}
