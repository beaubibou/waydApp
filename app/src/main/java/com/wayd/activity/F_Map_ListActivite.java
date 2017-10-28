package com.wayd.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.application.wayd.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wayd.bean.Activite;
import com.wayd.bean.CritereRechercheActivite;

import com.wayd.bean.Outils;
import com.wayd.bean.Profil;

import java.util.ArrayList;

public class F_Map_ListActivite extends SupportMapFragment implements
        OnMapReadyCallback {

    private final ArrayList<Activite> listeActivite = new ArrayList<>();
    private GoogleMap googleMap;
    private Button B_recherche;
    double latitude, longitude;
    public static final int CENTRER_PERSONNE = 0, CENTRER_CARTE = 1,CENTRER_NOCHANGE=3;
    private CritereRechercheActivite critereRechercheActivite;

    public F_Map_ListActivite() {

        critereRechercheActivite=new CritereRechercheActivite( RechercheActiviteNew.motCle,RechercheActiviteNew.TOUTE_ACTIVITE, RechercheActiviteNew.RAYON_RECHERCHE_DEFAUT,0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.my_layout,
                container, false);

        View vue = super.onCreateView(inflater, container, savedInstanceState);
        layout.addView(vue, 0);
        B_recherche = (Button) layout.findViewById(R.id.recherche);
        B_recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng centre = googleMap.getCameraPosition().target;
                latitude = centre.latitude;
                longitude = centre.longitude;

                CritereRechercheActivite filtre = ((RechercheActiviteNew) getActivity()).getCritereRechercheActivite();// Recupere le filtre du formulaire
                filtre.setPosition(latitude, longitude);// Met les coordonnées du centre de la carte
                ((RechercheActiviteNew) getActivity()).updateListeActivite(RechercheActiviteNew.FROM_MAP,CENTRER_CARTE);// Lance le filtre

                //   new Map_ListeActivity.getListActivite().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Log.d("F_MapListactivite", latitude + " " + longitude);
            }
        });
        return layout;

    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (googleMap == null) {

            getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

        setUpMap();
    }

    private void setUpMap() {

           googleMap.getUiSettings().setMapToolbarEnabled(false);
    }

    public void updateListe(int centrerSur) {
        listeActivite.clear();
        listeActivite.addAll(((RechercheActiviteNew) getActivity()).getListeActivite());
        AfficheActivite(centrerSur);
    }


    private void AfficheActivite(int centrerSur) {

        googleMap.clear();

        float taillePixel=convertDpToPixel(25f,getActivity().getBaseContext());
        for (Activite activite : listeActivite) {
            LatLng activitePosition = new LatLng(activite.getLatitude(), activite.getLongitude());

           Bitmap icon = BitmapFactory.decodeResource(getResources(),Outils.getActiviteMipMap(activite.getIdTypeActite(),activite.getTypeUser()));
            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.
                    fromBitmap(getResizedBitmap(icon,(int)taillePixel,(int)taillePixel))).position(activitePosition).snippet(activite.getPseudoOrganisateur())
                    .title(activite.getTitre())).setTag(activite);

        }
        // AFFICHE MA POSITION

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng lat = googleMap.getCameraPosition().target;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                // TODO Auto-generated method stub

                Activite activite=(Activite) marker.getTag();

                if (activite == null) return ;

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



                //if ((int) marker.getTag() == 0) return;

                //Intent appel = new Intent(getActivity(), DetailActivite.class);
               // appel.putExtra("idactivite", (int) marker.getTag());
               // appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //startActivity(appel);

            }
        });

        LatLng maposition;

        switch (centrerSur) {

            case CENTRER_PERSONNE:
                maposition = new LatLng(Outils.personneConnectee.getPosition().latitude, Outils.personneConnectee.getPosition().longitude);
                googleMap.addMarker(new MarkerOptions().position(maposition).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setTag(0);
                int ZOOM_DEFAUT = 13;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Outils.personneConnectee.getPosition().latitude, Outils.personneConnectee.getPosition().longitude), ZOOM_DEFAUT));


                break;
            case CENTRER_CARTE:

                maposition = googleMap.getCameraPosition().target;
                //   LatLng maposition = new LatLng(Outils.personneConnectee.getLatitude(), Outils.personneConnectee.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(maposition).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setTag(0);

                break;


        }

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static float convertDpToPixel(float dp, Context context){

        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }




}