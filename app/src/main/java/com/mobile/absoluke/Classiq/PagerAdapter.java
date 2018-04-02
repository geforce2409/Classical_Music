package com.mobile.absoluke.Classiq;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Yul Lucia on 11/09/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    String[] listFragment = {"FragmentAchievement", "FragmentSaved", "FragmentDetails"};
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentAchievement fragmentAchievement = new FragmentAchievement();

                return fragmentAchievement;

            case 1:
                FragmentSaved fragmentSaved = new FragmentSaved();

                return fragmentSaved;

            case 2:
                FragmentDetails fragmentDetails = new FragmentDetails();

                return fragmentDetails;
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
