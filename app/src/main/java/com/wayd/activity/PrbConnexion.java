package com.wayd.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.MessageServeur;


public class PrbConnexion extends AppCompatActivity implements AsyncTaches.AsyncAddPrbConnexion.AsyncAddPrbConnexionListener {

    private String email, probleme;
    private EditText ET_probleme, ET_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signalerbug);

        ET_probleme = (EditText) findViewById(R.id.listesouci);
        ET_mail = (EditText) findViewById(R.id.mail);
        final Button B_Valider = (Button) findViewById(R.id.validersuggestion);
        B_Valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator();
            }
        });


    }

    private void validator() {


        String probleme = ET_probleme.getText().toString().trim();

        if (probleme.isEmpty()) {

            Toast toast = Toast.makeText(getBaseContext(),R.string.mailNonConforme, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            ET_probleme.setError(getString(R.string.problemeObligatoire));
            return;

        }

        String email=ET_mail.getText().toString().trim();

        if (!isValidEmail(email)){

            Toast toast = Toast.makeText(getBaseContext(),R.string.mailNonConforme, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            ET_mail.setError(getString(R.string.mailNonConforme));
            return;
        }


        new AsyncTaches.AsyncAddPrbConnexion(this, probleme,email, PrbConnexion.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void loopBack_AddPrbConnexion(MessageServeur messageserveur) {

        if (messageserveur!=null){

            if (messageserveur.isReponse()){
                Toast toast = Toast.makeText(getBaseContext(),R.string.prbSignale, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                finish();
            }

        }

    }
    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
