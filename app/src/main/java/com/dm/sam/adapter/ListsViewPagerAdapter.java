package com.dm.sam.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.dm.sam.activity.CategoriesFragment;
import com.dm.sam.activity.SitesFragment;
import org.jetbrains.annotations.NotNull;

public class ListsViewPagerAdapter  extends FragmentStateAdapter {
    public ListsViewPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1:
                return new CategoriesFragment();
            default:
                return new SitesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
