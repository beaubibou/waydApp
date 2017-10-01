package com.wayd.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.application.wayd.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wayd.bean.Activite;
import com.wayd.bean.Profil;
import com.wayd.webservice.Wservice;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class Map_ListeActivity extends MenuNoDrawer implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final ArrayList<Activite> listeActivite = new ArrayList<>();
    private double latitude;
    private double longitude;
    private int idtypeactivite;
    private int rayon;
    private String motcle = "";
    private boolean isfiltreactivite = false;
    private int commencedans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initTableauDeBord();
        InitDrawarToolBar();
        latitude = getIntent().getDoubleExtra("latitude", 1);
        longitude = getIntent().getDoubleExtra("longitude", 1);
        idtypeactivite = getIntent().getIntExtra("idtypeactivite", 1);
        motcle = getIntent().getStringExtra("motcle");
        rayon = getIntent().getIntExtra("rayon", 1);
        isfiltreactivite = idtypeactivite != -1;

        commencedans=getIntent().getIntExtra("commencedans",0);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Button B_rechereZone=  (Button) findViewById(R.id.recherchezone);
        B_rechereZone.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        LatLng centre = mMap.getCameraPosition().target;
        latitude = centre.latitude;
        longitude = centre.longitude;
        new getListActivite().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
});
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getListActivite WSgetListeActivite;
        WSgetListeActivite = new getListActivite();
        WSgetListeActivite.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),12));
    }


    private class getListActivite extends AsyncTask<String, String, ArrayList<Activite>> {

        @Override
        protected ArrayList<Activite> doInBackground(String... params) {
            try {
                if (isfiltreactivite)
                    return new Wservice().getListActiviteAvenir(latitude, longitude, rayon, idtypeactivite, motcle,commencedans);
                else
                    return new Wservice().getListActiviteAvenirNocritere(latitude, longitude, rayon, motcle,commencedans);
            } catch (IOException | XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            return null;

        }

        /**
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Activite> result) {
            mMap.clear();

            if (result != null) {
                listeActivite.clear();
                listeActivite.addAll(result);
            }
            for (Activite activite : listeActivite) {
                LatLng sydney = new LatLng(activite.getLatitude(), activite.getLongitude());
                mMap.addMarker(new MarkerOptions().position(sydney).snippet(activite.getPseudoOrganisateur()).title(activite.getTitre())).setTag(activite);

            }
            // AFFICHE MA POSITION
            LatLng maposition = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(maposition).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setTag(0);

            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    LatLng lat = mMap.getCameraPosition().target;
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    // TODO Auto-generated method stub

                   Activite activite=(Activite) marker.getTag();

                    if (activite == null) return ;

                    Intent appel;
                    switch (activite.getTypeUser()) {

                        case Profil.WAYDEUR:
                            appel = new Intent(Map_ListeActivity.this, DetailActivite.class);
                            appel.putExtra("idactivite", activite.getId());
                            appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivityForResult(appel, DetailActivite.ACTION_DETAIL_ACTIVITE);
                            break;

                        case Profil.PRO:
                            appel = new Intent(Map_ListeActivity.this, DetailActivitePro.class);
                            appel.putExtra("idactivite", activite.getId());
                            appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivityForResult(appel, DetailActivite.ACTION_DETAIL_ACTIVITE);
                            break;

                    }


            //        Intent appel = new Intent(Map_ListeActivity.this, DetailActivite.class);
                   // appel.putExtra("idactivite", (int) marker.getTag());
                  //  appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                  //  startActivity(appel);

                }
            });



        }

    }


}
