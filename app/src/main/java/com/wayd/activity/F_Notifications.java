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
import android.widget.Switch;
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
public class F_Notifications extends Fragment implements AsyncTaches.AsyncUpdateNotificationPref.AsyncUpdateNotificationPrefListener {


    Switch SW_Notification;


    public F_Notifications() {
    }

    public static Fragment newInstance() {

        return new F_Notifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.f_notifications, container, false);
        SW_Notification = (Switch) rootView.findViewById(R.id.sw_notification);
        SW_Notification.setChecked(Outils.personneConnectee.isNotification());
        FloatingActionButton savepref = (FloatingActionButton) rootView.findViewById(R.id.saveprefnotification);
        savepref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Outils.personneConnectee.isNotification()!=SW_Notification.isChecked())// Verifie un changement
                savePref();
            }
        });
        return rootView;

    }

    private void savePref() {

        boolean notification = SW_Notification.isChecked();
        new AsyncTaches.AsyncUpdateNotificationPref(this, Outils.personneConnectee.getId(), notification, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void loopBack_UpdateNotificationPref(MessageServeur messageserveur, boolean notification) {

        System.out.println("***************************************"+notification);
        if (messageserveur != null) {
            System.out.println("***************************************"+notification);
            Toast toast = Toast.makeText(getActivity().getBaseContext(), R.string.updateNotificationPref, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            if (messageserveur.isReponse()) Outils.personneConnectee.setNotification(notification);
        }
    }
}
