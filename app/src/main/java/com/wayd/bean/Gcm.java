package com.wayd.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wayd.webservice.Wservice;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by bibou on 01/01/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Gcm {
    private static final String REG_ID = "regId";
    private static Context mcontext;
    private String regId;
    private final int idpersonne;


    public Gcm(int idpersonne,Context mcontext ){

        this.idpersonne=idpersonne;
        Gcm.mcontext =mcontext;
    }


    public void updatePersonneGcm(){

        regId= FirebaseInstanceId.getInstance().getToken();// Possible garce à déclaration des services
        new WS_updateGCM().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    private class WS_updateGCM extends AsyncTask<String, String, Personne> {

        @Override
        protected Personne doInBackground(String... params) {

            try {

                new Wservice().updateGCM(idpersonne, regId);

            } catch (IOException e) {

                e.printStackTrace();
                return null;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }
     /**
         * @see AsyncTask#onPreExecute()
         */

    }




}
