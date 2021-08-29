package com.example.diet.view.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.diet.R;
import com.example.diet.adapter.TabsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        addTabs();
    }

    private void initViews() {
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            viewPager = (ViewPager) findViewById(R.id.pager);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTabs() {
        try {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.diet_tab)));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(tabsPagerAdapter);
            viewPager.setCurrentItem(0);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
