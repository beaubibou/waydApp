package com.wayd.activity;

import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.wayd.bean.Activite;
import com.wayd.bean.CritereRechercheActivite;
import com.wayd.bean.GPSTracker;
import com.wayd.bean.Outils;
import com.wayd.bean.TypeActivite;
import com.wayd.listadapter.PlaceArrayAdapter;
import com.wayd.listadapter.TypeActivityAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class F_CritereRechecheActivite extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, AsyncTaches.AsyncGetAdresse.AsyncGetAdresseListener, GPSTracker.positionGpsListener {

    private final static int RAYON_MAX_RECHERCHE = 10000;
    private final static int RAYON_MAX_RECHERCHE_ADMIN = 100000;
    private final static int TOUTES = -1;
    private AutoCompleteTextView mAutocompleteTextView;
    private PlaceArrayAdapter.PlaceAutocomplete item;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private GoogleApiClient mGoogleApiClient;
    private CheckBox CK_GPS;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(-89, -179), new LatLng(89, 179));

    private final ArrayList<TypeActivite> listtypeactivitecomplete = new ArrayList<>();
    private Spinner spinnerFiltreactivite;
    private SeekBar seekRayon;
    private TextView TV_rayonMax;
    private EditText motcle;
    private View rootView = null;
    private boolean adressValide=false;

    private CritereRechercheActivite critereRechercheActivite;

    public F_CritereRechecheActivite() {

        critereRechercheActivite=new CritereRechercheActivite(false, RechercheActiviteNew.motCle,RechercheActiviteNew.TOUTE_ACTIVITE, RechercheActiviteNew.RAYON_RECHERCHE_DEFAUT);

       }



    public static Fragment newInstance() {

        return new F_CritereRechecheActivite();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.f_critererechercheactivite, container, false);
        InitAutoCompletetion();
        InitBarreRayon(Outils.personneConnectee.isAdmin());
        initBouttonListeActivite();
        InitSpinnerActivite();
        InitMotCle();
        Outils.gps.addListenerGPS(this);
        InitCkGPS();

        spinnerFiltreactivite.setSelection(0);// Met la valeur par defaut de la catégorie (toutes)
        seekRayon.setProgress(RechercheActiviteNew.RAYON_RECHERCHE_DEFAUT);// Met la vaelur par defaut de recherche
        return rootView;

    }

    private void InitMotCle() {
        motcle = (EditText) rootView.findViewById(R.id.id_motcle);
        motcle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ((RechercheActiviteNew) getActivity()).setCritereRechercheActivite(getFiltre());


            }
        });


    }


    private void InitAutoCompletetion() {

        mAutocompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                adressValide=false;
                CK_GPS.setChecked(false);
                return false;
            }
        });
        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

    }


    private void initBouttonListeActivite() {
        Button BUT_listActivity = (Button) rootView.findViewById(R.id.rechercheactivite);
        BUT_listActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (validator())
                getListeActivites();
            //    else
             //   Toast.makeText(getActivity().getBaseContext(),"Ton adresse n'est pas valide" , Toast.LENGTH_LONG).show();

           }
        });
    }

    private boolean validator() {

        if (mAutocompleteTextView.getText().toString().isEmpty() && !CK_GPS.isChecked()){

            Toast toast =Toast.makeText(getActivity().getBaseContext(),R.string.noAdresse , Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }

        if (!adressValide && !CK_GPS.isChecked() ){
            Toast toast =Toast.makeText(getActivity().getBaseContext(), R.string.err_adresse, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        return true;
    }

    private void getListeActivites() {
        CritereRechercheActivite filtretmp = getFiltre();
        ((RechercheActiviteNew) getActivity()).setCritereRechercheActivite(filtretmp);
        ((RechercheActiviteNew) getActivity()).updateListeActivite(RechercheActiviteNew.FROM_RECHERCHE,F_Map_ListActivite.CENTRER_PERSONNE);//demande le rafraichissement de la part du bouton
    }

    private CritereRechercheActivite getFiltre() {
        boolean isfiltre;
        double finalLatitide, finalLongitude;
        int idtypeactivite = ((TypeActivite) spinnerFiltreactivite.getSelectedItem()).getId();
        isfiltre = idtypeactivite != TOUTES;
        finalLatitide = Outils.personneConnectee.getPosition().latitude;// En fonction du gps la personne renvoi l'adresse saise ou le gps
        finalLongitude = Outils.personneConnectee.getPosition().longitude;
        return new CritereRechercheActivite(isfiltre, motcle.getText().toString(), idtypeactivite, seekRayon.getProgress()
                , finalLongitude, finalLatitide);
    }


    private void InitSpinnerActivite() {
        listtypeactivitecomplete.addAll(Outils.listtypeactivitecomplete);
        listtypeactivitecomplete.add(0, new TypeActivite(TOUTES, TOUTES, "Toutes", true));
        spinnerFiltreactivite = (Spinner) rootView.findViewById(R.id.id_typeactivite);
        TypeActivityAdapter typeActivieAdapter = new TypeActivityAdapter(getActivity(), listtypeactivitecomplete);
        spinnerFiltreactivite.setAdapter(typeActivieAdapter);

        typeActivieAdapter.notifyDataSetChanged();
        spinnerFiltreactivite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((RechercheActiviteNew) getActivity()).setCritereRechercheActivite(getFiltre());

             int idCategorie = ((TypeActivite) spinnerFiltreactivite.getSelectedItem()).getId();
                  //met a jour le filtre du formulaire suite à changement du spinne
             critereRechercheActivite.setIdtypeactivite(idCategorie);
             // Previent les autres fragment que le critere de recherche à changer


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity()).
                enableAutoManage(this.getActivity() /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void InitCkGPS() {
        CK_GPS = (CheckBox) rootView.findViewById(R.id.id_gps);
        CK_GPS.setChecked(false);
        CK_GPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    if (Outils.gps.canGetLocation())// Si le gps est disponible
                    {
                        Outils.personneConnectee.setPositionGps(Outils.gps.getLatitude(), Outils.gps.getLongitude());
                        LatLng position=new LatLng(Outils.gps.getLatitude(), Outils.gps.getLongitude());
                        updateAdresseGps(position);
                        Outils.personneConnectee.setGps(true);
                        //Met la position au filtre
                        critereRechercheActivite.setLatLng(position);
                        // Envoi la nouvelle position aux autres fragments

                    }
                    else {

                        Outils.gps.showSettingsAlert(getContext(), getActivity());
                        buttonView.setChecked(false);
                        Outils.personneConnectee.setGps(false);
                    }
                }
                else

                {
                    mAutocompleteTextView.setHint(R.string.f_critererecherche_hint_saisiradresse);
                    Outils.personneConnectee.setGps(false);
                }

            }
        });

        if (Outils.gps.canGetLocation()) {
            CK_GPS.setChecked(true);
            Outils.personneConnectee.setGps(true);

        } else {

            CK_GPS.setChecked(false);
            Outils.personneConnectee.setGps(false);
        }
    }

    private void InitBarreRayon(boolean isadmin) {
        TV_rayonMax = (TextView) rootView.findViewById(R.id.id_affichermax);
        seekRayon = (SeekBar) rootView.findViewById(R.id.id_seek_rayon);

        if (isadmin)
            seekRayon.setMax(RAYON_MAX_RECHERCHE_ADMIN);
        else
            seekRayon.setMax(RAYON_MAX_RECHERCHE);

        TV_rayonMax.setText(String.valueOf(RechercheActiviteNew.RAYON_RECHERCHE_DEFAUT));
        seekRayon.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress < 1000)
                    TV_rayonMax.setText("" + progress + " M");
                else {
                    NumberFormat nf = new DecimalFormat("0.0");
                    String s = nf.format((double) progress / 1000);
                    TV_rayonMax.setText("" + s + " Km");
                }
                ((RechercheActiviteNew) getActivity()).setCritereRechercheActivite(getFiltre());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((RechercheActiviteNew) getActivity()).setCritereRechercheActivite(getFiltre());
            }
        });

    }

    private final ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {

                return;
            }
            final Place place = places.get(0);
            LatLng positionSaisie=new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            Outils.personneConnectee.SetPositionSaisie(positionSaisie);
            //Met la position au filtre
            critereRechercheActivite.setLatLng(positionSaisie);
            // Envoi la nouvelle position aux autres fragments


        }
    };

    private final AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            CK_GPS.setChecked(false);
            mAutocompleteTextView.setEnabled(false);
            mAutocompleteTextView.setEnabled(true);
            adressValide=true;

        }


    };


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    public void onDestroy() {
        Outils.gps.removeListenerGPS(this);
        super.onDestroy();
    }

    @Override
    public void loopBack_GetAdresse(Address adresse) {

        if (adresse != null) {
            mAutocompleteTextView.setText("");
            mAutocompleteTextView.setHint(adresse.getAddressLine(0));
            item = null;
        } else

            mAutocompleteTextView.setHint("Pas d'adresse connue");
    }


    @Override
    public void loopBackChangePositionGps(LatLng latLng) {
        if (CK_GPS.isChecked()) updateAdresseGps(latLng);
    }



    private void updateAdresseGps(LatLng latLng) {

        new AsyncTaches.AsyncGetAdresse(this, latLng.latitude, latLng.longitude, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }








}
