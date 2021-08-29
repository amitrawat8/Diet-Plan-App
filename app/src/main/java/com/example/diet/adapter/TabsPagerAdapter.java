package com.example.diet.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.diet.view.ui.DietListFragment;


public class TabsPagerAdapter extends FragmentPagerAdapter {
    int tabCount;

    public TabsPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new DietListFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
