package com.wayd.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.ProfilNotation;

import org.apache.commons.lang3.StringEscapeUtils;

public class DonneAvis extends MenuNoDrawer implements AsyncTaches.AsyncAddNotation.Async_AddNotationListener, AsyncTaches.AsyncGetDonneAvisFull.Async_GetDonneAvisListenerFull {

    private EditText ET_Commentaires;
    private TextView TV_nom, TV_age, TV_Titre, TV_NbrCarac;
    private String commentaires;
    private float note;
    private RatingBar RT_Note, RT_NoteProfil;
    private ImageView IV_Photo;
    private int idactivite;
    private int idpersonnenotee;
    private final static int RETOUR_NOTATION = 1030;
    private boolean isAmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donneavis);
        InitDrawarToolBar();
        initTableauDeBord();
        idactivite = getIntent().getIntExtra("idactivite", 0);
        idpersonnenotee = getIntent().getIntExtra("idpersonnenotee", 0);
        ET_Commentaires = (EditText) findViewById(R.id.commentaire);
        IV_Photo = (ImageView) findViewById(R.id.iconactivite);
        RT_Note = (RatingBar) findViewById(R.id.note);
        RT_Note.setMax(5);
        RT_Note.setStepSize(0.5f);
        RT_NoteProfil = (RatingBar) findViewById(R.id.noteprofil);
        RT_NoteProfil.setMax(5);
        RT_NoteProfil.setStepSize(0.5f);
        TV_nom = (TextView) findViewById(R.id.pseudo);
        TV_age = (TextView) findViewById(R.id.ck_activesexe);
        TV_Titre = (TextView) findViewById(R.id.titreactivite);
        TV_NbrCarac = (TextView) findViewById(R.id.nbrcaractere);

        Button BTN_noter = (Button) findViewById(R.id.id_noterprofil);
        BTN_noter.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View v) {
                if (validator()) {
                    if (isAmi)
                        notePersonne(true);// Verifie si il sont déja amis et ne demande pas à être ami
                    else
                        dialogProposeAmi(); // Sinon propose de le mettre
                }

            }
        });

        new AsyncTaches.AsyncGetDonneAvisFull(this, Outils.personneConnectee.getId(), idpersonnenotee, idactivite, DonneAvis.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ET_Commentaires.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    TV_NbrCarac.setText("" + s.length() + "/" + getString(R.string.maxLengtAvis));
                else
                    TV_NbrCarac.setText("");


            }
        });
    }

    private void dialogProposeAmi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DonneAvis.this);
        builder.setTitle(R.string.ajouteAmi_Titre);
        builder.setMessage(R.string.ajouteAmi_Message);
        builder.setPositiveButton(R.string.ajouteAmi_OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                notePersonne(true);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.ajouteAmi_No, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                notePersonne(false);
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button buttonNon = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNon.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Fondbutton));
        buttonNon.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Textbutton));
        Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOui.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Fondbutton));
        buttonOui.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Textbutton));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 0, 15, 0);
        buttonOui.setLayoutParams(params);

    }

    private void notePersonne(boolean ami) {
        new AsyncTaches.AsyncAddNotation(this, idpersonnenotee, idactivite, commentaires, note, ami, DonneAvis.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean validator() {
        commentaires = (StringEscapeUtils.escapeJava(ET_Commentaires.getText().toString())).trim();
        note = RT_Note.getRating();
        return true;

    }

    @Override
    public void loopBack_AddNotation(MessageServeur messageserveur) {

        Toast toast = Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        // Renvoi à l'activité le fait que la note à été donnée pour la suppression
        getIntent().putExtra("supprime", true);
        getIntent().putExtra("idactivite", idactivite);
        getIntent().putExtra("idpersonnenotee", idpersonnenotee);
        setResult(RETOUR_NOTATION, getIntent());
        finish();
    }

    @Override
    public void loopBack_GetDonneAvisFull(ProfilNotation profil, Activite activite) {
        if (profil != null) {
            IV_Photo.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(), profil.getPhoto()));
            TV_nom.setText(profil.getPseudo());
            TV_age.setText(profil.getAgeStr());
            RT_NoteProfil.setRating((float) profil.getNote());
            isAmi = profil.isAmi();
        }
        if (activite != null) {
            String titre = StringEscapeUtils.unescapeJava(activite.getTitre());
            TV_Titre.setText(titre);
        }
    }
}
