package com.example.systemedge;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HardwareViewPagerAdapter extends FragmentStateAdapter {

    public HardwareViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new ChipsetsFragment();
            case 1: return new StorageFragment();
            case 2: return new DisplayFragment();
            case 3: return new CameraFragment();
            case 4: return new BatteryFragment();
            case 5: return new SensorsFragment();
            case 6: return new TemperatureFragment();
            default: return new ChipsetsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}