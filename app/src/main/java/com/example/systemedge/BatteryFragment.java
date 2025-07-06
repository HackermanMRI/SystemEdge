package com.example.systemedge;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Build;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

import java.util.Locale;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import java.text.SimpleDateFormat;

import java.lang.reflect.Method;



public class BatteryFragment extends Fragment {

    //declarations

    // Views for the first card
    private TextView batteryPercentageText, batteryStatusText, batteryCurrentText, batteryPowerText;
    // Container for the second card's rows
    private LinearLayout containerCard1, containerCard2;
    private LayoutInflater inflater;
    // Receiver to listen for battery changes
    private BroadcastReceiver batteryReceiver;

    private Handler liveUpdateHandler;
    private Runnable liveUpdateRunnable;

    private static final String PREFS_NAME = "BatteryHealthPrefs";
    private static final String KEY_SAVED_MAX_CAPACITY = "savedMaxCapacity";


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
        return inflater.inflate(R.layout.fragment_battery, container, false);
    }

    @Override
    @SuppressWarnings("deprecation") // Add this to suppress warnings for the whole method
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Initialize views from the layout
        batteryPercentageText = view.findViewById(R.id.battery_percentage_text);
        batteryStatusText = view.findViewById(R.id.battery_status_text);
        batteryCurrentText = view.findViewById(R.id.battery_current_text);
        batteryPowerText = view.findViewById(R.id.battery_power_text);
        containerCard1 = view.findViewById(R.id.container_card_1);
        containerCard2 = view.findViewById(R.id.container_card_2);

        // Setup the receiver to start listening
        setupBatteryReceiver();
        setupLiveUpdates();



    }

    @Override
    public void onResume() {
        super.onResume();
        // Register the broadcast receiver for major battery state changes
        if (getActivity() != null) {
            getActivity().registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
        // --- NEW: Start the per-second updates when the fragment is visible ---
        liveUpdateHandler.post(liveUpdateRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the broadcast receiver to prevent memory leaks
        if (getActivity() != null) {
            getActivity().unregisterReceiver(batteryReceiver);
        }
        // --- NEW: Stop the per-second updates when the fragment is not visible ---
        liveUpdateHandler.removeCallbacks(liveUpdateRunnable);
    }


    /**
     * Sets up the BroadcastReceiver to listen for major, event-based battery changes
     * like charging status, health, etc.
     */
    private void setupBatteryReceiver() {
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // This method is called by the system when the battery state changes.
                // It updates the less frequently changed data.
                updateStaticBatteryUI(intent);
            }
        };
    }

    /**
     * --- NEW: Sets up the Handler and Runnable for continuous, per-second updates. ---
     */
    private void setupLiveUpdates() {
        liveUpdateHandler = new Handler(Looper.getMainLooper());
        liveUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateLiveBatteryData(); // Call the method to update live data
                liveUpdateHandler.postDelayed(this, 1000); // Schedule it to run again after 1 second
            }
        };
    }

    /**
     * --- NEW: Updates data that changes rapidly (every second). ---
     * This method is called by the Handler. It gets the latest live values
     * like current, charge counter, and percentage.
     */
    private void updateLiveBatteryData() {
        if (getContext() == null) return; // Ensure fragment is still attached

        // Get battery manager service
        BatteryManager bm = (BatteryManager) getContext().getSystemService(Context.BATTERY_SERVICE);

        // To get the latest percentage, we can use a sticky intent.
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = getContext().registerReceiver(null, filter);

        if (intent == null) return;

        // --- Update Percentage ---
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int percentage = -1;
        if (level != -1 && scale != -1) {
            percentage = (int) ((level / (float) scale) * 100);
        }
        batteryPercentageText.setText(percentage + "%");


        // --- Update Current and Power ---
        long currentNow = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW); // in microamps
        long voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1); // in millivolts
        double powerWatts = (currentNow / 1000.0) * (voltage / 1000.0) / 1000.0; // P = I*V

        batteryCurrentText.setText("Current: " + (currentNow / 1000) + " mA");
        batteryPowerText.setText(String.format("Power: %.2f W", powerWatts));


        // --- Update Remaining Capacity Row ---
        // We need to find and update the existing row instead of re-adding it.
        TextView remainingCapacityValueView = containerCard2.findViewWithTag("remaining_capacity_value");
        if (remainingCapacityValueView != null) {
            long chargeCounter = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER); // in µAh
            remainingCapacityValueView.setText((chargeCounter / 1000) + " mAh");
        }
    }


    /**
     * Updates UI with data from the ACTION_BATTERY_CHANGED broadcast.
     * This is for data that doesn't change every second, like health, technology, etc.
     */
    private void updateStaticBatteryUI(Intent intent) {
        // Clear old data first to prevent duplicates
        containerCard1.removeAllViews();
        containerCard2.removeAllViews();

        // Update the status text which can change
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        batteryStatusText.setText(getChargeStatusString(status));

        // Get voltage once from the intent
        long voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1); // in millivolts

        // --- Populate Card 1 ---
        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        addRowToContainer(containerCard1, "Battery Recognised", present ? "Yes" : "No");

        int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        addRowToContainer(containerCard1, "Temperature", (temp / 10.0) + " °C");

        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        addRowToContainer(containerCard1, "Power Source", getPluggedSourceString(plugged));

        String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        addRowToContainer(containerCard1, "Technology", technology);

        addRowToContainer(containerCard1, "Voltage", (voltage / 1000.0) + " V");

        // --- Populate Card 2 ---
        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        addRowToContainer(containerCard2, "Health", getHealthString(health));

        double capacity = getBatteryCapacity(requireContext());
        addRowToContainer(containerCard2, "Capacity (System Data)", String.format("%.1f mAh", capacity));

        // --- Add the "Remaining Capacity" row with a Tag so we can find it later ---
        View rowView = inflater.inflate(R.layout.layout_info_row, containerCard2, false);
        TextView textKey = rowView.findViewById(R.id.text_key);
        TextView textValue = rowView.findViewById(R.id.text_value);
        textKey.setText("Remaining Capacity");
        textValue.setText("N/A"); // Initial value
        textValue.setTag("remaining_capacity_value"); // Set a unique tag
        containerCard2.addView(rowView);
        // The live updater will now fill in the value every second.

        // Calculate max capacity
        BatteryManager bm = (BatteryManager) requireActivity().getSystemService(Context.BATTERY_SERVICE);
        long chargeCounter = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int percentage = (int) ((level / (float) scale) * 100);


        //int maxChargeCapacity = calculateMaxCapacityPercentage(percentage, chargeCounter / 1000, capacity);
        //addRowToContainer(containerCard2, "Maximum Charging Capacity", maxChargeCapacity + "%", R.color.purple_700);

        // --- NEW LOGIC FOR MAXIMUM CAPACITY ---
        // 1. Calculate the current maximum capacity
        int currentMaxCapacity = calculateMaxCapacityPercentage(percentage, chargeCounter / 1000, capacity);

        // 2. Get the previously stored maximum capacity
        int storedMaxCapacity = getStoredMaxCapacity(requireContext());

        // 3. Determine the final value to show (the smaller of the two)
        int finalMaxCapacityToShow = Math.min(currentMaxCapacity, storedMaxCapacity);

        // 4. If the current value is smaller, it's a new "lowest" value, so save it.
        // We also save it if the stored value is the default (101), meaning nothing was saved before.
        if (currentMaxCapacity < storedMaxCapacity) {
            saveMaxCapacity(requireContext(), currentMaxCapacity);
        }

        // 5. Add the final, most accurate value to the container
        addRowToContainer(containerCard2, "Maximum Charging Capacity", finalMaxCapacityToShow + "%", R.color.purple_700);
        // ------------------------------------



        // Get cycle count
        String cycleCount = readSystemFile("/sys/class/power_supply/battery/cycle_count");
        addRowToContainer(containerCard2, "Cycle Count", cycleCount);

        // Get Time Remaining (requires API 28+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            long timeRemaining = bm.computeChargeTimeRemaining(); // in milliseconds
            if (timeRemaining != -1) {
                addRowToContainer(containerCard2, "Time Remaining", formatTime(timeRemaining));
            } else {
                addRowToContainer(containerCard2, "Time Remaining", "N/A");
            }
        }

        // --- Finally, call the live updater once to populate the live fields immediately ---
        updateLiveBatteryData();
    }


    // --- Helper Methods (Unchanged) ---


    /**
     * Saves the given maximum capacity value to SharedPreferences.
     * @param context The application context.
     * @param capacity The integer value to save.
     */
    private void saveMaxCapacity(Context context, int capacity) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SAVED_MAX_CAPACITY, capacity);
        editor.apply();
    }

    /**
     * Retrieves the stored maximum capacity from SharedPreferences.
     * @param context The application context.
     * @return The stored integer value, or 101 if nothing is stored.
     */
    private int getStoredMaxCapacity(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Return 101 as a default. Any valid calculation (<=100) will be smaller,
        // so the first calculated value will always be saved.
        return prefs.getInt(KEY_SAVED_MAX_CAPACITY, 101);
    }



    private int calculateMaxCapacityPercentage(int percentage, long remainingMah, double designCapacityMah) {
        if (percentage <= 0 || designCapacityMah <= 0) {
            return 0;
        }
        double currentFullCapacity = ((double) remainingMah * 100.0) / percentage;
        double healthRatio = currentFullCapacity / designCapacityMah;
        int healthPercentage = (int) (healthRatio * 100);
        return Math.min(healthPercentage, 100);
    }

    private String getChargeStatusString(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING: return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING: return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL: return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING: return "Not Charging";
            default: return "Unknown";
        }
    }

    private String getHealthString(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD: return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return "Overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD: return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "Failure";
            default: return "Unknown";
        }
    }

    private String getPluggedSourceString(int plugged) {
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC: return "AC Charger";
            case BatteryManager.BATTERY_PLUGGED_USB: return "USB Port";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS: return "Wireless";
            default: return "Battery";
        }
    }

    private double getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(context);
            batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }

    private String readSystemFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine();
        } catch (IOException e) {
            return "N/A";
        }
    }

    private String formatTime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        return String.format(Locale.getDefault(), "%d hours, %d minutes", hours, minutes);
    }

}