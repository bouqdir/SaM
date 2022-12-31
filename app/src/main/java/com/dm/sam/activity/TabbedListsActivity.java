package com.dm.sam.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.dm.sam.R;
import com.dm.sam.listener.TabsListener;
import com.dm.sam.adapter.ListsViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;

public class TabbedListsActivity extends AppCompatActivity {

    TabLayout tabLayout;
    public ViewPager2 viewPager2;
    ListsViewPagerAdapter viewPagerAdapter;
    TabsListener tabsListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_lists);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.tabbedListTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this,CarteActivity.class));
        });


        tabLayout=findViewById(R.id.tabs);
        viewPager2=findViewById(R.id.view_pager);
        viewPagerAdapter = new ListsViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        tabsListener= new TabsListener(this,viewPager2);
        tabLayout.addOnTabSelectedListener(tabsListener);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

}