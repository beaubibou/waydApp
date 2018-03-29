package com.wayd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.application.wayd.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.wayd.bean.Outils;

public class FaceBookEvenement extends MenuNoDrawer {
    //Test git
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebookevent);
        String lienfacebook= getIntent().getStringExtra("lienfacebook");
        WebView webView;
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(lienfacebook);


    }





}
