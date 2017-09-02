package com.wayd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Outils;
import com.wayd.bean.TableauBord;


public class MenuNoDrawer extends AppCompatActivity implements TableauBord.TdbChangeListener {

    private TextView nbrmessagenonlu;
    private TextView nbrnotification;
    private boolean inittdb = false;
    private Activity currentActivite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("");
        this.currentActivite=this;
        if (!getLocalClassName().equals("com.wayde.main.MainActivity"))
          if (!Outils.activiteEnCours.contains(this))  Outils.activiteEnCours.add(this);


        Outils.tableaudebord.addTdbChangeListener(this);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //   if (menu == null) return false;
        //  getMenuInflater().inflate(R.menu.menubarre, menu);

        return true;

    }


    //  private void updateTableauDeBord() {
    //    if (!inittdb) return;

    //    if (Outils.tableaudebord.getNbrmessagenonlu() > 0) {
    //      nbrmessagenonlu.setText("" + Outils.tableaudebord.getNbrmessagenonlu());
    //      nbrmessagenonlu.setBackground(getResources().getDrawable(R.drawable.badge_item_count));
    //  } else {

    //      nbrmessagenonlu.setBackground(null);
    //      nbrmessagenonlu.setText("");

    //   }

    //   if (Outils.tableaudebord.getNbrnotification() > 0) {
    //       nbrnotification.setText("" + Outils.tableaudebord.getNbrnotification());
    //       nbrnotification.setBackground(getResources().getDrawable(R.drawable.badge_item_count));
    //   } else {

    //       nbrnotification.setBackground(null);
    //       nbrnotification.setText("");

    //    }


    //   }

    void initTableauDeBord() {

        nbrmessagenonlu = (TextView) findViewById(R.id.badge_nbrmessagenonlu);
        Button But_nbrmessagenonlu = (Button) findViewById(R.id.tdb_mail);
        But_nbrmessagenonlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getLocalClassName().equals("com.wayd.activity.MesDiscussions")) return;
                Intent appel;
                Outils.fermeActiviteEnCours(currentActivite);
                appel = new Intent(MenuNoDrawer.this,
                        MesDiscussions.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                if (Outils.principal != currentActivite) currentActivite.finish();


            }
        });

        nbrnotification = (TextView) findViewById(R.id.badge_nbrnotification);
        Button But_nbrnotifiation = (Button) findViewById(R.id.tdb_notification);
        But_nbrnotifiation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appel;
                if (getLocalClassName().equals("com.wayd.activity.MesNotifications")) return;
                Outils.fermeActiviteEnCours(currentActivite);
                appel = new Intent(MenuNoDrawer.this,
                        MesNotifications.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                if (Outils.principal != currentActivite) currentActivite.finish();

            }
        });

        Button But_nbrSuggestion = (Button) findViewById(R.id.tdb_suggestion);
        But_nbrSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appel;
                if (getLocalClassName().equals("com.wayd.activity.MesSuggestions")) return;
                Outils.fermeActiviteEnCours(currentActivite);
                appel = new Intent(MenuNoDrawer.this,
                        MesSuggestions.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                if (Outils.principal != currentActivite) currentActivite.finish();

            }
        });


        inittdb = true;// Permet de finaliser l'initialisation.
        // Dans le cas ou un message est recu est que le tableau de bord n'est pas initialisé
        updateTableauBord(Outils.tableaudebord);

    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home://Fleche retour dans la tyoolbar
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    void InitDrawarToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_iconwaydbc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        View view = toolbar.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform actions
                Outils.fermeActiviteEnCours(currentActivite);
                if (Outils.principal != currentActivite) currentActivite.finish();

            }
        });
        initTableauDeBord();
    }




    protected void onDestroy() {
        Outils.tableaudebord.removeTdbChangeListener(this);
        Outils.activiteEnCours.remove(this);
        super.onDestroy();

    }

    @Override
    public void updateTableauBord(TableauBord tableauBord) {
        if (!inittdb) return;

        if (tableauBord.getNbrmessagenonlu() > 0) {
            nbrmessagenonlu.setText(String.valueOf(tableauBord.getNbrmessagenonlu()));
          //  nbrmessagenonlu.setBackground(getResources().getDrawable(R.drawable.badge_item_count));
            nbrmessagenonlu.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_item_count));


        } else {

            nbrmessagenonlu.setBackground(null);
            nbrmessagenonlu.setText("");

        }

        if (tableauBord.getNbrnotification() > 0) {
            nbrnotification.setText(String.valueOf(tableauBord.getNbrnotification()));
     //       nbrnotification.setBackground(getResources().getDrawable(R.drawable.badge_item_count));
            nbrnotification.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_item_count));


        } else {

            nbrnotification.setBackground(null);
            nbrnotification.setText("");

        }

    }
}