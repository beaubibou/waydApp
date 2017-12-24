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
import com.wayd.bean.ProfilPro;

import java.util.ArrayList;

public class UnProfilPro extends MenuDrawerNew implements AsyncTaches.AsyncGetProfilPro.Async_GetProfilProListener {

    private ImageView photop;
    private TextView TV_age;
    private TextView TV_pseudo;
    private TextView TV_sexe;
    private ProfilPro profilSelectionne;
    private RatingBar ratingBar;
    private final static int ONGLET_DETAIL = 0, ONGLET_AVIS = 1, ONGLET_STAT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unprofilpro);
        InitDrawarToolBar();
        initTableauDeBord();
        int idpersonne = getIntent().getIntExtra("idpersonne", 0);
        photop = (ImageView) findViewById(R.id.iconactivite);
        TV_pseudo = (TextView) findViewById(R.id.pseudo);
        new AsyncTaches.AsyncGetProfilPro(this, idpersonne, UnProfilPro.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initPage(ProfilPro profil) {
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), profil);
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
      //  tabLayout.getTabAt(ONGLET_AVIS).setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_commentact));
      //  tabLayout.getTabAt(ONGLET_STAT).setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_statact));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_testonglet, menu);
        return true;
    }



    private void signalerProfil() {
        Intent appel = new Intent(UnProfilPro.this, SignalerProfilPro.class);
        appel.putExtra("idpersonne", profilSelectionne.getId());
        appel.putExtra("pseudo", profilSelectionne.getPseudo());
        appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(appel);

    }

    @Override
    public void loopBack_GetProfilPro(ProfilPro profil) {
        profilSelectionne = profil;
        photop.setImageDrawable(Outils.getAvatarDrawable(getBaseContext(), profil.getPhoto()));
        TV_pseudo.setText(profil.getPseudo());
        TextView TV_Profil = (TextView) findViewById(R.id.signalerprofil);

        if (Outils.personneConnectee.getId() == profil.getId())
            TV_Profil.setVisibility(View.GONE);// Enleve le bouton signaler si c'est mon profil
        else
            TV_Profil.setVisibility(View.VISIBLE);// Enleve le bouton signaler si c'est mon profil

        TV_Profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalerProfil();
            }


        });
        //*********************
        initPage(profil);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        final ProfilPro profil;

        public SectionsPagerAdapter(FragmentManager fm, ProfilPro profil) {
            super(fm);
            this.profil = profil;

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case ONGLET_DETAIL:

                    return F_DetailProfilPro.newInstance(profil);

              //  case ONGLET_AVIS:

             //       return F_ListAvis.newInstance(profil, listAvis);

           //     case ONGLET_STAT:
//
           //         return F_Stats.newInstance(profil);

            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case ONGLET_DETAIL:
                    return "";
            //    case ONGLET_AVIS:
             //       return "";
             //   case ONGLET_STAT:
               //     return "";
            }
            return null;
        }
    }
}
