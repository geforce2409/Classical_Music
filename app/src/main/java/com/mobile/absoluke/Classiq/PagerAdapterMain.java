package com.mobile.absoluke.Classiq;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Yul Lucia on 12/16/2017.
 */

public class PagerAdapterMain extends FragmentStatePagerAdapter {
    String[] listFragment = {"FragmentHome", "FragmentQuiz"};

    public PagerAdapterMain(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentHome fragmentHome = new FragmentHome();

                return fragmentHome;

//            case 1:
//                FragmentQuiz fragmentQuiz = new FragmentQuiz();
//
//                return fragmentQuiz;
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
