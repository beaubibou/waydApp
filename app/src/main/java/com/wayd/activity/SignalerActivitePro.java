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

public class SignalerActivitePro extends MenuNoDrawer implements AsyncTaches.AsyncSignalActivite.Async_SignalActiviteListener {


    private final int SUSPECTE = 0, DANGEREUSE = 1, ILLICITE = 2, GRATUITE_PAYANTE = 3, AUTRES = 4;
    private int idactivite, idmotif;
    private String motif;
    private String titreActivite, libelleActivite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signaleractivitespro);
        InitDrawarToolBar();
        initTableauDeBord();
        /* ***********************Recupere de l'intent emetteur************/

        idactivite = getIntent().getIntExtra("idactivite", 0);
        titreActivite = getIntent().getStringExtra("titreActivite");
        libelleActivite = getIntent().getStringExtra("libelleActivite");

        TextView TV_Suspecte = (TextView) findViewById(R.id.suspecte);
       TextView TV_Dangereuse = (TextView) findViewById(R.id.dangereuse);
        TextView TV_Illicite = (TextView) findViewById(R.id.illicite);
        TextView TV_Gratuite_Payante = (TextView) findViewById(R.id.gratuite_payante);
        TextView TV_Autres = (TextView) findViewById(R.id.autres);
       TextView TV_Titre = (TextView) findViewById(R.id.titreactivite);
       TV_Titre.setText(getString(R.string.SignalerActivite_description) + titreActivite);
           TV_Suspecte.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                idmotif = SUSPECTE;

                dialogConfirmation();
            }
       });


       TV_Dangereuse.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View v) {
               idmotif = DANGEREUSE;
              dialogConfirmation();
           }
        });


       TV_Illicite.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                idmotif = ILLICITE;
                dialogConfirmation();
            }
        });


        TV_Gratuite_Payante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idmotif = GRATUITE_PAYANTE;
                dialogConfirmation();
           }
       });


        TV_Autres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idmotif = AUTRES;
                dialogRaison();

            }
        });

//
    }


    private void dialogConfirmation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignalerActivitePro.this);
        builder.setTitle(R.string.SignalerActivite_Titre);
        builder.setMessage(R.string.SignalerActivite_Message);
        builder.setPositiveButton(R.string.SignalerActivite_OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                signaleActivite();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.SignalerActivite_No, new DialogInterface.OnClickListener() {

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

    private void dialogRaison() {// Ouvre le popup pour la saisie du profil

        // Récupere le layour et le champ profil dans le layout modifie profil
        LayoutInflater factory = LayoutInflater.from(SignalerActivitePro.this);
        View alertDialogView = factory.inflate(R.layout.pop_signaleractivite, null);
        final EditText ET_Commentaires = (EditText) alertDialogView.findViewById(R.id.commentaire);
        //**************************************
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(alertDialogView);
        adb.setTitle(R.string.SignalActiviteMotif_Message);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton(R.string.SignalActiviteMotif_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                motif = ET_Commentaires.getText().toString().trim();

                if (motif.isEmpty()) {

                    Toast toast = Toast.makeText(getBaseContext(), R.string.SignalerActivite_ErreurRaisonVide, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;

                } else {

                    dialogConfirmation();
                }


            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        adb.setNegativeButton(R.string.SignalActiviteMotif_No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Lorsque l'on cliquera sur annuler on quittera l'application

            }
        });
        AlertDialog alertDialog = adb.create();
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

    private void signaleActivite() {
      new AsyncTaches.AsyncSignalActivite(this, Outils.personneConnectee.getId(), idactivite, idmotif, motif, titreActivite, libelleActivite, SignalerActivitePro.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void loopBack_SignalActivite(MessageServeur messageserveur) {

        if (messageserveur != null) {
            Toast toast = Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        }


    }
}
