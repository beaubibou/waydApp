package com.wayd.bean;

import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.application.wayd.R;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wayd.busMessaging.BusMessaging;
import com.wayd.webservice.Wservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


public class Outils extends Application {

    public static boolean connected = false;
    public static String jeton = null;

    public static Personne personneConnectee;
    public static GPSTracker gps;
    public static final ReceiverGCM LOOP_BACK_RECEIVER_GCM = new ReceiverGCM();
    public static final ArrayList<TypeActivite> listtypeactivitecomplete = new ArrayList<>();
    public static final ArrayList<TypeActivite> listtypeactiviteWaydeur = new ArrayList<>();
    public static final ArrayList<TypeActivite> listtypeactivitePro = new ArrayList<>();
    public static Version  DERNIERE_VERSION_WAYD=null;

    public static final BusMessaging busMessaging = new BusMessaging();

    public static  ArrayList<TypeActivite> getListTypeActiviteWaydeur(){

        if (listtypeactiviteWaydeur.size()!=0)
    return listtypeactiviteWaydeur;
        for (TypeActivite typeActivite:listtypeactivitecomplete  ) {
            if (typeActivite.getTypeUser() == Profil.WAYDEUR || typeActivite.getTypeUser() == 2)
              if (typeActivite.getId()!=TypeActivite.FACEBOOK)
                listtypeactiviteWaydeur.add(typeActivite);
        }
        return listtypeactiviteWaydeur;
    }

    public static  ArrayList<TypeActivite> getListTypeActivitePro(){

        if (listtypeactivitePro.size()!=0)
            return listtypeactivitePro;
        for (TypeActivite typeActivite:listtypeactivitecomplete  ) {
            if (typeActivite.getTypeUser() == Profil.PRO)
                listtypeactivitePro.add(typeActivite);

        }
        return listtypeactivitePro;
    }
    public static int getIndiceTypeActivite(int idTypeActivite){// renvoi l'indice en fontion de l'id du typeactivite

        for (int f=0;f<listtypeactivitecomplete.size();f++)
        if (listtypeactivitecomplete.get(f).getId()==idTypeActivite)
            return f;
        return 0;

    }

    public static Version getVersionApk(Context context){
        PackageManager manager = context.getPackageManager();


        try {
            Log.d("version","Recupere la version");

            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = info.versionCode;
            String versionName = info.versionName+versionCode;
            String [] decoupe=versionName.split(Pattern.quote("."));
            for (String version:decoupe)
                Log.d("version",version);

            return new Version(Integer.parseInt(decoupe[1]),Integer.parseInt(decoupe[2]),Integer.parseInt(decoupe[3]));

        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
        }
        return null;

    }


    public static Date getDateFromSoapObject(Object objet) {

        if (objet == null) return null;

        try
        {
            return Wservice.getDateFromString(objet.toString());
        } catch (ParseException e) {
            // TODO Auto-generated catch block

            return null;
        }
    }


    public static int getActiviteMipMap(int typeActivite,int typeUser) {
        // Le typeActivite correspond wadyeur ou pro. c'est porté par l'activité


        switch (typeUser) {

            case Profil.WAYDEUR:

                switch (typeActivite) {

                    case TypeActivite.BAR_RESTO:

                        return R.mipmap.ic_barrestorond;

                    case TypeActivite.SPORT:
                        return R.mipmap.ic_sportrnd;

                    case TypeActivite.CULTURE:
                        return R.mipmap.ic_expositionrnd;
                    case TypeActivite.JOUER:
                        return R.mipmap.ic_jeurnd;
                    case TypeActivite.DISCUTER:
                        return R.mipmap.ic_friendsrnd;
                    case TypeActivite.WAYDEURS_DISPO:
                        return R.mipmap.ic_suggestionwayd;
                    case TypeActivite.AUTRE:
                        return R.mipmap.ic_autre;
                    case TypeActivite.ENTRAIDE:
                        return R.mipmap.ic_entraide;

                }

            case Profil.PRO:

                switch (typeActivite) {

                    case TypeActivite.BAR_RESTO:
                        return R.mipmap.ic_barrestopro;

                    case TypeActivite.SPORT:
                        return R.mipmap.ic_sportpro;
                    case TypeActivite.CULTURE:
                        return R.mipmap.ic_expopro;
                    case TypeActivite.JOUER:
                        return R.mipmap.ic_jeupro;
                    case TypeActivite.DISCUTER:
                        return R.mipmap.ic_discuterpro;
                    case TypeActivite.AUTRE:
                        return R.mipmap.ic_autrepro;


                }

            case Profil.CARPEDIEM:
                return R.mipmap.icon_facebook;
        }

        return R.mipmap.ic_expositionrnd;
    }


    public static int getActiviteMipMap(int type) {

        switch (type) {

            case TypeActivite.BAR_RESTO:

                return R.mipmap.ic_barrestorond;

            case TypeActivite.SPORT:
                return R.mipmap.ic_sportrnd;

            case TypeActivite.CULTURE:
                return R.mipmap.ic_expositionrnd;
            case TypeActivite.JOUER:
                return R.mipmap.ic_jeurnd;
            case TypeActivite.DISCUTER:
                return R.mipmap.ic_friendsrnd;
            case TypeActivite.WAYDEURS_DISPO:
                return R.mipmap.ic_suggestionwayd;
            case TypeActivite.AUTRE:
                return R.mipmap.ic_autre;
            case TypeActivite.ENTRAIDE:
                return R.mipmap.ic_entraide;

        }
        return R.mipmap.ic_expositionrnd;
    }



    public static Bitmap redimendiensionnePhoto(Bitmap photo) {
       if (photo!=null) {
           float newheigh = photo.getHeight();
           float newwith = photo.getWidth();
           float ratio = (newwith / newheigh);
           if (newheigh > 200) {

               double finalwith = 200 * ratio;
               double finalheigt = 200;
               return Bitmap.createScaledBitmap(photo, (int) finalwith, (int) finalheigt, true);
           } else
               return photo;
       }
       return photo;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        View viewinit;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
            Calendar temp = Calendar.getInstance();
            temp.set(year, month, day);
            viewinit.setTag(temp.getTime());// Met la tag dans le tag
            ((TextView) viewinit).setText(formatDate.format(temp.getTime()));

        }

        public void setView(View v) {
            viewinit = v;
        }

    }


    public static AppCompatActivity principal;

    public static String getStringWsFromDate(Date date) {
        if (date == null) return "Pas de date";
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(date);
    }

    public static String getStringFromDateCourte(Date date) {
        if (date == null) return "Pas de date";
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
        return formater.format(date);
    }


    public static final TableauBord tableaudebord = new TableauBord(0, 0, 0, 0, 0);

    public static final ArrayList<Activity> activiteEnCours = new ArrayList<>();

    public static void fermeActiviteEnCours(Activity activite_) {

        for (Activity activie : Outils.activiteEnCours) {
            if (activie != activite_) activie.finish();
        }
        activiteEnCours.clear();
    }

    public static Bitmap getPhotoFormString(String photostr) {

        if (photostr.equals("noupdatephoto"))
            return null; // Gere le cas d'un utilisateur qui n'a pas de photo

        byte[] bytes = Base64.decode(photostr, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String encodeTobase64(Bitmap image) {
        if (image == null) return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static boolean isConnectFaceBook() {
        return Profile.getCurrentProfile() != null;
    }

    public static boolean isConnectFromFB() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String typeconnexion = user.getProviders().get(0);
            if (typeconnexion.equals("facebook.com")) return true;
        }
        return false;
    }

    public static boolean isConnectFromPwd() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String typeconnexion = user.getProviders().get(0);
            if (typeconnexion.equals("password")) return true;
        }

        return false;
    }

    public static boolean isConnectFromGoogle() {

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String typeconnexion = user.getProviders().get(0);
            if (typeconnexion.equals("google.com")) return true;
        }

        return false;
    }





    public static boolean isConnect() {

        if (personneConnectee == null) return false;
        return Outils.personneConnectee.getId() != 0;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }





    public static Drawable getCirculaireAvatarDrawable(Context mcontext) {
        if (Outils.personneConnectee.getPhoto() != null) {

            RoundImage roundedImage;
            roundedImage = new RoundImage(Bitmap.createScaledBitmap(Outils.personneConnectee.getPhoto(),
                    Outils.personneConnectee.getPhoto().getWidth()
                    , Outils.personneConnectee.getPhoto().getHeight(), true));

            return roundedImage;
            // photo.setImageBitmap(Bitmap.createScaledBitmap(Outils.personneConnectee.getPhoto(), 200, 200, true));

        } else {
            Bitmap photoinconnu = BitmapFactory.decodeResource(mcontext.getResources(), R.mipmap.ic_inconnu);
            RoundImage roundedImage;
            roundedImage = new RoundImage(Bitmap.createScaledBitmap(photoinconnu, 200, 200, true));
            return roundedImage;
        }

    }


    public static Drawable getAvatarDrawable(Context mcontext, Bitmap photo) {
        if (photo != null) {

            RoundImage roundedImage;
            roundedImage = new RoundImage(Bitmap.createScaledBitmap(photo,
                    photo.getWidth()
                    , photo.getHeight(), true));

            return roundedImage;
            // photo.setImageBitmap(Bitmap.createScaledBitmap(Outils.personneConnectee.getPhoto(), 200, 200, true));

        } else {
            Bitmap photoinconnu = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.com_facebook_profile_picture_blank_square);
            RoundImage roundedImage;
            roundedImage = new RoundImage(Bitmap.createScaledBitmap(photoinconnu, 400, 400, true));
            return roundedImage;
        }
    }





    public static double getDistance(double lat1, double lat2, double lon1,
                                     double lon2) {
        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = 0 ;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);

    }

    public static List<Address> getFromLocation(double lat, double lng, int maxResult){

        String address = String.format(Locale.ENGLISH,"http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="+Locale.getDefault().getCountry(), lat, lng);
        // String address="https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=AIzaSyA_K_75z5BiALmZbNnEHlP7Y7prhXd-vAc";

        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        List<Address> retList = null;

        try {
            response = client.execute(httpGet);

            HttpEntity entity = response.getEntity();

            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);

            }

            JSONObject jsonObject =  new JSONObject(stringBuilder.toString());
            retList = new ArrayList<Address>();


            if("OK".equalsIgnoreCase(jsonObject.getString("status"))){
                JSONArray results = jsonObject.getJSONArray("results");

                for (int i=0;i<results.length();i++ ) {
                    JSONObject result = results.getJSONObject(i);
                  // String indiStr = result.getString("formatted_address");
                    String indiStr = new String(result.getString("formatted_address").getBytes("ISO-8859-1"), "UTF-8");

                    Address addr = new Address(Locale.ITALY);
                    addr.setAddressLine(0, indiStr);
                    retList.add(addr);
                }
            }


        } catch (ClientProtocolException e) {
            Log.e("tewtr", "Error calling Google geocode webservice.", e);
        } catch (IOException e) {
            Log.e("test", "Error calling Google geocode webservice.", e);
        } catch (JSONException e) {
            Log.e("test", "Error parsing Google geocode webservice response.", e);
        }

        return retList;
    }
}