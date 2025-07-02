package com.example.systemedge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HardwareFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hardware, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Adapter and ViewPager
        HardwareViewPagerAdapter adapter = new HardwareViewPagerAdapter(this);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_hardware);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_hardware);

        viewPager.setAdapter(adapter);
        // Apply the same smooth page transition
        viewPager.setOffscreenPageLimit(1);
        viewPager.setPageTransformer(new DepthPageTransformer());

        // Use TabLayoutMediator to set tab titles only
        new TabLayoutMediator(tabLayout, viewPager, false, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Chipsets"); break;
                case 1: tab.setText("Storage"); break;
                case 2: tab.setText("Display"); break;
                case 3: tab.setText("Camera"); break;
                case 4: tab.setText("Battery"); break;
                case 5: tab.setText("Sensors"); break;
                case 6: tab.setText("Temperature"); break;
            }
        }).attach();

        // Manually sync tab selection from swiping to prevent stutter/blinking
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        // Manually sync ViewPager position from tapping to prevent feedback loop
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (viewPager.getCurrentItem() != tab.getPosition()) {
                    viewPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}