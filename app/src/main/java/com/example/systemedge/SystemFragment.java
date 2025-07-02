package com.example.systemedge;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SystemFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the new layout with TabLayout and ViewPager2
        return inflater.inflate(R.layout.fragment_system, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the adapter
        SystemViewPagerAdapter adapter = new SystemViewPagerAdapter(this);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        //transition
        viewPager.setOffscreenPageLimit(1);
        viewPager.setPageTransformer(new DepthPageTransformer());

        // Link the TabLayout and the ViewPager2
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Set the title for each tab
            switch (position) {
                case 0: tab.setText("Device"); break;
                case 1: tab.setText("Software"); break;
                case 2: tab.setText("Connectivity"); break;
                case 3: tab.setText("Location"); break;
                case 4: tab.setText("Apps"); break;
            }
        }).attach();


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Animate the tab selection to the new position
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        // 4. NEW: Add a listener on the TabLayout to update the ViewPager when a tab is clicked by the user.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (viewPager.getCurrentItem() != tab.getPosition()) {
                    viewPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });

    }
}