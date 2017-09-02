package com.wayd.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.application.wayd.R;
import com.wayd.bean.Avis;
import com.wayd.bean.Outils;
import com.wayd.bean.Profil;

import java.util.ArrayList;

public class UnProfil extends MenuDrawerNew implements AsyncTaches.AsyncGetProfilFull.Async_GetProfilListenerFull {

    private ImageView photop;
    private TextView TV_age;
    private TextView TV_pseudo;
    private TextView TV_sexe;
    private Profil profilSelectionne;
    private RatingBar ratingBar;
    private final static int ONGLET_DETAIL = 0, ONGLET_AVIS = 1, ONGLET_STAT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unprofil);
        InitDrawarToolBar();
        initTableauDeBord();
        int idpersonne = getIntent().getIntExtra("idpersonne", 0);
        photop = (ImageView) findViewById(R.id.iconactivite);
        TV_pseudo = (TextView) findViewById(R.id.pseudo);
        TV_age = (TextView) findViewById(R.id.age);
        TV_sexe = (TextView) findViewById(R.id.sexe);
        ratingBar = (RatingBar) findViewById(R.id.noteprofil);
        new AsyncTaches.AsyncGetProfilFull(this, idpersonne, UnProfil.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initPage(Profil profil, ArrayList<Avis> listAvis) {
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), profil, listAvis);
        /*
      The {@link ViewPager} that will host the section contents.
     */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //      tabLayout.getTabAt(ONGLET_DETAIL).setIcon(getBaseContext().getResources().getDrawable(R.mipmap.ic_useract));
        //     tabLayout.getTabAt(ONGLET_AVIS).setIcon(getBaseContext().getResources().getDrawable(R.mipmap.ic_commentact));
        //    tabLayout.getTabAt(ONGLET_STAT).setIcon(getBaseContext().getResources().getDrawable(R.mipmap.ic_statact));

        tabLayout.getTabAt(ONGLET_DETAIL).setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_useract));
        tabLayout.getTabAt(ONGLET_AVIS).setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_commentact));
        tabLayout.getTabAt(ONGLET_STAT).setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_statact));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_testonglet, menu);
        return true;
    }

    @Override
    public void loopBack_GetProfilFull(Profil profil, ArrayList<Avis> listAvis) {
        profilSelectionne = profil;
        photop.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(), profil.getPhoto()));
        TV_pseudo.setText(profil.getPseudo());
        TV_age.setText(profil.getAgeStr());
        TV_sexe.setText(profil.getSexeStr());
        ratingBar.setMax(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setRating((float) profil.getNote());

        TextView TV_Profil = (TextView) findViewById(R.id.signalerprofil);
        if (Outils.personneConnectee.getId() == profil.getIdpersonne())
            TV_Profil.setVisibility(View.INVISIBLE);// Enleve le bouton signaler si c'est mon profil
        else
            TV_Profil.setVisibility(View.VISIBLE);// Enleve le bouton signaler si c'est mon profil

        TV_Profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalerProfil();
            }


        });
        //*********************
        initPage(profil, listAvis);

    }

    private void signalerProfil() {
        Intent appel = new Intent(UnProfil.this, SignalerProfil.class);
        appel.putExtra("idpersonne", profilSelectionne.getIdpersonne());
        appel.putExtra("pseudo", profilSelectionne.getPseudo());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        final Profil profil;
        final ArrayList<Avis> listAvis;

        public SectionsPagerAdapter(FragmentManager fm, Profil profil, ArrayList<Avis> listAvis) {
            super(fm);
            this.profil = profil;
            this.listAvis = listAvis;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case ONGLET_DETAIL:

                    return F_DetailProfil.newInstance(profil);

                case ONGLET_AVIS:

                    return F_ListAvis.newInstance(profil, listAvis);

                case ONGLET_STAT:

                    return F_Stats.newInstance(profil);

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
                case ONGLET_DETAIL:
                    return "";
                case ONGLET_AVIS:
                    return "";
                case ONGLET_STAT:
                    return "";
            }
            return null;
        }
    }
}
