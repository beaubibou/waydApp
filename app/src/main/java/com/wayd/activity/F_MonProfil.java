package com.wayd.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class F_MonProfil extends Fragment implements AsyncTaches.AsyncUpdateProfil.AsyncUpdateProfilListener, AsyncTaches.AsyncSupprimeCompte.Async_SupprimeCompteListener {
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private ImageView IM_photo;
    private TextView ET_Pseudo;
    private TextView TV_Commentaires;
    private TextView TV_datenaissance;
    private CheckBox CK_affichesexe, CK_afficheage;
    private Bitmap photo;
    private String nom, commentaire, pseudo;
    private int sexe;
    private Date datenaissance;
    private boolean affichesexe;
    private boolean afficheage;
    private View alertDialogView;
    private View rootView = null;
    public F_MonProfil() {
    }

    public static Fragment newInstance() {

        return new F_MonProfil();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.f_profil, container, false);
        IM_photo = (ImageView) rootView.findViewById(R.id.avatar);
        initButtonChangeMdp();
        initTvCommentaire();
        TV_datenaissance = (TextView) rootView.findViewById(R.id.datedenaissance);
        initSpinnerSexe();
        initFloatingButtonSaveProfil();// mise à jour du profil;
        TV_datenaissance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
           CK_afficheage = (CheckBox) rootView.findViewById(R.id.ck_activeage);
        CK_affichesexe = (CheckBox) rootView.findViewById(R.id.ck_activesexe);
        photo = Outils.personneConnectee.getPhoto();
        CK_afficheage.setChecked(Outils.personneConnectee.isAfficheage());
        CK_affichesexe.setChecked(Outils.personneConnectee.isAffichesexe());
        IM_photo.setImageDrawable(Outils.getAvatarDrawable(getContext(), Outils.personneConnectee.getPhoto()));
        TV_datenaissance.setText(Outils.getStringFromDateCourte(Outils.personneConnectee.getDatenaissance()));
        TV_datenaissance.setTag(Outils.personneConnectee.getDatenaissance());
        this.datenaissance = Outils.personneConnectee.getDatenaissance();
        ET_Pseudo = (TextView) rootView.findViewById(R.id.pseudo);
        ET_Pseudo.setText(Outils.personneConnectee.getPseudo());
        Button b_photo = (Button) rootView.findViewById(R.id.changephoto);
        Button BT_SupprimerComtpe = (Button) rootView.findViewById(R.id.supprimercompte);

        BT_SupprimerComtpe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmationSuppressionComte();
            }
        });
        b_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //      prendrePhoto();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                buttonNon.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Fondbutton));
                buttonNon.setTextColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Textbutton));
                Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                buttonOui.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Fondbutton));
                buttonOui.setTextColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Textbutton));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(15, 0, 15, 0);
                buttonOui.setLayoutParams(params);

            }
        });

        return rootView;

    }

    private void prendrePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getContext().getPackageManager()) != null) {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
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
    private void initTvCommentaire() {
        TV_Commentaires = (TextView) rootView.findViewById(R.id.commentaires);
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

    private void initSpinnerSexe() {
        Spinner SP_sexe = (Spinner) rootView.findViewById(R.id.sexe);
        List<String> listsexe = new ArrayList<>();
        listsexe.add(0, "Femme");
        listsexe.add(1, "Homme");

        ArrayAdapter<String> sexeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listsexe);
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
        SP_sexe.setSelection(Outils.personneConnectee.getSexe());
    }

    private void initButtonChangeMdp() {
        TextView changermdp = (TextView) rootView.findViewById(R.id.changemdp);

        if (!Outils.isConnectFromPwd()) changermdp.setVisibility(View.INVISIBLE);
        changermdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMdp();
            }
        });
    }

    private void initFloatingButtonSaveProfil() {
        FloatingActionButton BUT_updateProfil = (FloatingActionButton) rootView.findViewById(R.id.saveprofil);
        BUT_updateProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validator())
                    updateProfil();

            }
        });
    }

    private void updateProfil() {

        new AsyncTaches.AsyncUpdateProfil(this, photo,
                nom, pseudo,
                datenaissance, sexe, commentaire, Outils.personneConnectee.getId(), afficheage, affichesexe, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void chargerPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    private void changeMdp() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        alertDialogView = factory.inflate(R.layout.changepwd, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setView(alertDialogView);
        adb.setTitle(R.string.ChangementMDP_Titre);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton(R.string.ChangementMDP_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
                EditText oldpass = (EditText) alertDialogView.findViewById(R.id.oldpass);
                EditText newpass = (EditText) alertDialogView.findViewById(R.id.newpass);
                ChangeFireBaseMdp(oldpass.getText().toString(), newpass.getText().toString());

            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        adb.setNegativeButton(R.string.ChangementMDP_No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Lorsque l'on cliquera sur annuler on quittera l'application

            }
        });

        AlertDialog alertDialog = adb.create();
        alertDialog.show();
        Button buttonNon = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNon.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Fondbutton));
        buttonNon.setTextColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Textbutton));
        Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOui.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Fondbutton));
        buttonOui.setTextColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Textbutton));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 0, 15, 0);
        buttonOui.setLayoutParams(params);

      //  adb.show();

    }

    private void modifieProfil() {// Ouvre le popup pour la saisie du profil

        // Récupere le layour et le champ profil dans le layout modifie profil
        LayoutInflater factory = LayoutInflater.from(getActivity());
        alertDialogView = factory.inflate(R.layout.popup_descriptionprofil, null);
        final EditText ET_Commentaires = (EditText) alertDialogView.findViewById(R.id.profil);
        //**************************************

        ET_Commentaires.setText(TV_Commentaires.getText().toString());// Charge avec la valeur du profil du TextView
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

        adb.setView(alertDialogView);
        adb.setTitle(R.string.DescriptionProfil_Titre);
        adb.setMessage(R.string.DescriptionProfil_Message);
        adb.setIcon(android.R.drawable.ic_menu_edit);

        adb.setPositiveButton(R.string.DescriptionProfil_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TV_Commentaires.setText(ET_Commentaires.getText().toString());

                // Ferme clavier
                InputMethodManager imm = (InputMethodManager)getActivity().
                getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        ET_Commentaires.getWindowToken(), 0);

            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        adb.setNegativeButton(R.string.DescriptionProfil_No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Ferme clavier
                InputMethodManager imm = (InputMethodManager)getActivity().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        ET_Commentaires.getWindowToken(), 0);
                //Lorsque l'on cliquera sur annuler on quittera l'application

            }
        });
        AlertDialog alertDialog = adb.create();
        alertDialog.show();
        Button buttonNon = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNon.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Fondbutton));
        buttonNon.setTextColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Textbutton));
        Button buttonOui = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOui.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Fondbutton));
        buttonOui.setTextColor(ContextCompat.getColor(getContext(),R.color.altertDialog_Textbutton));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 0, 15, 0);
        buttonOui.setLayoutParams(params);

    }

    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new Outils.DatePickerFragment();
        ((Outils.DatePickerFragment) newFragment).setView(v);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            InputStream image_stream;
            try {
                image_stream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                Bitmap tmpphoto = BitmapFactory.decodeStream(image_stream);
                photo = Outils.redimendiensionnePhoto(tmpphoto);
                photo=compressImage(photo);
                IM_photo.setImageBitmap(photo);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photo = Outils.redimendiensionnePhoto(imageBitmap);
            photo=compressImage(photo);
            IM_photo.setImageBitmap(photo);

        }

    }

    public Bitmap compressImage(Bitmap image){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,80,stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        return compressedBitmap;

    }

    @Override
    public void loopBack_UpdateProfil(MessageServeur result) {

        if (result != null) {
            if (result.isReponse()) {

                Toast toast = Toast.makeText(getActivity().getBaseContext(), result.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Outils.personneConnectee.setNom(nom);
                Outils.personneConnectee.setPseudo(pseudo);
                Outils.personneConnectee.setSexe(sexe);
                Outils.personneConnectee.setDatenaissance(datenaissance);
                Outils.personneConnectee.setPhoto(photo);
                Outils.personneConnectee.setCommentaire(commentaire);
                Outils.personneConnectee.setAfficheage(CK_afficheage.isChecked());
                Outils.personneConnectee.setAffichesexe(CK_affichesexe.isChecked());
                Outils.personneConnectee.notifyPersonneChange();// Informe les activity à l"ecoute des changements

            }
        }
    }


    private boolean validator() {

        commentaire = TV_Commentaires.getText().toString().trim();
        pseudo = ET_Pseudo.getText().toString().trim();
     //   if (commentaire.isEmpty()) commentaire = "";

        if (pseudo.isEmpty()) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), R.string.nomObligatoire, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }


        if (TV_datenaissance.getTag() == null) {

            Toast toast = Toast.makeText(getActivity().getBaseContext(), R.string.dateNaissanceObligatoire, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            return false;
        }

        affichesexe = CK_affichesexe.isChecked();
        afficheage = CK_afficheage.isChecked();
        datenaissance = ((Date) TV_datenaissance.getTag());
        return true;
    }

    private void ChangeFireBaseMdp(String oldpass, final String newPass) {

        if (oldpass.isEmpty() || newPass.isEmpty()) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), R.string.mdpObigatoires, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final ProgressDialog mProgressDialog = ProgressDialog.show(getActivity(), "Patientez ...", "Mise jour...", true);
                mProgressDialog.setCancelable(false);

                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (!task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.erreurInconnue) + task.getException().getMessage(), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            } else {
                                mProgressDialog.dismiss();
                                Toast toast = Toast.makeText(getActivity().getBaseContext(), R.string.mdpChange, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    });
                } else {

                    mProgressDialog.dismiss();
                    Toast toast = Toast.makeText(getActivity().getBaseContext(), R.string.echecAuth, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();



                }
            }
        });
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
}
