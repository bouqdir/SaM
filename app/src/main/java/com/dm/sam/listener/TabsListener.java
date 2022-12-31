package com.dm.sam.listener;

import androidx.viewpager2.widget.ViewPager2;
import com.dm.sam.activity.TabbedListsActivity;
import com.google.android.material.tabs.TabLayout;

public class TabsListener implements TabLayout.OnTabSelectedListener {

    TabbedListsActivity activity;
    ViewPager2 viewPager2;

    public TabsListener(TabbedListsActivity activity, ViewPager2 viewPager2) {
        this.activity = activity;
        this.viewPager2 = viewPager2;
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager2.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


}
