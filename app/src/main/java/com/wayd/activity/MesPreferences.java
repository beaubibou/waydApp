package com.wayd.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.application.wayd.R;

public class MesPreferences extends MenuDrawerNew {

    public static final int ONGLET_PROFIL = 0;
    public static final int ONGLET_PREFERENCES = 1;
    public static final int ONGLET_NOTIFICATIONS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mespreferences);
        InitDrawarToolBar();
        initTableauDeBord();
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        /*
      The {@link ViewPager} that will host the section contents.
     */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        int onglet = getIntent().getIntExtra("onglet", ONGLET_PROFIL);// recupre la valeur de l'onglet à ouvrir par defaut l'ongelt profil si pas de valeur
        tabLayout.getTabAt(onglet).select();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.menu_testonglet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case ONGLET_PROFIL:
                    return F_MonProfil.newInstance();

                case ONGLET_PREFERENCES:
                    return F_Preferences.newInstance();

                case ONGLET_NOTIFICATIONS:
                    return F_Notifications.newInstance();

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
                case ONGLET_PROFIL:
                    return "Profil";
                case ONGLET_PREFERENCES:
                    return "Suggestions";
                case ONGLET_NOTIFICATIONS:
                    return "Notifications";

            }
            return null;
        }
    }
}
