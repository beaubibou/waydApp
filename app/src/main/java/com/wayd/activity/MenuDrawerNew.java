package com.wayd.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.wayd.R;
import com.google.firebase.auth.FirebaseAuth;

import com.wayd.bean.Outils;
import com.wayd.bean.Personne;
import com.wayd.bean.Profil;
import com.wayd.bean.TableauBord;


public class MenuDrawerNew extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Personne.PersonneChangeListener, TableauBord.TdbChangeListener {

    protected DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    private static int selectionslide; // Retient le numero du menu selectionné aprés la fermeture du drawerlayout
    private Menu menudrawer;
    private TextView nbrmessagenonlu;
    private TextView nbrnotification, nbrsuggestion;
    private boolean inittdb = false;
    private NavigationView navigationView;

    private final static int RECHERCHE_ACTIVITE = 1;
    private final static int PROPOSE_ACTIVITE = 2;
    private final static int MES_MESSAGES = 4;
    private final static int MES_ACTIVITES = 5;
    private final static int MES_SUGGESTIONS = 6;
    private final static int MA_SPHERE = 7;
    private final static int MES_PREFERENCES = 8;
    private final static int DECONNEXION = 9;
    private final static int AMELIORER = 10;
    private final static int APROPOS = 11;
    private final static int SHARE = 12;
    private Activity currentActivity;
    private MenuItem menu_mesactivite, menu_masphere, menu_rechercher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActivity = this;

        if (!getLocalClassName().equals("com.wayd.main.MainActivity"))
            if (!Outils.activiteEnCours.contains(this)) Outils.activiteEnCours.add(this);

        Outils.personneConnectee.addPersonneChangeListener(this);
        Outils.tableaudebord.addTdbChangeListener(this);


    }

    private void udpateDrawer() {


        menu_mesactivite.setTitle(getString(R.string.menu_drawer_mesactivite) + " (" + Outils.tableaudebord.getNbractiviteencours() + ")");
//        menu_mesmessages.setTitle("Messages(" + Outils.tableaudebord.getNbrmessagenonlu() + ")");
        //  menu_messuggestions.setTitle("Suggestions(" + Outils.tableaudebord.getNbrsuggestion() + ")");
        menu_masphere.setTitle(getString(R.string.menu_drawer_masphere) + " (" + Outils.tableaudebord.getNbrami() + ")");

    }

    private void updateTableauDeBord(TableauBord tableauDeBord) {
        if (!inittdb) return;

        if (tableauDeBord.getNbrmessagenonlu() > 0) {
            nbrmessagenonlu.setText(String.valueOf(tableauDeBord.getNbrmessagenonlu()));
            //    nbrmessagenonlu.setBackground(getResources().getDrawable(R.drawable.badge_item_count));
            nbrmessagenonlu.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_item_count));

        } else {

            nbrmessagenonlu.setBackground(null);
            nbrmessagenonlu.setText("");

        }

        if (tableauDeBord.getNbrnotification() > 0) {
            nbrnotification.setText(String.valueOf(tableauDeBord.getNbrnotification()));
            //   nbrnotification.setBackground(getResources().getDrawable(R.drawable.badge_item_count));
            nbrnotification.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_item_count));


        } else {

            nbrnotification.setBackground(null);
            nbrnotification.setText("");

        }

        if (tableauDeBord.getNbrsuggestion() <= 10) {
            nbrsuggestion.setText(String.valueOf(tableauDeBord.getNbrsuggestion()));
            // nbrsuggestion.setBackground(getResources().getDrawable(R.drawable.badge_item_count));
            nbrsuggestion.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_item_count));

        }
        if (tableauDeBord.getNbrsuggestion() > 10) {
            nbrsuggestion.setText("10+");
            //  nbrsuggestion.setBackground(getResources().getDrawable(R.drawable.badge_item_count));
            nbrsuggestion.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_item_count));

        }

        if (tableauDeBord.getNbrsuggestion() == 0) {

            nbrsuggestion.setBackground(null);
            nbrsuggestion.setText("");

        }


    }

    protected void initTableauDeBord() {

        nbrmessagenonlu = (TextView) findViewById(R.id.badge_nbrmessagenonlu);
        Button But_nbrmessagenonlu = (Button) findViewById(R.id.tdb_mail);
        But_nbrmessagenonlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) return;
                if (getLocalClassName().equals("com.wayd.activity.MesDiscussions")) return;

                Outils.fermeActiviteEnCours(currentActivity);
                Intent appel = new Intent(MenuDrawerNew.this,
                        MesDiscussions.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                if (Outils.principal != currentActivity) currentActivity.finish();

            }
        });

        nbrnotification = (TextView) findViewById(R.id.badge_nbrnotification);
        Button But_nbrnotifiation = (Button) findViewById(R.id.tdb_notification);
        But_nbrnotifiation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) ;
                if (getLocalClassName().equals("com.wayd.activity.MesNotifications")) return;
                Outils.fermeActiviteEnCours(currentActivity);
                Intent appel = new Intent(MenuDrawerNew.this,
                        MesNotifications.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                if (Outils.principal != currentActivity) currentActivity.finish();

            }
        });

        nbrsuggestion = (TextView) findViewById(R.id.badge_nbrsuggestion);
        Button But_nbrSuggestion = (Button) findViewById(R.id.tdb_suggestion);
        But_nbrSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) return;
                if (getLocalClassName().equals("com.wayd.activity.MesSuggestions")) return;
                Outils.fermeActiviteEnCours(currentActivity);
                Intent appel = new Intent(MenuDrawerNew.this,
                        MesSuggestions.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                if (Outils.principal != currentActivity) currentActivity.finish();

            }
        });

        inittdb = true;// Permet de finaliser l'initialisation.
        // Dans le cas ou un message est recu est que le tableau de bord n'est pas initialisé

        updateTableauDeBord(Outils.tableaudebord);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        //    getMenuInflater().inflate(R.menu.menu_liste, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    protected void InitDrawarToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_iconwaydbc);

        //   setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {

                Intent appel;
                switch (selectionslide) {

                    case MES_MESSAGES:
                        selectionslide = -1;
                        if (getLocalClassName().equals("com.wayd.activity.MesDiscussions")) return;
                        Outils.fermeActiviteEnCours(currentActivity);
                        appel = new Intent(MenuDrawerNew.this,
                                MesDiscussions.class);
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(appel);
                        if (Outils.principal != currentActivity) currentActivity.finish();

                        break;
                    case MES_ACTIVITES:
                        selectionslide = -1;
                        if (getLocalClassName().equals("com.wayd.activity.MesActivites")) return;

                        Outils.fermeActiviteEnCours(currentActivity);
                        appel = new Intent(MenuDrawerNew.this,
                                MesActivites.class);
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(appel);
                        if (Outils.principal != currentActivity) currentActivity.finish();

                        break;

                    case MES_SUGGESTIONS:
                        selectionslide = -1;
                        if (getLocalClassName().equals("com.wayd.activity.MesSuggestions")) return;
                        Outils.fermeActiviteEnCours(currentActivity);
                        appel = new Intent(MenuDrawerNew.this,
                                MesSuggestions.class);
                        appel.putExtra("onglet", MesPreferences.ONGLET_PROFIL);// Ouvre l'onglet profil par défaut
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(appel);
                        if (Outils.principal != currentActivity) currentActivity.finish();
                        break;

                    case RECHERCHE_ACTIVITE:
                        selectionslide = -1;
                        if (getLocalClassName().equals("com.wayd.activity.RechercheActiviteNew"))
                            return;
                        Outils.fermeActiviteEnCours(currentActivity);
                        appel = new Intent(MenuDrawerNew.this,
                                RechercheActiviteNew.class);
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(appel);
                        if (Outils.principal != currentActivity) currentActivity.finish();
                        break;

                    case PROPOSE_ACTIVITE:
                        selectionslide = -1;
                        if (getLocalClassName().equals("com.wayd.activity.ProposeActivites"))
                            return;
                        if (getLocalClassName().equals("com.wayd.activity.ProposeActivitesPro"))
                            return;

                        Outils.fermeActiviteEnCours(currentActivity);

                        switch (Outils.personneConnectee.getTypeUser()) {

                            case Profil.WAYDEUR:
                                appel = new Intent(MenuDrawerNew.this, ProposeActivites.class);
                                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivityForResult(appel, DetailActivite.ACTION_DETAIL_ACTIVITE);
                                break;

                            case Profil.PRO:
                                appel = new Intent(MenuDrawerNew.this, ProposeActivitesPro.class);
                                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivityForResult(appel, DetailActivite.ACTION_DETAIL_ACTIVITE);
                                break;

                        }

                        //  appel = new Intent(MenuDrawerNew.this,
                        //         ProposeActivites.class);
                        //    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        //   startActivity(appel);
                        if (Outils.principal != currentActivity) currentActivity.finish();
                        break;

                    case MES_PREFERENCES:
                        selectionslide = -1;
                        if (getLocalClassName().equals("com.wayd.activity.MesPreferences")) return;
                        if (getLocalClassName().equals("com.wayd.activity.MonProfilPro")) return;
                        Outils.fermeActiviteEnCours(currentActivity);

                        switch (Outils.personneConnectee.getTypeUser()) {

                            case (Profil.WAYDEUR):
                                appel = new Intent(MenuDrawerNew.this,
                                        MesPreferences.class);
                                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(appel);
                                break;

                            case (Profil.PRO):
                                appel = new Intent(MenuDrawerNew.this,
                                        MonProfilPro.class);
                                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(appel);
                                break;
                        }


                        if (Outils.principal != currentActivity) currentActivity.finish();
                        break;

                    case MA_SPHERE:
                        selectionslide = -1;
                        if (getLocalClassName().equals("com.wayd.activity.Liste_Amis")) return;
                        Outils.fermeActiviteEnCours(currentActivity);
                        appel = new Intent(MenuDrawerNew.this,
                                Liste_Amis.class);
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(appel);
                        if (Outils.principal != currentActivity) currentActivity.finish();

                        break;

                    case AMELIORER:
                        selectionslide = -1;
                        if (getLocalClassName().equals("com.wayd.activity.AmeliorerWayd")) return;
                        Outils.fermeActiviteEnCours(currentActivity);
                        appel = new Intent(MenuDrawerNew.this,
                                AmeliorerWayd.class);
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(appel);
                        if (Outils.principal != currentActivity) currentActivity.finish();
                        break;

                    case APROPOS:
                        selectionslide = -1;
                        if (getLocalClassName().equals("com.wayd.activity.Apropos")) return;
                        Outils.fermeActiviteEnCours(currentActivity);
                        appel = new Intent(MenuDrawerNew.this,
                                Apropos.class);
                        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(appel);
                        if (Outils.principal != currentActivity) currentActivity.finish();
                        break;

                    case DECONNEXION:
                        selectionslide = -1;
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        Outils.connected = false;
                        Outils.personneConnectee.Raz();
                        Outils.tableaudebord.Raz();

                        //  updateIhm();
                        Outils.fermeActiviteEnCours(currentActivity);
                        if (Outils.principal != currentActivity) currentActivity.finish();
                        Outils.principal.finish();

                        break;


                    case SHARE:
                        share();
                        break;

                }

                selectionslide = -1;
                super.onDrawerClosed(view);

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                selectionslide = -1;
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menudrawer = navigationView.getMenu();
        menu_mesactivite = menudrawer.findItem(R.id.id_mesactivite);

        //  menu_mesmessages= menudrawer.findItem(R.id.id_mesmessage);
        // menu_messuggestions= menudrawer.findItem(R.id.id_messuggestion);
        menu_masphere = menudrawer.findItem(R.id.id_masphere);
        menu_rechercher = menudrawer.findItem(R.id.id_rechercher);

        if (Outils.personneConnectee.getTypeUser() == Profil.PRO) {
            menu_masphere.setVisible(false);
            menu_rechercher.setVisible(false);
        }
        View hView = navigationView.getHeaderView(0);
        hView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getLocalClassName().equals("com.wayd.activity.UnProfil")) {

                    Outils.fermeActiviteEnCours(currentActivity);
                    Intent appel = new Intent(MenuDrawerNew.this,
                            UnProfil.class);
                    appel.putExtra("idpersonne", Outils.personneConnectee.getId());
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(appel);
                    if (Outils.principal != currentActivity) currentActivity.finish();
                }
            }
        });

        ImageView photo = (ImageView) hView.findViewById(R.id.iconactivite);
        TextView pseudo = (TextView) hView.findViewById(R.id.pseudo);
        //  TextView email = (TextView) hView.findViewById(R.id.id_email);
        TextView sexe = (TextView) hView.findViewById(R.id.id_sexe);
        TextView age = (TextView) hView.findViewById(R.id.id_age);
        ImageView IMG_modificationProfil = (ImageView) hView.findViewById(R.id.modifprofil);
        IMG_modificationProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionslide = MES_PREFERENCES;
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        pseudo.setText(Outils.personneConnectee.getPseudo());
        //  email.setText(Outils.personneConnectee.getEmail());
        sexe.setText(Outils.personneConnectee.getSexeStr());
        age.setText(Outils.personneConnectee.getAge());
        photo.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(), Outils.personneConnectee.getPhoto()));
        View view = toolbar.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform actions
                Outils.fermeActiviteEnCours(currentActivity);
                if (Outils.principal != currentActivity) currentActivity.finish();

            }
        });
        udpateDrawer();


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


    protected void onDestroy() {

        Outils.activiteEnCours.remove(this);
        Outils.tableaudebord.removeTdbChangeListener(this);
        Outils.personneConnectee.removePersonneChangeListener(this);
        super.onDestroy();

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        selectionslide = -1;
        switch (id) {

            case R.id.id_rechercher:
                selectionslide = RECHERCHE_ACTIVITE;
                break;
            case R.id.id_proposer:

                selectionslide = PROPOSE_ACTIVITE;
                break;

            case R.id.id_mesactivite:

                selectionslide = MES_ACTIVITES;
                break;

            case R.id.id_masphere:

                selectionslide = MA_SPHERE;

                break;

            case R.id.id_deconnexion:

                selectionslide = DECONNEXION;

                break;

            case R.id.id_ameliorer:

                selectionslide = AMELIORER;

                break;

            case R.id.id_apropos:

                selectionslide = APROPOS;

                break;


            case R.id.id_partager:

                selectionslide = SHARE;

                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {


            super.onBackPressed();

        }
    }

    @Override
    public void updatePersonne(Personne personne) {

        if (navigationView != null && personne != null) {
            View hView = navigationView.getHeaderView(0);
            ImageView photo = (ImageView) hView.findViewById(R.id.iconactivite);
            photo.setImageDrawable(Outils.getCirculaireAvatarDrawable(getBaseContext()));
            TextView pseudo = (TextView) hView.findViewById(R.id.pseudo);
            pseudo.setText(personne.getPseudo());
            TextView sexe = (TextView) hView.findViewById(R.id.id_sexe);
            TextView age = (TextView) hView.findViewById(R.id.id_age);
            sexe.setText(personne.getSexeStr());
            age.setText(personne.getAge());


        }


    }

    @Override
    public void updateTableauBord(TableauBord tableauBord) {

        updateTableauDeBord(tableauBord);
        if (menudrawer != null) {
            udpateDrawer();
        }
    }

    private void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBodyText = "http://play.google.com/store/apps/details?id=com.application.wayd";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wayd lien");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));

    }
}