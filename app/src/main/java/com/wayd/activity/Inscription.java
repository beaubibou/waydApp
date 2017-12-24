package com.wayd.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.application.wayd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Inscription extends AppCompatActivity {


    private EditText ET_mail;
    private EditText ET_pwd;
    private EditText ET_pwdconfirm;
    private String email;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);
        ET_mail = (EditText) findViewById(R.id.id_mail);
        ET_pwd = (EditText) findViewById(R.id.id_pwd);
        ET_pwdconfirm = (EditText) findViewById(R.id.id_pwdconfirm);
        CheckBox CK_AfficheMdp = (CheckBox) findViewById(R.id.id_affmdp);
        CK_AfficheMdp.setChecked(true);
        CK_AfficheMdp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ET_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ET_pwdconfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                } else {
                    ET_pwd.setInputType(InputType.TYPE_CLASS_TEXT);
                    ET_pwdconfirm.setInputType(InputType.TYPE_CLASS_TEXT);

                }

            }
        });
        CK_AfficheMdp.setChecked(false);
        Button BUT_creercompte = (Button) findViewById(R.id.id_creercompte);
        BUT_creercompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validator()) {
                    CreateAccount();


                }
            }

        });
    }

    private void CreateAccount() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
       final ProgressDialog mProgressDialog;
        mProgressDialog = ProgressDialog.show(Inscription.this, "Patientez ...", "Création du compte...", true);
        mProgressDialog.setCancelable(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //     Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {//
                                    mProgressDialog.dismiss();

                                    Toast toast =Toast.makeText(getBaseContext(),R.string.compteCree, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    finish();
                                    mAuth.signOut();
                                }
                            });

                        }

                        if (!task.isSuccessful()) {

                            //  Toast.makeText(getBaseContext(), task.getException().toString()+ "L'inscription a renconté un prb." , Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {


                                Toast toast =Toast.makeText(Inscription.this,R.string.consigenMdp, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();



                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast toast =Toast.makeText(Inscription.this,R.string.mailExistDeja, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                ET_mail.setError(getString(R.string.mailExistDeja));

                            } catch (Exception e) {

                            }


                        }

                        // ...

                    }
                });
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean validator() {

        email = ET_mail.getText().toString();
        password = ET_pwd.getText().toString();
         String confirmPassword=ET_pwdconfirm.getText().toString();



        if (email.isEmpty()) {
            //    Toast.makeText(getBaseContext(), "Mail obligatoire", Toast.LENGTH_SHORT).show();
            ET_mail.setError(getString(R.string.mailObligatoire));
            return false;
        }

        if (!isValidEmail(email)) {
            //  Toast.makeText(getBaseContext(), "Mail non valide", Toast.LENGTH_SHORT).show();
            ET_mail.setError(getString(R.string.mailNonConforme));
            return false;
        }

        if (!ET_pwd.getText().toString().equals(confirmPassword)) {

            // Toast.makeText(getBaseContext(), "Mot de passe non confirmé", Toast.LENGTH_SHORT).show();
            ET_pwd.setError(getString(R.string.mdpDifferent));
            return false;
        }

        if (password.length()<6)
        {
            // Toast.makeText(getBaseContext(), "Mot de passe non confirmé", Toast.LENGTH_SHORT).show();
            ET_pwd.setError(getString(R.string.consigenMdp));
            return false;
        }
        if (confirmPassword.length()<6)
        {
            // Toast.makeText(getBaseContext(), "Mot de passe non confirmé", Toast.LENGTH_SHORT).show();
            ET_pwdconfirm.setError(getString(R.string.consigenMdp));
            return false;
        }

        return true;

    }
}
