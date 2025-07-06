package com.example.systemedge;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SensorsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SensorAdapter adapter;
    private final List<SensorInfo> sensorInfoList = new ArrayList<>();
    private TextView sensorCountText;
    private SensorManager sensorManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        sensorCountText = view.findViewById(R.id.sensor_count_text);
        recyclerView = view.findViewById(R.id.sensors_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the SensorManager system service
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        // Load all sensor data and populate the list
        loadSensors();
    }

    /**
     * Fetches all available sensors from the device, populates our custom list,
     * and sets up the RecyclerView adapter.
     */
    private void loadSensors() {
        // Get the list of all sensors on the device
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Update the header text with the total count
        sensorCountText.setText(String.format(Locale.US, "%d Sensors are available on your device", deviceSensors.size()));

        // Clear any old data
        sensorInfoList.clear();

        // Iterate through the system's sensor list
        for (Sensor sensor : deviceSensors) {
            // Create a new custom SensorInfo object for each sensor
            SensorInfo info = new SensorInfo();

            // Populate our custom object with data from the system's Sensor object
            info.industrialName = sensor.getName();
            info.vendor = sensor.getVendor();
            info.power = sensor.getPower();

            // Check if it's a wake-up sensor (requires API 21)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                info.isWakeUpSensor = sensor.isWakeUpSensor();
            }

            // Get the user-friendly name and icon based on the sensor type
            SensorNameAndIcon pair = getSensorNameAndIcon(sensor.getType());
            info.name = pair.name;
            info.iconResId = pair.iconResId;

            // Add the fully populated object to our list
            sensorInfoList.add(info);
        }

        // Create and set the adapter for the RecyclerView
        adapter = new SensorAdapter(sensorInfoList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * A helper method to get a user-friendly name and a corresponding icon
     * for a given sensor type constant.
     * @param sensorType The integer type from the Sensor class (e.g., Sensor.TYPE_ACCELEROMETER)
     * @return A custom Pair object containing the name and icon resource ID.
     */
    private SensorNameAndIcon getSensorNameAndIcon(int sensorType) {
        String name;
        //int iconResId;

        // All cases will now use the same placeholder icon
        int iconResId = R.drawable.ic_sensor_placeholder;

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                name = "ACCELEROMETER";
                //iconResId = R.drawable.ic_sensor_accelerometer;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                name = "MAGNETIC FIELD";
                break;
            case Sensor.TYPE_GYROSCOPE:
                name = "GYROSCOPE";
                break;
            case Sensor.TYPE_LIGHT:
                name = "LIGHT";
                break;
            case Sensor.TYPE_PRESSURE:
                name = "PRESSURE";
                break;
            case Sensor.TYPE_PROXIMITY:
                name = "PROXIMITY";
                break;
            case Sensor.TYPE_GRAVITY:
                name = "GRAVITY";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                name = "LINEAR ACCELERATION";
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                name = "ROTATION VECTOR";
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                name = "HUMIDITY";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                name = "AMBIENT TEMPERATURE";
                break;
            case Sensor.TYPE_STEP_COUNTER:
                name = "STEP COUNTER";
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                name = "STEP DETECTOR";
                break;
            // Add any other specific sensors you want to identify here
            default:
                name = "OTHER SENSOR";
                break;
        }
        return new SensorNameAndIcon(name, iconResId);
    }

    /**
     * A simple helper class to return two values (name and icon) from the helper method.
     */
    private static class SensorNameAndIcon {
        final String name;
        final int iconResId;

        SensorNameAndIcon(String name, int iconResId) {
            this.name = name;
            this.iconResId = iconResId;
        }
    }
}