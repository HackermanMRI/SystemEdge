package com.example.systemedge;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SystemViewPagerAdapter extends FragmentStateAdapter {

    public SystemViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new DeviceFragment();
            case 1: return new SoftwareFragment();
            case 2: return new ConnectivityFragment();
            case 3: return new LocationFragment();
            case 4: return new AppsFragment();
            default: return new DeviceFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}