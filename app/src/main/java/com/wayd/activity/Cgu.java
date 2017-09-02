package com.wayd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.application.wayd.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.wayd.bean.Outils;

public class Cgu extends MenuNoDrawer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cgu);
        WebView webView;
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://wayd.fr/apropos/cgu/cgu.html");
        Button B_validerCgu=(Button)findViewById(R.id.valider_cgu);
        RelativeLayout layoutCgu=(RelativeLayout) findViewById(R.id.layout_validecgu);

        if (Outils.personneConnectee==null){
            layoutCgu.setVisibility(View.GONE);// efface le bouton valide cgu
            return;
        }

        if (!Outils.personneConnectee.isPremiereconnexion())
        {
           layoutCgu.setVisibility(View.GONE);// efface le bouton valide cgu
        }

        B_validerCgu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appel = new Intent(Cgu.this, PremiereConnexion.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        if (Outils.personneConnectee==null){

            super.onBackPressed();
            return;
        }

        if (Outils.personneConnectee.isPremiereconnexion()) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Outils.connected = false;

            Outils.personneConnectee.Raz();
            if (Outils.tableaudebord!=null)
            Outils.tableaudebord.Raz();
            Outils.fermeActiviteEnCours(this);
            if (Outils.principal!=null) Outils.principal.finish();
            finish();
       }
       else

      super.onBackPressed();

    }

}
