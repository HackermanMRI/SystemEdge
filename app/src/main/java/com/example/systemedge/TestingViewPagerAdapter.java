package com.example.systemedge;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TestingViewPagerAdapter extends FragmentStateAdapter {

    public TestingViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new RunBenchmarkFragment();
        }
        return new ManualCheckupFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}