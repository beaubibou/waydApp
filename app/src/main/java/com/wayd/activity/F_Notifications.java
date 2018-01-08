package com.wayd.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
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
public class F_Notifications extends Fragment implements AsyncTaches.AsyncUpdateNotificationPref.AsyncUpdateNotificationPrefListener, AsyncTaches.AsyncSupprimeCompte.Async_SupprimeCompteListener {


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

        Button BT_SupprimerComtpe = (Button) rootView.findViewById(R.id.supprimercompte);

        BT_SupprimerComtpe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmationSuppressionComte();
            }
        });

        FloatingActionButton savepref = (FloatingActionButton) rootView.findViewById(R.id.saveprefnotification);
        savepref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Outils.personneConnectee.isNotification() != SW_Notification.isChecked())// Verifie un changement
                    savePref();
            }
        });
        return rootView;

    }

    private void dialogConfirmationSuppressionComte() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.supprimeCompte_Titre);
        builder.setMessage(R.string.supprimeCompte_Message);
        builder.setPositiveButton(R.string.supprimeCompte_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                supprimeCompte();
            }
        });
        builder.setNegativeButton(R.string.supprimeCompte__No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button buttonNon = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.altertDialog_Fondbutton));
        buttonNon.setTextColor(ContextCompat.getColor(getContext(), R.color.altertDialog_Textbutton));
        Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOui.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.altertDialog_Fondbutton));
        buttonOui.setTextColor(ContextCompat.getColor(getContext(), R.color.altertDialog_Textbutton));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 0, 15, 0);
        buttonOui.setLayoutParams(params);

    }

    private void supprimeCompte() {
        new AsyncTaches.AsyncSupprimeCompte(this, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void savePref() {

        boolean notification = SW_Notification.isChecked();
        new AsyncTaches.AsyncUpdateNotificationPref(this, Outils.personneConnectee.getId(), notification, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void loopBack_UpdateNotificationPref(MessageServeur messageserveur, boolean notification) {

        if (messageserveur != null) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), R.string.updateNotificationPref, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            if (messageserveur.isReponse()) Outils.personneConnectee.setNotification(notification);
        }
    }

    @Override
    public void loopBack_SupprimeCompte(MessageServeur messageserveur) {
        if (messageserveur != null) {

            if (messageserveur.isReponse()) {
                Toast toast = Toast.makeText(getActivity().getBaseContext(), R.string.messageCompteSupprime, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                fermeApplication();
            }
        }
    }

    private void fermeApplication() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Outils.connected = false;
        Outils.personneConnectee.Raz();
        Outils.tableaudebord.Raz();
        LoginManager.getInstance().logOut();
        getActivity().finish();
        Outils.principal.finish();
    }
}
