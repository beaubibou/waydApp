package com.wayd.bean;

import android.Manifest;
import android.app.Activity;
import android.app.Service;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.application.wayd.R;
import com.google.android.gms.maps.model.LatLng;
import com.wayd.main.MainActivity;
import com.wayd.webservice.Wservice;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    private  Activity activite;
    // flag for GPS status
    private boolean canGetLocation = false;

    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 30; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15 * 1; // 1 minute

    // Declaring a Location Manager
    public LocationManager locationManager;
    private final ArrayList<positionGpsListener> mPositionGpsListeners = new ArrayList<>();


    public GPSTracker(Context context, Activity activite) {
        this.mContext = context;
        this.activite = activite;
        getLocation();

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

          //  Log.d("GpsTracker","isGPSEnabled="+isGPSEnabled);
            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

           // Log.d("GpsTracker","isNetworkEnable="+isNetworkEnabled);
            boolean isPassiveProvider = locationManager
                    .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

        //    Log.d("GpsTracker","isPassiveProvider="+isPassiveProvider);

            this.canGetLocation = (isGPSEnabled || isNetworkEnabled );
            Log.d("GpsTracker","canGetLocation="+canGetLocation);
                     if (ActivityCompat.checkSelfPermission(activite, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(activite, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        ActivityCompat.requestPermissions(activite,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                1);

                       }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("Network", "Network");

                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                             latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            new WS_UpdatePosition().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        }

                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                new WS_UpdatePosition().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(activite, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activite, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(activite,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                ActivityCompat.requestPermissions(activite,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        2);
            }
            locationManager.removeUpdates(GPSTracker.this);
            locationManager=null;
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.d("GpsTracker","isGPSEnabled="+isGPSEnabled);
        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("GpsTracker","isNetworkEnable="+isNetworkEnabled);
        boolean isPassiveProvider = locationManager
                .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

        Log.d("GpsTracker","isPassiveProvider="+isPassiveProvider);

        return  (isGPSEnabled || isNetworkEnabled );
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(final Context moncontext, final Activity activites){
        AlertDialog.Builder adb = new AlertDialog.Builder(activites);

        // Setting Dialog Title
        adb.setTitle(R.string.GpsInfo_Titre);

        // Setting Dialog Message
        adb.setMessage(R.string.GpsInfo_Message);

        // On pressing Settings button
        adb.setPositiveButton(R.string.GpsInfo_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                moncontext.startActivity(intent);

            }
        });

        // on pressing cancel button
        adb.setNegativeButton(R.string.GpsInfo_No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        AlertDialog alertDialog = adb.create();
        alertDialog.show();
       // Button buttonNon = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
     //   buttonNon.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.altertDialog_Fondbutton));
      //  buttonNon.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.altertDialog_Textbutton));
      //  Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
       // buttonOui.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.altertDialog_Fondbutton));
       // buttonOui.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.altertDialog_Textbutton));
       // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
         //       LinearLayout.LayoutParams.WRAP_CONTENT,
         //       LinearLayout.LayoutParams.WRAP_CONTENT
      //  );
      //  params.setMargins(15, 0, 15, 0);
      //  buttonOui.setLayoutParams(params);
    }

    @Override
    public void onLocationChanged(Location location) {
      latitude=location.getLatitude();
      longitude=location.getLongitude();

      LatLng latLng=new LatLng(latitude,longitude)  ;
       for (positionGpsListener ecouteur:mPositionGpsListeners)
       ecouteur.loopBackChangePositionGps(latLng);
        Log.d("GSP tracker","on location change");
       new WS_UpdatePosition().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("GSP tracker","DEscativiation provider");
          this.canGetLocation = false;
    }

    @Override
    public void onProviderEnabled(String provider) {
        this.canGetLocation = true;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Log.d("GSP tracker","Status change ");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void clearListener() {
        mPositionGpsListeners.clear();
    }

    public void setActivite(MainActivity activite) {
        this.activite = activite;
    }


    public interface positionGpsListener
    {
        void loopBackChangePositionGps(LatLng latLng);

    }

    public void addListenerGPS(positionGpsListener positionGpsListener){

        mPositionGpsListeners.add(positionGpsListener);

    }

    public void removeListenerGPS(positionGpsListener positionGpsListener){

        mPositionGpsListeners.remove(positionGpsListener);
    }

    private class WS_UpdatePosition extends AsyncTask<String, String, Avis> {

        @Override
        protected Avis doInBackground(String... params) {

            try {

                new Wservice().updatePosition(Outils.personneConnectee.getId(),latitude,longitude);

            } catch (IOException | XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

            return null;

        }



    }



}
