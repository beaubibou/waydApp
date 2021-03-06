package com.wayd.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;

public class SignalerProfilPro extends MenuNoDrawer implements AsyncTaches.AsyncSignalProfil.Async_SignalProfilListener {

    private int idpersonne, idmotif=SignalerProfil.COMMENTAIRE;
    private EditText ET_Motif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signalerprofilpro);
        InitDrawarToolBar();
        initTableauDeBord();
        idpersonne = getIntent().getIntExtra("idpersonne", 0);
        String pseudo = getIntent().getStringExtra("pseudo");
        final Button B_Valider = (Button) findViewById(R.id.validersuggestion);
        ET_Motif = (EditText) findViewById(R.id.motif);
        B_Valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if  (validator())
                 dialogConfirmation();
            }
        });

    }

    private boolean validator() {


        if (ET_Motif==null || ET_Motif.getText().toString().trim().isEmpty()) {

            Toast toast = Toast.makeText(getBaseContext(), R.string.SignalerActivite_ErreurRaisonVide, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;

        }

    return true;
    }

    private void dialogConfirmation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignalerProfilPro.this);
        builder.setTitle(R.string.SignalerProfil_Titre);
        builder.setMessage(R.string.SignalerProfil_Message);
        builder.setPositiveButton(R.string.SignalerProfil_OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                signalerProfil();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.SignalerProfil_No, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
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


    private void signalerProfil() {

        new AsyncTaches.AsyncSignalProfil(this, Outils.personneConnectee.getId(), idpersonne,
                idmotif, ET_Motif.getText().toString().trim(), SignalerProfilPro.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    @Override
    public void loopBack_SignalProfil(MessageServeur messageserveur) {
        if (messageserveur!=null){
            Toast toast =Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        }

    }
}
