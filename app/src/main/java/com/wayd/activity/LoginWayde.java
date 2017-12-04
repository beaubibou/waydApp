package com.wayd.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wayd.bean.GPSTracker;
import com.wayd.bean.Gcm;
import com.wayd.bean.Outils;
import com.wayd.bean.Personne;
import com.wayd.bean.Profil;
import com.wayd.bean.TableauBord;
import com.wayd.main.MainActivity;
import com.wayd.webservice.Wservice;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class LoginWayde extends AppCompatActivity implements

        GoogleApiClient.OnConnectionFailedListener, AsyncTaches.AsyncConnexionWayd.AsyncConnexionWaydListener {

    private EditText TV_login;
    private EditText TV_mdp;
    private String mail;
    private String email;
    private FirebaseAuth mAuth;
    private LoginWayde.WS_TableauBord threadTdb = null;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialogFb;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // permet de ne pas réouvrir la fenetre login quand l'appli est en background
        if (!isTaskRoot()) {
            finish();
            return;
        }


        setContentView(R.layout.login);
        intSignGoogle();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        gestionAutorisation();// Demande l'autorisation du GPS

        // Si l'utilisateur change de status d authehtification
        // On ne gere que le cas ou il est null - Cas d une deconnexion
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("LoginWayde","Changegment de l etat d'authentication de l'utilisateur") ;

                if (user == null) {
                  Log.d("LoginWayde","Deconnexion de l'user") ;

                    if (threadTdb != null) {
                        threadTdb.arret();
                        threadTdb.cancel(true);
                        Outils.connected = false;
                     //   LoginManager.getInstance().logOut();

                    }
                }
                // ...
            }
        }   ;


        initBouttonConnexionWayd();
        initBouttonNouveCompte();

        TV_login = (EditText)

                findViewById(R.id.ET_login);

        TV_mdp = (EditText)

                findViewById(R.id.ET_pwd);

     initBouttonConnexGoogle();


      initCk_Mdp();

      initMdpOublie();

     testResau();
    initTextView_Apropos();
    initTextView_ProblemeConnexion();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        connexion( currentUser);

        //   ConnexionByUser();

    }

    // Une fois l'utilisateur récupere on récupere le jeton
    private void connexion(final FirebaseUser currentUser) {

        if (currentUser==null)return;
        if (Outils.isConnect())
        Log.i("LoginWayd","methode connexion");


        currentUser.getToken(true)

                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {

                            Outils.jeton = task.getResult().getToken();
                            Outils.connected = true;
                            Log.i("LoginWayd","********************methode connexion isSuccessful"+currentUser.getEmail());

                            connexionWayd();
                            return;

                        } else {

                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                        }

                    }
                });

    }



    private void initTextView_Apropos() {
        TextView TV_Apropos = (TextView) findViewById(R.id.apropos);
        TV_Apropos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appel = new Intent(LoginWayde.this,
                        Apropos.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);

            }
        });
    }

    private void initTextView_ProblemeConnexion() {

        TextView TV_PrbConnexion = (TextView) findViewById(R.id.prbconnexion);
        TV_PrbConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appel = new Intent(LoginWayde.this,
                        PrbConnexion.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);

            }
        });
    }

    private void testResau() {
        if (!
                isOnline())

        {
            afficheMessageSnack();
        }
    }

    private void afficheMessageSnack() {
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Une connexion Internet est nécéssaire",
                Snackbar.LENGTH_LONG);
        mySnackbar.getView().setBackgroundColor(Color.parseColor("#F44336"));
        View view = mySnackbar.getView();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        mySnackbar.show();
    }

    private void initBouttonConnexionWayd() {
        Button BUT_Connexion = (Button) findViewById(R.id.button_loginwayde);
        BUT_Connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validator()) ConnexionByUser();
            }
        });
    }


    private void initBouttonNouveCompte() {
        TextView BUT_nouveaucompte = (TextView) findViewById(R.id.button_nouveaucompte);
        TextView BUT_PasEncoreDecompte = (TextView) findViewById(R.id.pasencorecompte);

        BUT_nouveaucompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline()) {
                    afficheMessageSnack();
                    return;
                }
                Intent appel = new Intent(LoginWayde.this,
                        Inscription.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
            }
        });

        BUT_PasEncoreDecompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline()) {
                    afficheMessageSnack();
                    return;
                }
                Intent appel = new Intent(LoginWayde.this,
                        Inscription.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
            }
        });


    }

    private void initBouttonConnexGoogle() {
        Button BTN_GOOGLE = (Button) findViewById(R.id.btn_google);
        BTN_GOOGLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connexionGoogle();
            }
        });
    }

    private void initMdpOublie() {


        TextView TV_mdpoublie = (TextView) findViewById(R.id.id_mdpoublie);
        TV_mdpoublie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline()) {
                    afficheMessageSnack();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginWayde.this);
                builder.setTitle(R.string.DemandeMail_Titre);

                // Set up the input
                final EditText input = new EditText(LoginWayde.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton(R.string.DemandeMail_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mail = input.getText().toString();
                        // if (isValidEmail()

                        if (mail.isEmpty()) {

                            Toast toast = Toast.makeText(LoginWayde.this, R.string.renseignerMail, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();


                            return;
                        }

                        mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast toast = Toast.makeText(LoginWayde.this, R.string.validerLienMail, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {

                                    } catch (FirebaseAuthInvalidUserException e) {

                                        Toast toast = Toast.makeText(LoginWayde.this, R.string.noCompteExist, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();


                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        Toast toast = Toast.makeText(LoginWayde.this, R.string.mdpIncorrect, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();

                                    } catch (Exception e) {

                                        Toast toast = Toast.makeText(LoginWayde.this, R.string.erreurInconnue, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }

                                }
                            }
                        });

                    }
                });
                builder.setNegativeButton(R.string.DemandeMail_No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });
    }

    private void initCk_Mdp() {
        CheckBox CK_AfficheMdp = (CheckBox) findViewById(R.id.id_affmdp);
        CK_AfficheMdp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    TV_mdp.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                if (isChecked) TV_mdp.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

    }

    private void intSignGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                .requestIdToken(getString(R.string.default_web_client_id))

                .requestEmail()

                .build();




        mGoogleApiClient = new GoogleApiClient.Builder(this)

                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)

                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)

                .build();
    }

    private void connexionGoogle() {
        if (!isOnline()) {
            afficheMessageSnack();
            return;
        }

        mProgressDialogFb = ProgressDialog.show(LoginWayde.this, "Authentification Google", "Connexion...", true);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {//
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                return;
            }

        }

        if (mProgressDialogFb != null) mProgressDialogFb.dismiss();



    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            if (mProgressDialogFb != null) mProgressDialogFb.dismiss();
                            mAuth.signOut();
                            System.out.println( task.getException());
                            try {
                                throw task.getException();
                            } catch (Exception e) {

                                Toast toast = Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }


                        }
                        if (task.isSuccessful()){

                            if (mProgressDialogFb != null) mProgressDialogFb.dismiss();
                            Log.d("LoginWayde","Firebase  user google récupéreré");
                            FirebaseUser user = mAuth.getCurrentUser();
                            connexion(user);


                        }


                    }

                });

    }


    private void gestionAutorisation() { //DEMANDE LA PERMISSION A L UTLISATEUR D'UTILSIER LE GPS

        if (ContextCompat.checkSelfPermission(LoginWayde.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            int MY_PERMISSIONS_REQUEST_FINE = 123;
            ActivityCompat.requestPermissions(LoginWayde.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE);

        }

        int permission = ActivityCompat.checkSelfPermission(LoginWayde.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(LoginWayde.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

         permission = ActivityCompat.checkSelfPermission(LoginWayde.this, Manifest.permission.ACCESS_NETWORK_STATE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(LoginWayde.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 129);
        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {// Echec connexion google
        if (mProgressDialogFb != null) mProgressDialogFb.dismiss();

    }

    @Override
    public void loopBack_ConnexionWayd(Personne personne) {

        if (mProgressDialogFb != null) mProgressDialogFb.dismiss();

        if (!isVersionUpdate())return;

        if (personne == null) {

            mAuth.signOut();
            Outils.connected = false;

        }

        if (personne != null) {

            if (personne.getId() != 0) {

                if (mAuth.getCurrentUser().getDisplayName() != null)
                personne.setNom(mAuth.getCurrentUser().getDisplayName());
                personne.setEmail(mAuth.getCurrentUser().getEmail());
                              Outils.personneConnectee = new Personne(personne);
                Log.d("***debug","************personne connetecé");
              // Gere le cas d'un utilisateur professionnel

                if (personne.getTypeUser()== Profil.PRO){
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    Outils.connected = false;
                    Outils.personneConnectee.Raz();
                    Outils.tableaudebord.Raz();
                    infoDialogPro();
                    return;
                }



                if (personne.isPremiereconnexion()) {
                    ouvreTutoriel();
                } else {

                    ouvreMainWayd();
                }


             //   new Gcm(personne.getId(), getBaseContext()).updatePersonneGcm();// à l'occasion refait l'identification
                threadTdb = (LoginWayde.WS_TableauBord) new WS_TableauBord().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            } else // Si l'id de la personne =0;
            {

                Toast toast = Toast.makeText(getBaseContext(), "Echec authentification  " , Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Outils.connected = false;
                mAuth.signOut();

            }

        }


    }

    private void infoDialogPro() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginWayde.this);
        builder.setTitle("Information");
        builder.setMessage("La version pour les professionnel, n'est pas encore finalisée." +
                " Nous t'invitons à aller sur wayd.fr pour poster tes activités");
        builder.setPositiveButton(R.string.ConfirmeParticipation_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private void ouvreMainWayd() {

        Toast toast = Toast.makeText(getBaseContext(), getString(R.string.messageBienvenue)+ Outils.personneConnectee.getPseudo(), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Intent appel = new Intent(LoginWayde.this, MainActivity.class);
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);

    }

    private void ouvreTutoriel() {
        Intent appel = new Intent(LoginWayde.this, Cgu.class);
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);

    }

    // Compare la version dispobile sur le serveur et la version de application
    public boolean isVersionUpdate(){

        if (Outils.getVersionApk(getBaseContext()).isAjour( Outils.DERNIERE_VERSION_WAYD))
            return true;

        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(LoginWayde.this);

        builder.setTitle("Mise à jour ")
                .setMessage("Une mise à jour obligatoire est disponible")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=com.application.wayd"));
                        startActivity(intent);
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        return false;

    }


    private void connexionWayd() {
        Log.i("LoginWayd","methode connexionWayd");
        mProgressDialogFb = ProgressDialog.show(LoginWayde.this, "Connexion Wayd ", "Connexion...", true);
        new AsyncTaches.AsyncConnexionWayd(this, Outils.jeton, LoginWayde.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void ConnexionByUser() {
        email = TV_login.getText().toString();
        String password = TV_mdp.getText().toString();

        if (email.equals("")) {

            Toast toast = Toast.makeText(getBaseContext(), R.string.mailObligatoire, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();


            return;
        }
        if (password.equals("")) {

            Toast toast = Toast.makeText(getBaseContext(), R.string.mdpObligatoire, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }


        firebaseAuthWithPwd(email,password);

    }

    private void firebaseAuthWithPwd(String email,String password) {

        Log.i("LoginWayd","methode firebaseAuthWithPwd");
        mProgressDialogFb = ProgressDialog.show(LoginWayde.this, "Authentification", "Connexion...", true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            mProgressDialogFb.dismiss();
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {

                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast toast = Toast.makeText(getBaseContext(), R.string.noCompteExist, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast toast = Toast.makeText(getBaseContext(), R.string.mdpIncorrect
                                        , Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            } catch (Exception e) {

                            }

                        }
                        if (task.isSuccessful()){

                            mProgressDialogFb.dismiss();
                            Log.i("LoginWayd","methode firebaseAuthWithPwd isSuccessful");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user.isEmailVerified())
                            connexion(user);

                            else
                            {   Toast toast = Toast.makeText(LoginWayde.this, R.string.comptePasActive, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                mAuth.signOut();
                            }

                        }

                        // ...
                    }
                });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }




    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public class WS_TableauBord extends AsyncTask<String, Integer, TableauBord> {
        TableauBord tdb;
        boolean arret = false;

        @Override
        protected TableauBord doInBackground(String... params) {

            while (!arret) {
                if (Outils.personneConnectee.getId() != 0 && !arret)

                    try {
                        tdb = new Wservice().getTableauBord(Outils.personneConnectee.getId());
                        new Wservice().updateNotification(Outils.personneConnectee.getId(),Outils.jeton);
                        publishProgress(50);
                        Thread.sleep(1000 * 30);

                        Log.d("Loginwayde","Lance tdb automatique");
                    } catch (IOException | InterruptedException | XmlPullParserException e) {


            }

            }        //     publishProgress(10);
            //     Thread.sleep(60000);
            return null;

        }

        protected void onProgressUpdate(Integer... values) {
            if (tdb != null) {

                Outils.tableaudebord.initialise(tdb.getNbractiviteencours(),
                        tdb.getNbrmessagenonlu(), tdb.getNbrsuggestion(),
                        tdb.getNbrnotification(), tdb.getNbrami());
            }

        }

        @Override
        protected void onPostExecute(TableauBord tdb) {
            if (tdb != null) {
                Outils.tableaudebord.initialise(tdb.getNbractiviteencours(),
                        tdb.getNbrmessagenonlu(), tdb.getNbrsuggestion(),
                        tdb.getNbrnotification(), tdb.getNbrami());


            }

        }


        /**
         * @see AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

        }

        public void arret() {
            arret = true;
        }
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean validator() {

        email = TV_login.getText().toString();

        if (!isValidEmail(email)) {
            TV_login.setError(getString(R.string.mailNonConforme));
            return false;
        }


        if (!isOnline()) {
            afficheMessageSnack();

            return false;
        }


        return true;

    }

    private boolean isOnline() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() &&networkInfo.isAvailable());


    }

    @Override
    public void onResume() {
        super.onResume();


        if (Outils.isConnect())
        {


            Intent appel = new Intent(LoginWayde.this,
                    MainActivity.class);
            appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(appel);

        }

    }


}
