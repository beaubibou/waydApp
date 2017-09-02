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

public class F_DetailProfil extends Fragment {
    private static final String ARG_SECTION_COMMENTAIRE = "commentaire";
    private static final String ARG_SECTION_SEXE = "sexe";


    public F_DetailProfil() {
    }

    public static Fragment newInstance(Profil profil) {

        F_DetailProfil fragment = new F_DetailProfil();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_COMMENTAIRE, profil.getCommentaire());
        args.putInt(ARG_SECTION_SEXE, profil.getSexe());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_detailprofil, container, false);
        String descriptionProfil = getArguments().getString(ARG_SECTION_COMMENTAIRE);
        int sexe = getArguments().getInt(ARG_SECTION_SEXE);

        TextView TV_description = (TextView) rootView.findViewById(R.id.descriptionprofil);
        Log.d("unprofil",descriptionProfil);



        if (descriptionProfil.isEmpty()) {



            switch (sexe){

                case 0:// Cas de la femme

                    TV_description.setText(R.string.UnProfil_noDescriptionFemme);

                    break;
                case 1:
                    TV_description.setText(R.string.UnProfil_noDescriptionHomme);
                    break;
                case 2:

                    TV_description.setText(R.string.UnProfil_noDescriptionMasque);
                    break;

                case 3:
                    TV_description.setText(R.string.UnProfil_noDescriptionMasque);
                    break;
                default:
                    TV_description.setText(R.string.UnProfil_noDescriptionMasque);

            }

        }
        else

            TV_description.setText(descriptionProfil);

        return rootView;

    }

}
