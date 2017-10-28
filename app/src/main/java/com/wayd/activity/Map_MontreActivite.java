package com.wayd.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.application.wayd.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wayd.bean.Outils;

public class Map_MontreActivite extends MenuNoDrawer implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.montre_activite);
        initTableauDeBord();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            // true, then it has handled the app icon touch event
        }


        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
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

        LatLng maposition = Outils.personneConnectee.getPositionLatLngGps();
        double actlatitude=getIntent().getDoubleExtra("latitude",47);
        double actlongitude=getIntent().getDoubleExtra("longitude",3);
        int   typeActivite=getIntent().getIntExtra("typeActivite",0);
        int   typeUser=getIntent().getIntExtra("typeUser",0);

        float taillePixel=convertDpToPixel(25f,getBaseContext());

        LatLng actposition=new LatLng(actlatitude,actlongitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actposition,12));
        MarkerOptions marker = new MarkerOptions().position(maposition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

      //  MarkerOptions activitemarker = new MarkerOptions().position(actposition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        Bitmap icon = BitmapFactory.decodeResource(getResources(),Outils.getActiviteMipMap(typeActivite,typeUser));
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.
                fromBitmap(getResizedBitmap(icon,(int)taillePixel,(int)taillePixel))).position(actposition))         ;

        googleMap.addMarker(marker);
     //   googleMap.addMarker(activitemarker);


       // mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
    //    {
         //   @Override
         //   public void onMapClick(LatLng arg0)
         //   {mMap.clear();
       //     marker.position(arg0);
          //      maposition=arg0;
            //    mMap.addMarker(marker);
             //   getIntent().putExtra("latitude", arg0.latitude);
             //   getIntent().putExtra("longitude", arg0.longitude);
              //  getIntent().putExtra("changepos", true);
             //   setResult(1001,getIntent());


   //         }
     //   });


    }

    public static float convertDpToPixel(float dp, Context context){

        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
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

}
