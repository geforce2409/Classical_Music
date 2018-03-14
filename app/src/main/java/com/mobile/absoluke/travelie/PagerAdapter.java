package com.mobile.absoluke.travelie;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Yul Lucia on 11/09/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    String[] listFragment = {"FragmentLocation", "FragmentPosts", "FragmentPhotos", "FragmentAbout"};
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentLocation fragmentLocation = new FragmentLocation();

                return fragmentLocation;

            case 1:
                FragmentPosts fragmentPosts = new FragmentPosts();

                return fragmentPosts;

            case 2:
                FragmentPhotos fragmentPhotos = new FragmentPhotos();

                return fragmentPhotos;

            case 3:
                FragmentAbout fragmentAbout = new FragmentAbout();

                return fragmentAbout;
        }
        return null;
    }

    @Override
    public int getCount() {
        return listFragment.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //String title = "";
        return null;
    }
}
