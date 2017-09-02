package com.wayd.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.IndicateurWayd;

public class Statistique extends AppCompatActivity implements AsyncTaches.AsyncGetIndicateursWayd.AsyncGetIndicateurListener {

    private TextView TV_totalActivite;
    private TextView TV_totalParticipation,TV_totalInscrit,TV_totalMessagaAmi,TV_totalMessagaActvite;
    private long echantillonage= System.currentTimeMillis();
    private boolean init=true;

    private long refTotalActivite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistique);

        TV_totalActivite = (TextView) findViewById(R.id.totalactivite);
        TV_totalParticipation = (TextView) findViewById(R.id.totalparticipation);
        TV_totalInscrit = (TextView) findViewById(R.id.totalinscrit);
        TV_totalMessagaAmi= (TextView) findViewById(R.id.totalmessageami);
        TV_totalMessagaActvite= (TextView) findViewById(R.id.totalmessageact);
        Button b_rafraichir = (Button) findViewById(R.id.rafraichir);

        b_rafraichir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            rafraichir();
            }


        });

        new AsyncTaches.AsyncGetIndicateursWayd(this,Statistique.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void rafraichir() {

      new AsyncTaches.AsyncGetIndicateursWayd(this,Statistique.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    @Override
    public void loopBack_GetIndicateur(IndicateurWayd indicateurWayd) {

        double TauxParticipation,TauxTotalActivite = 0,TauxTotalMessageAmi,TauxTotalMessageActivite,TAuxTotalInscrit;


        if (init){

       refTotalActivite=indicateurWayd.getNbrTotalActivite();
       init=false;
        }

        else{
           long difference= System.currentTimeMillis()-echantillonage;
           TauxTotalActivite=((double) indicateurWayd.getNbrTotalActivite()-(double)refTotalActivite)*3600*1000/(double)difference;
           refTotalActivite=indicateurWayd.getNbrTotalActivite();
           echantillonage= System.currentTimeMillis();

        }


        TV_totalActivite.setText(String.valueOf(indicateurWayd.getNbrTotalActivite()));
        TV_totalParticipation.setText(String.valueOf(indicateurWayd.getNbrTotalParticipation()));
        TV_totalInscrit.setText(String.valueOf(indicateurWayd.getNbrTotalInscrit()));
        TV_totalMessagaAmi.setText(String.valueOf(indicateurWayd.getNbrTotalMessage()));
        TV_totalMessagaActvite.setText(String.valueOf(indicateurWayd.getNbrTotalMessageByact()));


    }
}
