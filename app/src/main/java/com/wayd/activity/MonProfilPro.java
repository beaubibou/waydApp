package com.wayd.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.Profil;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MonProfilPro extends MenuDrawerNew implements AsyncTaches.AsyncUpdateProfilPro.AsyncUpdateProfilProListener {

    private ImageView IM_photo;
    private TextView TV_pseudo;
    private EditText ET_siret, ET_telephone, ET_pseudo, ET_siteWeb;
    private Profil profilSelectionne;
    private RatingBar ratingBar;
    private TextView TV_Commentaires;
    private Bitmap photo;
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private View alertDialogView;
    private String telephone, siret, siteWeb, pseudo, commentaire;
    int idpersonne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monprofilpro);
        InitDrawarToolBar();
        initTableauDeBord();
        initTvCommentaire();
        idpersonne = getIntent().getIntExtra("idpersonne", 0);
        initFloatingButtonSaveProfil();// mise à jour du profil;
        IM_photo = (ImageView) findViewById(R.id.avatar);
        TV_pseudo = (TextView) findViewById(R.id.pseudo);
        ET_pseudo = (EditText) findViewById(R.id.edit_pseudo);
        ET_siret = (EditText) findViewById(R.id.siret);
        ET_telephone = (EditText) findViewById(R.id.telephone);
        ET_siteWeb = (EditText) findViewById(R.id.siteweb);

        ratingBar = (RatingBar) findViewById(R.id.noteprofil);

        TV_pseudo.setText(Outils.personneConnectee.getPseudo());
        IM_photo.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(), Outils.personneConnectee.getPhoto()));

        ET_siteWeb.setText(Outils.personneConnectee.getSiteWeb());
        ET_pseudo.setText(Outils.personneConnectee.getPseudo());
        ET_telephone.setText(Outils.personneConnectee.getTelephone());
        ET_siret.setText(Outils.personneConnectee.getSiret());
        Button b_photo = (Button) findViewById(R.id.changephoto);

        b_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //      prendrePhoto();
                AlertDialog.Builder builder = new AlertDialog.Builder(MonProfilPro.this);
                builder.setTitle(R.string.ChangerPhoto_Titre)
                        .setItems(R.array.actionphoto, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {

                                    case 0:
                                        prendrePhoto();
                                        break;
                                    case 1:
                                        chargerPhoto();
                                        break;
                                }

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
        });

    }

    private void prendrePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getBaseContext().getPackageManager()) != null) {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_testonglet, menu);
        return true;
    }


    private void chargerPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            InputStream image_stream;
            try {
                image_stream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap tmpphoto = BitmapFactory.decodeStream(image_stream);
                photo = Outils.redimendiensionnePhoto(tmpphoto);
                IM_photo.setImageBitmap(photo);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photo = Outils.redimendiensionnePhoto(imageBitmap);
            IM_photo.setImageBitmap(photo);
        }

    }

    private void initTvCommentaire() {
        TV_Commentaires = (TextView) findViewById(R.id.commentaires);
        TV_Commentaires.setText(Outils.personneConnectee.getCommentaire());
        if (TV_Commentaires.getText().toString().equals(" ")) {
            TV_Commentaires.setText("");
            TV_Commentaires.setHint(R.string.f_profil_description);
        }
        TV_Commentaires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifieProfil();
            }
        });
        TV_Commentaires.setMovementMethod(new ScrollingMovementMethod());


    }

    private void initFloatingButtonSaveProfil() {
        FloatingActionButton BUT_updateProfil = (FloatingActionButton) findViewById(R.id.saveprofil);
        BUT_updateProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validator())
                    updateProfil();

            }
        });
    }

    private void modifieProfil() {// Ouvre le popup pour la saisie du profil

        // Récupere le layour et le champ profil dans le layout modifie profil
        LayoutInflater factory = LayoutInflater.from(MonProfilPro.this);
        alertDialogView = factory.inflate(R.layout.popup_descriptionprofil, null);
        final EditText ET_Commentaires = (EditText) alertDialogView.findViewById(R.id.profil);
        //**************************************

        ET_Commentaires.setText(TV_Commentaires.getText().toString());// Charge avec la valeur du profil du TextView
        AlertDialog.Builder adb = new AlertDialog.Builder(MonProfilPro.this);

        adb.setView(alertDialogView);
        adb.setTitle(R.string.DescriptionProfil_Titre);
        adb.setMessage(R.string.DescriptionProfil_Message);
        adb.setIcon(android.R.drawable.ic_menu_edit);

        adb.setPositiveButton(R.string.DescriptionProfil_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TV_Commentaires.setText(ET_Commentaires.getText().toString());

                // Ferme clavier
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        ET_Commentaires.getWindowToken(), 0);

            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        adb.setNegativeButton(R.string.DescriptionProfil_No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Ferme clavier
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        ET_Commentaires.getWindowToken(), 0);
                //Lorsque l'on cliquera sur annuler on quittera l'application

            }
        });
        AlertDialog alertDialog = adb.create();
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

    private boolean validator() {

        commentaire = TV_Commentaires.getText().toString().trim();
        pseudo = ET_pseudo.getText().toString().trim();
        telephone = ET_telephone.getText().toString().trim();
        siret = ET_siret.getText().toString().trim();
        siteWeb = ET_siteWeb.getText().toString().trim();


        //   if (commentaire.isEmpty()) commentaire = "";

        if (pseudo.isEmpty()) {
            Toast toast = Toast.makeText(getBaseContext(), R.string.nomObligatoire, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }


        return true;
    }

    private void updateProfil() {

        new AsyncTaches.AsyncUpdateProfilPro(this, photo,
                pseudo, telephone, siret, siteWeb,
                commentaire, Outils.personneConnectee.getId(), MonProfilPro.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loopBack_UpdateProfilPro(MessageServeur result) {
        if (result != null) {
            if (result.isReponse()) {

                Toast toast = Toast.makeText(getBaseContext(), result.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                TV_pseudo.setText(pseudo);
                Outils.personneConnectee.setPseudo(pseudo);
                Outils.personneConnectee.setPhoto(photo);
                Outils.personneConnectee.setCommentaire(commentaire);
                Outils.personneConnectee.setSiteWeb(siteWeb);
                Outils.personneConnectee.setSiret(siret);
                Outils.personneConnectee.setTelephone(telephone);
                Outils.personneConnectee.notifyPersonneChange();// Informe les activity à l"ecoute des changements

            }
        }
    }
}
