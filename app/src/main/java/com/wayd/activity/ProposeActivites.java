package com.wayd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import com.wayd.bean.GPSTracker;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.TypeActivite;
import com.wayd.listadapter.PlaceArrayAdapter;
import com.wayd.listadapter.TypeActivityAdapter;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;


public class ProposeActivites extends MenuDrawerNew implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, AsyncTaches.AsyncGetAdresse.AsyncGetAdresseListener, AsyncTaches.AsyncAddActivite.Async_AddActiviteListener, GPSTracker.positionGpsListener {

    private CheckBox CK_GPS;
    private String titre;
    private String libelle;
    private int dureeBalise, dureeActivite;
    private double latitudeSaisie = 47;
    private double longitudeSaisie = 3;
    private static GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final String LOG_TAG = "ProposeActivites";
    private AutoCompleteTextView mAutocompleteTextView;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private TypeActivityAdapter typeActivieAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(-89, -179), new LatLng(89, 179));
    private PlaceArrayAdapter.PlaceAutocomplete item;
    private Spinner  SP_NbxMaxWaydeurs, SP_DureeActivite;
    private TextView ET_titre;
    private TextView TV_Commentaires;
    private Spinner spinnertypeactivite;
    private boolean balise=false;
    private boolean adressValide=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.propose_activite);
        InitDrawarToolBar();
        initTableauDeBord();
        TV_Commentaires = (TextView) findViewById(R.id.commentaire);
        ET_titre = (EditText) findViewById(R.id.dateactivite);
        CK_GPS = (CheckBox) findViewById(R.id.id_gps);
        Button BUT_ProposeActivite = (Button) findViewById(R.id.id_buttonPropose);
        BUT_ProposeActivite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajouteActivite();
            }
        });
        SP_NbxMaxWaydeurs = (Spinner) findViewById(R.id.nbrmaxwayd);
        init_SP_DureeActivite();
        init_SP_NbmaxWaydeur();
        int_SP_TypeActivite();
        InitAdresseAutoComplete();
        InitCkGPS();
        balise=getIntent().getBooleanExtra("balise", false);
        Outils.gps.addListenerGPS(this);
        initListActivite();
        TV_Commentaires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifieDescription();// Affiche le pop pour modifier la descripton
            }
        });

        TV_Commentaires.setMovementMethod(new ScrollingMovementMethod());

    //    TV_Commentaires.getBackground().setColorFilter(Color.parseColor("#fadddd"), PorterDuff.Mode.SRC_IN);

    }

    private void int_SP_TypeActivite() {
        spinnertypeactivite = (Spinner) findViewById(R.id.id_typeactivite);
        typeActivieAdapter = new TypeActivityAdapter(this, Outils.listtypeactivitecomplete);
        spinnertypeactivite.setAdapter(typeActivieAdapter);
    }

    private void InitCkGPS() {
        CK_GPS = (CheckBox) findViewById(R.id.id_gps);
        CK_GPS.setChecked(false);
        CK_GPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    if (Outils.gps.canGetLocation())// Si le gps est disponible
                    {
                        updateAdresseGps(new LatLng(Outils.gps.getLatitude(), Outils.gps.getLongitude()));
                    } else {
                        Outils.gps.showSettingsAlert(getBaseContext(), ProposeActivites.this);
                        buttonView.setChecked(false);
                    }
                } else

                {
                    mAutocompleteTextView.setHint(R.string.s_proposeactivite_hintnoadresse);

                }

            }
        });

        if (Outils.gps.canGetLocation()) {
            CK_GPS.setChecked(true);
        } else {
            CK_GPS.setChecked(false);
        }

    }

    private void InitAdresseAutoComplete() {
        mGoogleApiClient = new GoogleApiClient.Builder(ProposeActivites.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
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
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
    }



    private void init_SP_DureeActivite() {
        SP_DureeActivite = (Spinner) findViewById(R.id.dureeactivite);
        List<String> duree = new ArrayList<>();
        duree.add("30min");
        duree.add("1h");
        duree.add("1h30");
        duree.add("2h");
        ArrayAdapter<String> dureeAdapter = new ArrayAdapter<>(this, R.layout.defautspinneritem, duree);
        SP_DureeActivite.setAdapter(dureeAdapter);
        SP_DureeActivite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dureeActivite = (position + 1) * 30;
                dureeBalise= (position + 1) * 30;
              }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        SP_DureeActivite.setSelection(3);//

    }

    private void init_SP_NbmaxWaydeur() {
        List<String> duree = new ArrayList<>();
        duree.add("2");
        duree.add("3");
        duree.add("4");
        duree.add("5");
        duree.add("6");
        ArrayAdapter<String> dureeAdapter = new ArrayAdapter<>(this, R.layout.defautspinneritem, duree);
        SP_NbxMaxWaydeurs.setAdapter(dureeAdapter);

    }

    private void updateAdresseGps(LatLng latLng) {

        new AsyncTaches.AsyncGetAdresse(this, latLng.latitude, latLng.longitude, ProposeActivites.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private final AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CK_GPS.setChecked(false);
            item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
            mAutocompleteTextView.setHint(mAutocompleteTextView.getText().toString());
            mAutocompleteTextView.setEnabled(false);
            mAutocompleteTextView.setEnabled(true);
            adressValide=true;
        }
    };


    private final ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            latitudeSaisie = place.getLatLng().latitude;
            longitudeSaisie = place.getLatLng().longitude;

        }
    };


    private void initListActivite() {
        typeActivieAdapter.notifyDataSetChanged();

        if (balise) {// Dans le cas ou la recherche est lancée par une balise on rempli les champs

            TV_Commentaires.setText(R.string.proposeactivite_defautCommentaire);
            ET_titre.setText(R.string.proposeactivite_defautTitre);
            spinnertypeactivite.setSelection(Outils.getIndiceTypeActivite(TypeActivite.WAYDEURS_DISPO));
        }

        else{

            spinnertypeactivite.setSelection(Outils.getIndiceTypeActivite(TypeActivite.DISCUTER));
        }

        SP_DureeActivite.setSelection(3);//

    }

    @Override
    public void loopBack_GetAdresse(Address adresse) {

        if (adresse != null) {
            mAutocompleteTextView.setText("");
            mAutocompleteTextView.setHint(adresse.getAddressLine(0) );
            item = null;
        } else
            mAutocompleteTextView.setHint(R.string.s_proposeactivite_hintnoadresse);
    }

    private boolean Validator() {

        //   titre = ET_titre.getText().toString();
        //   libelle = TV_Commentaires.getText().toString();

        titre = StringEscapeUtils.escapeJava(ET_titre.getText().toString()).trim();
        libelle = StringEscapeUtils.escapeJava(TV_Commentaires.getText().toString()).trim();


        if (titre.isEmpty()) {

            Toast toast =Toast.makeText(getBaseContext(),  R.string.proposeactivite_err_titre, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }

        if (libelle.isEmpty()) {
            //   Toast.makeText(getBaseContext(), "Libelle obligatoire", Toast.LENGTH_SHORT).show();
            //  return false;
            libelle = " ";
        }

        if (mAutocompleteTextView.getText().toString().isEmpty() && !CK_GPS.isChecked()){

            Toast toast =Toast.makeText(getBaseContext(),R.string.proposeactivite_err_saisieadresse, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

              return false;
        }

        if (!adressValide && !CK_GPS.isChecked() ){
            Toast toast =Toast.makeText(getBaseContext(), R.string.proposeactivite_err_adresse, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            return false;
        }

        return true;
    }

    private void ajouteActivite() {

        if (!Validator()) return;

        double finalLatitide, finalLongitude;

        if (CK_GPS.isChecked()) {// Récueprel la position de la personne par le GPS
            finalLatitide = Outils.personneConnectee.getLatitude();
            finalLongitude = Outils.personneConnectee.getLongitude();
        } else {
            finalLatitide = latitudeSaisie;
            finalLongitude = longitudeSaisie;
        }

        //  Calendar datedebut;
        int idtypeactivite = ((TypeActivite) spinnertypeactivite.getSelectedView().getTag()).getId();
        //  datedebut = (Calendar) TV_datedebut.getTag();
        //   calculHeureFin();// Determine les heures de début et fin de l'alerte et de l'activite

        String adresse = mAutocompleteTextView.getHint().toString();
        int nbmaxwaydeur = SP_NbxMaxWaydeurs.getSelectedItemPosition() + 2;
        int idorganisateur = Outils.personneConnectee.getId();

        new AsyncTaches.AsyncAddActivite(this, titre, libelle,
                idorganisateur, dureeBalise,
                idtypeactivite, finalLatitide, finalLongitude, adresse,
                nbmaxwaydeur, dureeActivite, Outils.jeton,
                ProposeActivites.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void loopBack_AddActivite(MessageServeur messageserveur) {

        if (messageserveur != null) {
            if (messageserveur.isReponse()) {

                int idactivite = Integer.parseInt(messageserveur.getMessage());// Récupére le n° de l'activite créee par le serveur
                if (idactivite != 0) {
                    Toast toast =Toast.makeText(getBaseContext(), R.string.proposeactivite_creationactivite, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent appel = new Intent(ProposeActivites.this, DetailActivite.class);
                    appel.putExtra("idactivite", idactivite);
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(appel);
                    finish();
                }

            } else // Retour du message négatif
            {
                Toast toast =Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }

        }

    }



    protected void onDestroy() {
        Outils.gps.removeListenerGPS(this);
        super.onDestroy();
    }

    @Override
    public void loopBackChangePositionGps(LatLng latLng) {
        if (CK_GPS.isChecked()) updateAdresseGps(latLng);
    }



    protected void onResume() {
        super.onResume();

    }

    private void modifieDescription() {// Ouvre le popup pour la saisie du profil

        // Récupere le layour et le champ profil dans le layout modifie profil
        LayoutInflater factory = LayoutInflater.from(ProposeActivites.this);
        View alertDialogView = factory.inflate(R.layout.popup_commentaireactivite, null);
        final EditText ET_Commentaires = (EditText) alertDialogView.findViewById(R.id.commentaire);
        //**************************************
        ET_Commentaires.setText(TV_Commentaires.getText().toString());// Charge avec la valeur de la description
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(alertDialogView);
        adb.setTitle(R.string.DecritActivite_Titre);
        adb.setIcon(android.R.drawable.ic_menu_edit);
        adb.setPositiveButton(R.string.DecritActivite_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TV_Commentaires.setText(ET_Commentaires.getText().toString());

            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        adb.setNegativeButton(R.string.DecritActivite_No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Lorsque l'on cliquera sur annuler on quittera l'application

            }
        });
        AlertDialog alertDialog = adb.create();
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


}