package com.wayd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PremiereConnexion extends MenuNoDrawer implements AsyncTaches.AsyncUpdatePseudo.AsyncUpdatePseudoListener {

    private static final int ANNEE_ANNIVERSAIRE = 1990;
    private static final int MOIS_ANNIVERSAIRE = 1;
    private static final int JOUR_ANNIVERSAIRE = 1;
    private EditText TV_Pseudo;
    private String pseudo;
    private int sexe;
    private TextView TV_datenaissance;
    private Long datenaissance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.premiereconnexion);
        TV_Pseudo = (EditText) findViewById(R.id.pseudo);
        TextView TV_PourqouiSexe = (TextView) findViewById(R.id.expliquesexe);
        TV_PourqouiSexe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PremiereConnexion.this);
                builder.setTitle(R.string.InfoPremiereConnexion_Titre);
                builder.setMessage(R.string.InfoPremiereConnexion_Message);
                builder.setPositiveButton(R.string.InfoPremiereConnexion_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                }).setIcon(android.R.drawable.ic_dialog_info);

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
        });


        Button B_validepseudo = (Button) findViewById(R.id.validepseudo);
        initSpinnerSexe();
        initDateNaissance();

        TV_datenaissance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        B_validepseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validator())
                    updatePseudo();

            }
        });

    }

    private void initDateNaissance() {
        TV_datenaissance = (TextView) findViewById(R.id.datedenaissance);
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        Calendar temp = Calendar.getInstance();
        temp.set(ANNEE_ANNIVERSAIRE, MOIS_ANNIVERSAIRE - 1, JOUR_ANNIVERSAIRE);
        TV_datenaissance.setTag(temp.getTime());
        TV_datenaissance.setText(formatDate.format(temp.getTime()));
    }


    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new Outils.DatePickerFragment();
        ((Outils.DatePickerFragment) newFragment).setView(v);
        newFragment.show(PremiereConnexion.this.getSupportFragmentManager(), "datePicker");

    }

    private void updatePseudo() {
        new AsyncTaches.AsyncUpdatePseudo(this, pseudo, datenaissance, sexe, Outils.personneConnectee.getId(), PremiereConnexion.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private boolean validator() {

        pseudo = TV_Pseudo.getText().toString().trim();

        if (pseudo.isEmpty()) {
            TV_Pseudo.setError(getString(R.string.pseudoVide));
            return false;
        }


        if (TV_datenaissance.getTag() == null) {

            TV_datenaissance.setError(getString(R.string.dateNaissanceObligatoire));
            return false;
        } else {
            datenaissance = ((Date) TV_datenaissance.getTag()).getTime();
        }

        return true;
    }


    private void dialogBienvenue() {


        Intent appel = new Intent(PremiereConnexion.this, MainActivity.class);
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);

        Toast toast = Toast.makeText(getBaseContext(),"Bienvenue", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
       // AlertDialog.Builder builder = new AlertDialog.Builder(PremiereConnexion.this);
      //  builder.setTitle("Bienvenue");
     //   builder.setMessage("Super...");
     //   builder.setPositiveButton(R.string.SignalerActivite_OK, new DialogInterface.OnClickListener() {

          // public void onClick(DialogInterface dialog, int which) {

       //         dialog.dismiss();
        //   }
     //   });

    //    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
      //      @Override
         //   public void onCancel(DialogInterface dialog) {
     //           Intent appel = new Intent(PremiereConnexion.this, MainActivity.class);
      //          appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
       //         startActivity(appel);
       //         dialog.dismiss();
      //      }
      //  });

     //   AlertDialog alertDialog = builder.create();
     //   alertDialog.show();
     //   Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
     //   buttonOui.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Fondbutton));
    //    buttonOui.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.altertDialog_Textbutton));
     //   LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
   //             LinearLayout.LayoutParams.WRAP_CONTENT,
  //              LinearLayout.LayoutParams.WRAP_CONTENT
   //     );
       // params.setMargins(15, 0, 15, 0);
      //  buttonOui.setLayoutParams(params);

    }

    @Override
    public void loopBack_UpdatePseudo(MessageServeur result) {
        if (result != null)

        {
            if (result.isReponse()) {
                Outils.personneConnectee.setPseudo(pseudo);
                Outils.personneConnectee.setPremiereconnexion(false);
                Outils.personneConnectee.setDatenaissance(new Date(datenaissance));
                Outils.personneConnectee.setSexe(sexe);
                dialogBienvenue();
            } else {

                TV_Pseudo.setError(result.getMessage());
            }

        }
    }

    private void initSpinnerSexe() {
        Spinner SP_sexe = (Spinner) findViewById(R.id.sexe);
        List<String> listsexe = new ArrayList<>();
        listsexe.add("Femme");
        listsexe.add("Homme");
        listsexe.add("Autre");
        ArrayAdapter<String> sexeAdapter = new ArrayAdapter<>(PremiereConnexion.this, android.R.layout.simple_list_item_1, listsexe);

        SP_sexe.setAdapter(sexeAdapter);
        SP_sexe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sexe = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        SP_sexe.setSelection(0);
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Outils.connected = false;
        Outils.personneConnectee.Raz();
        Outils.tableaudebord.Raz();
        Outils.fermeActiviteEnCours(this);
        finish();
    }


}
