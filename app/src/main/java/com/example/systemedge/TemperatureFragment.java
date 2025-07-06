package com.example.systemedge;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TemperatureFragment extends Fragment {

    private RecyclerView recyclerView;
    private TemperatureAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temperature, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.temperature_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadThermalData();
    }

    private void loadThermalData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<ThermalInfo> thermalInfoList = new ArrayList<>();
            int i = 0;
            while (true) {
                String typePath = "/sys/class/thermal/thermal_zone" + i + "/type";
                String tempPath = "/sys/class/thermal/thermal_zone" + i + "/temp";

                File typeFile = new File(typePath);
                if (!typeFile.exists()) {
                    break;
                }

                try {
                    String rawName = readFileAsString(typePath);
                    String tempString = readFileAsString(tempPath);
                    if (tempString != null && !tempString.isEmpty()) {
                        // --- THIS IS THE CHANGE ---
                        // Translate the raw name to a friendly name before adding it to the list
                        String friendlyName = getUnderstandableName(rawName);
                        float temperature = Float.parseFloat(tempString.trim()) / 1000.0f;
                        thermalInfoList.add(new ThermalInfo(friendlyName, temperature));
                    }
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                }
                i++;
            }

            Collections.sort(thermalInfoList, (o1, o2) -> o1.getComponentName().compareTo(o2.getComponentName()));

            handler.post(() -> {
                if (getContext() != null) {
                    adapter = new TemperatureAdapter(thermalInfoList);
                    recyclerView.setAdapter(adapter);
                }
            });
        });
    }

    /**
     * --- NEW HELPER METHOD ---
     * Translates cryptic system names into user-friendly names.
     * @param rawName The name read from the system file (e.g., "cpu-0-1-user").
     * @return A clean, understandable name (e.g., "CPU Core").
     */
    private String getUnderstandableName(String rawName) {
        if (rawName == null || rawName.isEmpty()) {
            return "Unknown";
        }
        // You can add more rules here for your specific device
        if (rawName.toLowerCase(Locale.ROOT).contains("cpu")) {
            return "CPU";
        }
        if (rawName.toLowerCase(Locale.ROOT).contains("gpu")) {
            return "GPU";
        }
        if (rawName.toLowerCase(Locale.ROOT).contains("battery")) {
            return "Battery";
        }
        if (rawName.toLowerCase(Locale.ROOT).startsWith("pm")) {
            return "Power Management IC";
        }
        if (rawName.toLowerCase(Locale.ROOT).contains("soc")) {
            return "System on Chip (SoC)";
        }
        if (rawName.toLowerCase(Locale.ROOT).contains("skin") || rawName.toLowerCase(Locale.ROOT).contains("aoss")) {
            return "Device Skin";
        }

        // If no rule matches, return the original name but formatted nicely
        return rawName.replace('-', ' ').toUpperCase(Locale.ROOT);
    }

    private String readFileAsString(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString().trim();
    }
}