package com.example.systemedge;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;



public class AppsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppAdapter adapter;
    private List<AppInfo> allAppsList = new ArrayList<>();
    private List<AppInfo> filteredAppsList = new ArrayList<>();
    private TextView appCountText;
    private Spinner spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appCountText = view.findViewById(R.id.app_count_text);
        spinner = view.findViewById(R.id.app_filter_spinner);
        recyclerView = view.findViewById(R.id.apps_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupSpinner();
        loadApps();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item_black, // Use our new custom layout
                getResources().getStringArray(R.array.app_filter_options)
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterApps(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadApps() {
        PackageManager pm = requireContext().getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo packageInfo : packages) {
            AppInfo app = new AppInfo();
            app.name = packageInfo.applicationInfo.loadLabel(pm).toString();
            app.packageName = packageInfo.packageName;
            app.version = packageInfo.versionName;
            app.icon = packageInfo.applicationInfo.loadIcon(pm);
            app.targetSdk = packageInfo.applicationInfo.targetSdkVersion;
            app.isSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            // Determine architecture (32/64 bit)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                app.architecture = (packageInfo.applicationInfo.nativeLibraryDir.contains("64")) ? "64 BIT" : "32 BIT";
            } else {
                app.architecture = "32 BIT";
            }
            allAppsList.add(app);
        }
        // Initially show user apps
        filterApps(0);
    }

    private void filterApps(int filterType) {
        filteredAppsList.clear();
        for (AppInfo app : allAppsList) {
            // 0 = User apps, 1 = System apps, 2 = All apps
            if ((filterType == 0 && !app.isSystemApp) ||
                    (filterType == 1 && app.isSystemApp) ||
                    (filterType == 2)) {
                filteredAppsList.add(app);
            }
        }

        if (adapter == null) {
            adapter = new AppAdapter(filteredAppsList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        appCountText.setText(String.valueOf(filteredAppsList.size()));
    }
}