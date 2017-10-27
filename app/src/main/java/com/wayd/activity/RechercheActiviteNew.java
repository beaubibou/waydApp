package com.wayd.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wayd.R;
import com.wayd.bean.Activite;
import com.wayd.bean.CritereRechercheActivite;
import com.wayd.bean.MessageServeur;
import com.wayd.bean.Outils;
import com.wayd.bean.Profil;
import com.wayd.comparator.ComparatorDistanceActivite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class RechercheActiviteNew extends MenuDrawerNew implements AsyncTaches.AsyncGetProfil.Async_GetProfilListener,
        AsyncTaches.AsyncGetListActivite.Async_GetListActiviteListener, AsyncTaches.AsyncAddActivite.Async_AddActiviteListener, AsyncTaches.AsyncGetActivite.Async_GetActiviteListener {

    // ******************Filtre par défaut *************************************
    //***************************************************************************
    static final int RAYON_RECHERCHE_DEFAUT = 2000;
    static final String motCle="";
    private static final int ONGLET_CRITERE = 0;
    private static final int ONGLET_RESULTAT = 1;
    private static final int ONGLET_CARTE = 2;
    private static final int NBR_MINIMUM_ACTIVITE_BALISE = 5;
    private static String NO_MOTCLE = "";
    public static final int TOUTE_ACTIVITE = -1;
    private int CENTRER_SUR;
    private Snackbar mySnackbar;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private final ImageView photop = null;
    private final TextView TV_age = null;
    private final TextView TV_pseudo = null;
    private final TextView TV_sexe = null;
    private final RatingBar ratingBar = null;
    private Fragment f_rechercheActivite;
    private Fragment f_listActivite;
    private F_Map_ListActivite f_mapListActivite;
    private CritereRechercheActivite critereRechercheActivite;
    private final ArrayList<Activite> listeActivite = new ArrayList<>();
    private TabLayout tabLayout = null;
    public static boolean balise;
    private Menu menuToolsBar;
    private boolean swipeRefresh = false;// Permet de savoir si la demande de rafraichissement vient du bouton ou du swipreRefres(pull to refresh)
    public static final int FROM_RECHERCHE = 1, FROM_SWIPE = 2, FROM_MAP = 3,FROM_PLUS=4;
    public int refreshSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rechercheactivite);
        InitDrawarToolBar();
        initTableauDeBord();
        initPage();
        //definir notre toolbar en tant qu'actionBar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        balise = getIntent().getBooleanExtra("ennuie", false);

        //!!!! On charge la liste des activité par défaut dans createOptionMenu dans le cas de l'ennui
    }

    public void setCritereRechercheActivite(CritereRechercheActivite critereRechercheActivite) {
        this.critereRechercheActivite = critereRechercheActivite;

    }


    public void updateListeActivite(int refreshSource, int centerSur) {
        if (centerSur != F_Map_ListActivite.CENTRER_NOCHANGE) CENTRER_SUR = centerSur;
        this.refreshSource = refreshSource;
        new AsyncTaches.AsyncGetListActivite(this, critereRechercheActivite, RechercheActiviteNew.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }




    private void initPage() {
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(ONGLET_CRITERE).setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_useract));
        tabLayout.getTabAt(ONGLET_RESULTAT).setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_commentact));
        tabLayout.getTabAt(ONGLET_CARTE).setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_placeholder));

        activeOngletResultat(false);
        activeOngletCarte(false);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {// >Verouille le scroll

            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == ONGLET_RESULTAT) {

                    ((F_ListActivite) f_listActivite).updateListe();
                    getMenuInflater().inflate(R.menu.menu_liste_tri_activite, menuToolsBar);

                }
                if (position == ONGLET_CARTE) {
                    f_mapListActivite.updateListe(CENTRER_SUR);
                    menuToolsBar.clear();
                }

                if (position == ONGLET_CRITERE)

                {
                    ((F_ListActivite) f_listActivite).updateListe();
                    menuToolsBar.clear();
                    if (mySnackbar != null) mySnackbar.dismiss();
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void getListeActivitesEnnuie() {
        CritereRechercheActivite filtre = new CritereRechercheActivite( NO_MOTCLE, TOUTE_ACTIVITE, RAYON_RECHERCHE_DEFAUT
                , Outils.personneConnectee.getLongitude(), Outils.personneConnectee.getLatitude(),0);
        setCritereRechercheActivite(filtre);
        updateListeActivite(FROM_RECHERCHE, F_Map_ListActivite.CENTRER_PERSONNE);// demande le rafraichissmeent de la part du boutton

    }

    @Override
    public void loopBack_GetProfil(Profil profil) {
        photop.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(), profil.getPhoto()));
        TV_pseudo.setText(profil.getPseudo());
        TV_age.setText(profil.getAgeStr());
        TV_sexe.setText(profil.getSexeStr());
        ratingBar.setMax(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setRating((float) profil.getNote());
        initPage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menuToolsBar = menu;
        if (balise) getListeActivitesEnnuie();// Il faut initier le menu avant de charg
        return true;

    }

    public ArrayList<Activite> getListeActivite() {
        return listeActivite;
    }

    @Override
    public void loopBack_getListActivite(ArrayList<Activite> vlistactivite) {

        if (vlistactivite != null) {



            this.listeActivite.clear();
            this.listeActivite.addAll(vlistactivite);
            Collections.sort(listeActivite, new ComparatorDistanceActivite());
            afficheNbrResultats();




            switch (refreshSource) {

                case FROM_RECHERCHE:

                    if (vlistactivite.size() > 0) {
                        activeOngletResultat(true);
                        activeOngletCarte(true);
                        mViewPager.setOnTouchListener(null);// Deveroille le scroll
                        tabLayout.getTabAt(ONGLET_RESULTAT).select();

                    } else {
                        // si pas de resultat et pas en mode balise

                        activeOngletResultat(true);
                        activeOngletCarte(true);
                        mViewPager.setOnTouchListener(null);// Deveroille le scroll
                        if (!balise)
                            afficheSnackNoResult();
                    }

                    if (vlistactivite.size() <= NBR_MINIMUM_ACTIVITE_BALISE && balise) {
                        tabLayout.getTabAt(ONGLET_RESULTAT).select();
                        afficheSnackBalise();

                    }

                    balise = false;// Pour ne plus affiche le snack
                    break;

                case FROM_SWIPE:
                    ((F_ListActivite) f_listActivite).updateListe(); // demande juste le rafraichissement
                    CENTRER_SUR = F_Map_ListActivite.CENTRER_PERSONNE;
                    f_mapListActivite.updateListe(CENTRER_SUR);
                    break;

                case FROM_PLUS:
                    ((F_ListActivite) f_listActivite).updateListe(); // demande juste le rafraichissement
                    CENTRER_SUR = F_Map_ListActivite.CENTRER_PERSONNE;
                    f_mapListActivite.updateListe(CENTRER_SUR);

            }

        }
    }

    private void activeOngletResultat(boolean active) {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        tabStrip.getChildAt(ONGLET_RESULTAT).setEnabled(active);

    }

    private void activeOngletCarte(boolean active) {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        tabStrip.getChildAt(ONGLET_CARTE).setEnabled(active);

    }

    private void afficheSnackBalise() {
        mySnackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout), R.string.noActivite,
                Snackbar.LENGTH_INDEFINITE);
        mySnackbar.getView().setBackgroundColor(Color.parseColor("#FFA440"));
        View view = mySnackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.BLACK);
        mySnackbar.setActionTextColor(Color.parseColor("#000000"));
        mySnackbar.setAction("Oui", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvreActivitePropose();
            }
        });
        mySnackbar.show();
    }

    private void afficheSnackNoResult() {
        mySnackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout), R.string.noResult,
                Snackbar.LENGTH_INDEFINITE);
        mySnackbar.getView().setBackgroundColor(Color.parseColor("#FFA440"));
        View view = mySnackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.BLACK);
        mySnackbar.setActionTextColor(Color.parseColor("#000000"));
        mySnackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        mySnackbar.show();
    }

    private void ouvreActivitePropose() {

        Intent appel = new Intent(RechercheActiviteNew.this, ProposeActivites.class);
        appel.putExtra("balise", true);// Indique qu'il faut remplir les champs titre et commentaire de la proposition
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Outils.fermeActiviteEnCours(this);
        startActivity(appel);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == DetailActivite.ACTION_DETAIL_ACTIVITE) {// recupere l'evenement d'une suprression de
            // particiaption dans le

            //   boolean refresh = data.getBooleanExtra("refresh", false);
            int action = data.getIntExtra(DetailActivite.ACTION, 0);// Recupere l'action de rechercheactiviteNew

            switch (action) {

                case DetailActivite.ACTION_REMOVE_ACTIVITE:

                    int idActivite_ = data.getIntExtra("idactivite", 0);

                    Iterator<Activite> it = listeActivite.iterator();

                    while (it.hasNext()) {
                        Activite tmp = it.next();
                        if (tmp.getId() == idActivite_) it.remove();

                    }
                    afficheNbrResultats();
                    ((F_ListActivite) f_listActivite).updateListe();
                    //    .notifyDataSetChanged();

                    break;

                case DetailActivite.ACTION_REMOVE_PARTICIPATION:

                    int idActivite = data.getIntExtra("idactivite", 0);
                    new AsyncTaches.AsyncGetActivite(this, idActivite, RechercheActiviteNew.this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    break;

            }


        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tri_distance:
                // Comportement du bouton "A Propos"
                ((F_ListActivite) f_listActivite).triDistance();

                return true;

            case R.id.tri_temps:
                // Comportement du bouton "Aide"

                ((F_ListActivite) f_listActivite).triTemps();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void afficheNbrResultats() {
        tabLayout.getTabAt(ONGLET_RESULTAT).setText(getString(R.string.onglet_Resultat) + " (" + listeActivite.size() + ")");
    }

    @Override
    public void loopBack_AddActivite(MessageServeur messageserveur) {

        if (messageserveur != null) {
            if (messageserveur.isReponse()) {
                int idactivite = Integer.parseInt(messageserveur.getMessage());// Récupére le n° de l'activite créee par le serveur
                if (idactivite != 0) {
                    Toast toast = Toast.makeText(getBaseContext(), R.string.creationActivite, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent appel = new Intent(RechercheActiviteNew.this, DetailActivite.class);
                    appel.putExtra("idactivite", idactivite);
                    appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(appel);
                    finish();
                }
            } else
                {
                Toast toast = Toast.makeText(getBaseContext(), messageserveur.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        }
    }

    @Override
    public void loopBack_GetActivite(Activite activite) {

        if (activite != null)
            ((F_ListActivite) f_listActivite).updateActivite(activite);// Rafraichit la liste des activites
    }

    public CritereRechercheActivite getCritereRechercheActivite() {
        return critereRechercheActivite;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            f_rechercheActivite = F_CritereRechecheActivite.newInstance();
            f_listActivite = F_ListActivite.newInstance();
            f_mapListActivite = new F_Map_ListActivite();

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:

                    return f_rechercheActivite;

                case 1:


                    return f_listActivite;

                case 2:

                    return f_mapListActivite;

            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.onglet_Criteres);
                case 1:
                    return getString(R.string.onglet_Resultat);
                case 2:
                    return getString(R.string.onglet_Carte);
            }
            return null;
        }
    }

    protected void onDestroy() {

        super.onDestroy();
        tabLayout.removeAllViews();
    }



}
