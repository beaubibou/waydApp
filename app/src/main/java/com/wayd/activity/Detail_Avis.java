package com.wayd.activity;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Avis;
import com.wayd.bean.Outils;

public class Detail_Avis extends MenuDrawerNew implements  AsyncTaches.AsyncGetAvis.AsyncGetAvisListener{
    //private TextView TV_titreActivite;
    private TextView TV_commentaire,TV_DateAvis;

    private RatingBar RT_note;
    private ImageView IMG_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_avis);

        InitDrawarToolBar();
        initTableauDeBord();
        int idnoter = getIntent().getIntExtra("idnoter", 0);
        int idactivite = getIntent().getIntExtra("idactivite", 0);
        int idnotateur = getIntent().getIntExtra("idnotateur", 0);
        int idpersonnenotee = getIntent().getIntExtra("idpersonnenotee", 0);

        //     TV_titreActivite = (TextView) findViewById(R.id.titreactivite);
        TV_commentaire = (TextView) findViewById(R.id.commentaire);
        TV_DateAvis = (TextView) findViewById(R.id.dateavis);
        RT_note = (RatingBar) findViewById(R.id.note);
        IMG_photo = (ImageView) findViewById(R.id.iconactivite);

        new AsyncTaches.AsyncGetAvis(this, idnoter, idactivite, idnotateur, idpersonnenotee, Outils.personneConnectee.getId(),Detail_Avis.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void loopBack_GetAvis(Avis result) {
        if (result != null) {

            //     TV_titreActivite.setText(result.getTitreactivite());
            TV_DateAvis.setText(result.getDateNotationStr());
            TV_commentaire.setText(result.getAvis());
            RT_note.setIsIndicator(true);
            RT_note.setStepSize(Float.valueOf("0.5"));
            RT_note.setRating(Float.valueOf(Double.toString(result.getNote())));
            IMG_photo.setImageBitmap(result.getPhotonotateur());

        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }


}
