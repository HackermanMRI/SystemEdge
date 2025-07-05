package com.example.systemedge;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import android.app.ActivityManager;

import java.text.DecimalFormat;

import java.io.File;


public class StorageFragment extends Fragment {

    private LinearLayout containerCard1,containerCard2,containerCard3;
    private LayoutInflater inflater;


    //methods
    private void addRowToContainer(LinearLayout container, String key, String value) {
        // Use the default text color
        addRowToContainer(container, key, value, R.color.value_color);
    }

    private void addRowToContainer(LinearLayout container, String key, String value, int valueColorRes) {
        // Inflate the row layout "prototype"
        View rowView = inflater.inflate(R.layout.layout_info_row, container, false);

        // Find the TextViews within the inflated view
        TextView textKey = rowView.findViewById(R.id.text_key);
        TextView textValue = rowView.findViewById(R.id.text_value);

        // Set the text
        textKey.setText(key);
        textValue.setText(value);

        // Set the custom color
        if (getContext() != null) {
            textValue.setTextColor(ContextCompat.getColor(getContext(), valueColorRes));
        }

        // Add the newly created row to the container
        container.addView(rowView);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_storage, container, false);
    }

    @Override
    @SuppressWarnings("deprecation") // Add this to suppress warnings for the whole method
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        containerCard1 = view.findViewById(R.id.container_card_1);
        containerCard2 = view.findViewById(R.id.container_card_2);
        containerCard3 = view.findViewById(R.id.container_card_3);

        populateRamInfo();
        populateStorageInfo();

    }

    private void populateStorageInfo() {
        // Assumes you have a container in your XML with this ID

        containerCard2.removeAllViews();
        containerCard3.removeAllViews();

        // Get Internal Storage (/data)
        addStorageInfoToContainer(containerCard3, "Internal", Environment.getDataDirectory());


        // Get System Storage (/system)
        addStorageInfoToContainer(containerCard2, "System", Environment.getRootDirectory());
    }

    /**
     * Helper method to calculate storage stats for a given path and add them to a container.
     * @param container The LinearLayout to add rows to.
     * @param storageName The display name (e.g., "Internal", "System").
     * @param path The file path to get stats for.
     */
    private void addStorageInfoToContainer(LinearLayout container, String storageName, File path) {
        try {
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            long availableBlocks = stat.getAvailableBlocksLong();

            long totalSize = totalBlocks * blockSize;
            long availableSize = availableBlocks * blockSize;
            long usedSize = totalSize - availableSize;




            addRowToContainer(container, "File Path " , path.getAbsolutePath());
            addRowToContainer(container, "Total " , formatBytes(totalSize));
            addRowToContainer(container, "Used " , formatBytes(usedSize));
            addRowToContainer(container, "Free" , formatBytes(availableSize));

        } catch (IllegalArgumentException e) {
            // This can happen if the path is invalid
            addRowToContainer(container, storageName + " Storage", "Error reading storage");
        }
    }

    private void populateRamInfo() {
        // Make sure you have a container_card_3 in your XML
        LinearLayout containerCard3 = requireView().findViewById(R.id.container_card_3);
        if (containerCard3 == null) return;

        containerCard3.removeAllViews();

        ActivityManager activityManager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);

            long totalRam = memoryInfo.totalMem;
            long availableRam = memoryInfo.availMem;
            long usedRam = totalRam - availableRam;


            addRowToContainer(containerCard1, "Total", formatBytes(totalRam));
            addRowToContainer(containerCard1, "Used", formatBytes(usedRam));
            addRowToContainer(containerCard1, "Available", formatBytes(availableRam));

        }
    }

    /**
     * Helper method to format bytes into KB, MB, GB, etc.
     * @param bytes The number of bytes to format.
     * @return A human-readable string like "5.8 GB".
     */
    private String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(bytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }



}