package com.wayd.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.Preference;
import com.wayd.listadapter.PreferencesAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


/**
 * Created by bibou on 13/01/2017.
 */
@SuppressWarnings("DefaultFileTemplate")
public class F_Preferences extends Fragment implements AsyncTaches.AsyncUpdatePreference.Async_UpdatePreferenceListener, AsyncTaches.AsyncGetListPreference.AsyncGetListPreferenceListener {

    private PreferencesAdapter adapter;
    private SeekBar SK_rayon;
    private TextView TV_rayonMax, TV_rayonMin;
    private static int RAYON_MIN_RECHERCHE=100;
    private final int RAYON_MAX_RECHERCHE=6000;

    private final ArrayList<Preference> listepreference = new ArrayList<>();

    public F_Preferences() {
    }

    public static Fragment newInstance() {

        return new F_Preferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.f_preferences, container, false);
        adapter = new PreferencesAdapter(getContext(), listepreference);
        ListView listViewPreference = (ListView) rootView.findViewById(R.id.LV_listepreferences);
        listViewPreference = (ListView) rootView.findViewById(R.id.LV_listepreferences);
        SK_rayon = (SeekBar) rootView.findViewById(R.id.rayonpref);
        TV_rayonMin = (TextView) rootView.findViewById(R.id.minrayon);
        TV_rayonMin.setText(RAYON_MIN_RECHERCHE+ "M");

        TV_rayonMax = (TextView) rootView.findViewById(R.id.id_affichermax);

        SK_rayon.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1000-RAYON_MIN_RECHERCHE)
                    TV_rayonMax.setText("" + (progress+RAYON_MIN_RECHERCHE) + " M");
                else {
                    NumberFormat nf = new DecimalFormat("0.0");
                    String s = nf.format((double) (progress+RAYON_MIN_RECHERCHE) / 1000);
                    TV_rayonMax.setText("" + s + " Km");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SK_rayon.setMax(RAYON_MAX_RECHERCHE-RAYON_MIN_RECHERCHE);
        SK_rayon.setProgress(Outils.personneConnectee.getRayon()-RAYON_MIN_RECHERCHE);
        FloatingActionButton savepref = (FloatingActionButton) rootView.findViewById(R.id.savepref);
        savepref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePref();
            }
        });
        listViewPreference.setAdapter(adapter);
        new AsyncTaches.AsyncGetListPreference(this, Outils.personneConnectee.getId(), getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return rootView;

    }

    private void savePref() {
        int rayon = SK_rayon.getProgress()+RAYON_MIN_RECHERCHE;
        new AsyncTaches.AsyncUpdatePreference(this, Outils.personneConnectee.getId(), listepreference, rayon, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void loopBack_UpdatePreference(MessageServeur messageserveur) {

        if (messageserveur != null) {
            if (messageserveur.isReponse()) {

                Outils.personneConnectee.setRayon(SK_rayon.getProgress()+RAYON_MIN_RECHERCHE);
                Toast toast = Toast.makeText(getActivity().getBaseContext(), R.string.prefrenceMisAjour, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


        }

    }

    @Override
    public void loopBack_GetListPreference(ArrayList<Preference> result) {
        if (result != null) {

            listepreference.clear();
            listepreference.addAll(result);
            adapter.notifyDataSetChanged();


        }
    }


}
