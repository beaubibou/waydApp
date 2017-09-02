package com.wayd.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.application.wayd.R;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;


public class AmeliorerWayd extends MenuNoDrawer implements AsyncTaches.AsyncAddSuggestion.Async_AddSuggestionListener {

    private String suggestion;
    private EditText ET_Suggestions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signaleramelioration);
        InitDrawarToolBar();
        initTableauDeBord();
        Button B_validerSuggestion = (Button) findViewById(R.id.validersuggestion);
        B_validerSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validator())dialogAjouteSuggestion();
            }
        });
        ET_Suggestions  = (EditText) findViewById(R.id.listesouci);
    }

    @Override
    public void loopBack_AddSuggestion(MessageServeur messageserveur) {
        if (messageserveur!=null){
            Toast toast=null;
            if (messageserveur.isReponse()){
                 toast =Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                finish();
            }else
            {
                 toast =Toast.makeText(getBaseContext(), "Une erreur inconnu est arrivée", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                        }


        }
    }

       private void dialogAjouteSuggestion() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AmeliorerWayd.this);
        builder.setTitle(R.string.AjouteSuggestion_Titre);
        builder.setMessage(R.string.AjouteSuggestion_Message);
        builder.setPositiveButton(R.string.AjouteSuggestion_OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                // ************************************************
              ajouteSuggestion();
              //  new WS_ajouteSuggestion().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.AjouteSuggestion_No, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing

              //  new WS_noter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                   android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                   android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
           );
           params.setMargins(15, 0, 15, 0);
           buttonOui.setLayoutParams(params);
    }

    private void ajouteSuggestion() {


        new AsyncTaches.AsyncAddSuggestion(this,suggestion, Outils.personneConnectee.getId(),AmeliorerWayd.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private boolean validator() {

        suggestion = ET_Suggestions.getText().toString().trim();
        if (suggestion.isEmpty())return false;
       // note = RT_Note.getRating();
        return true;

    }



}
