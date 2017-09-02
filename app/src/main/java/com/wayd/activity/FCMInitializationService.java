package com.wayd.activity;


import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.wayd.bean.Gcm;
import com.wayd.bean.Outils;
import com.wayd.bean.Personne;
import com.wayd.webservice.Wservice;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by bibou on 25/06/2017.
 */

public class FCMInitializationService extends FirebaseInstanceIdService {

    String fcmToken;
    @Override
    public void onTokenRefresh() {
        fcmToken = FirebaseInstanceId.getInstance().getToken();
        serverUpdateGCM();

    }

    public void serverUpdateGCM() {
        new WS_updateGCM().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class WS_updateGCM extends AsyncTask<String, String, Personne> {

        @Override
        protected Personne doInBackground(String... params) {

            try {
                if (Outils.personneConnectee != null)
                    new Wservice().updateGCM(Outils.personneConnectee.getId(), fcmToken);

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
