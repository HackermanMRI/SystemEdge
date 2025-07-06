package com.example.systemedge;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationFragment extends Fragment implements LocationListener {

    private LocationManager locationManager;
    private LayoutInflater inflater;

    // References to all the TextViews that will be updated
    private TextView latitudeValue, longitudeValue, altitudeValue, speedValue,
            speedAccuracyValue, hvAccuracyValue, satellitesValue,
            bearingValue, bearingAccuracyValue, providerValue;

    // 1. Launcher for the location permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Start location updates.
                    startLocationUpdates();
                } else {
                    // Permission denied. Show a message.
                    Toast.makeText(getContext(), "Location permission is required to show location data.", Toast.LENGTH_LONG).show();
                    clearAllLocationData("Permission Denied");
                }
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout containerCard1 = view.findViewById(R.id.container_card_1);

        // Get the LocationManager system service
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // This method creates the layout rows once, so we don't have to do it repeatedly
        setupInitialUI(containerCard1);

        // 2. Check for permission before trying to get location
        checkAndRequestLocationPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        // When the fragment resumes, re-check permission and start updates if granted
        checkAndRequestLocationPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 3. CRITICAL: Stop listening for updates when the fragment is not visible to save battery.
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Checks if location permission is granted. If not, it requests it.
     */
    private void checkAndRequestLocationPermission() {
        if (getContext() == null) return;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, start location updates.
            startLocationUpdates();
        } else {
            // Permission is not granted, request it.
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * This method now runs on a background thread to request location updates.
     */
    private void startLocationUpdates() {
        if (getContext() == null || locationManager == null) return;

        // Use an Executor to run this off the main thread, similar to StorageTestActivity
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // We need to request updates on the main thread's looper
                Looper.prepare();
                // Check permission again inside the background thread for safety
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                    }
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
                    }
                }
                Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This callback is triggered on the main thread when a new location is available.
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Update all the TextViews with the new data
        providerValue.setText(location.getProvider());
        latitudeValue.setText(String.format(Locale.US, "%.7f °", location.getLatitude()));
        longitudeValue.setText(String.format(Locale.US, "%.7f °", location.getLongitude()));

        if (location.hasAltitude()) {
            altitudeValue.setText(String.format(Locale.US, "%.1f m", location.getAltitude()));
        } else {
            altitudeValue.setText("N/A");
        }

        if (location.hasSpeed()) {
            float speedKmh = location.getSpeed() * 3.6f;
            speedValue.setText(String.format(Locale.US, "%.2f km/h", speedKmh));
        } else {
            speedValue.setText("N/A");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && location.hasSpeedAccuracy()) {
            speedAccuracyValue.setText(String.format(Locale.US, "%.2f m/s", location.getSpeedAccuracyMetersPerSecond()));
        } else {
            speedAccuracyValue.setText("Unknown");
        }

        String hAccuracy = String.format(Locale.US, "%.2f m", location.getAccuracy());
        String vAccuracy = "N/A";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && location.hasVerticalAccuracy()) {
            vAccuracy = String.format(Locale.US, "%.2f m", location.getVerticalAccuracyMeters());
        }
        hvAccuracyValue.setText(String.format("%s / %s", hAccuracy, vAccuracy));

        if (location.getExtras() != null && location.getExtras().containsKey("satellites")) {
            satellitesValue.setText(String.valueOf(location.getExtras().getInt("satellites")));
        } else {
            satellitesValue.setText("N/A");
        }

        if (location.hasBearing()) {
            bearingValue.setText(String.format(Locale.US, "%.1f °", location.getBearing()));
        } else {
            bearingValue.setText("N/A");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && location.hasBearingAccuracy()) {
            bearingAccuracyValue.setText(String.format(Locale.US, "%.1f °", location.getBearingAccuracyDegrees()));
        } else {
            bearingAccuracyValue.setText("Unknown");
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // Called when the user disables GPS in settings.
        clearAllLocationData("Please enable location services");
    }

    /**
     * Creates the UI rows once and stores references to the TextViews for fast updates.
     */
    private void setupInitialUI(LinearLayout container) {
        container.removeAllViews();
        providerValue = addRowAndGetView(container, "Location Provider", "Waiting for permission...").findViewById(R.id.text_value);
        latitudeValue = addRowAndGetView(container, "Latitude", "---").findViewById(R.id.text_value);
        longitudeValue = addRowAndGetView(container, "Longitude", "---").findViewById(R.id.text_value);
        altitudeValue = addRowAndGetView(container, "Altitude", "---").findViewById(R.id.text_value);
        speedValue = addRowAndGetView(container, "Speed", "---").findViewById(R.id.text_value);
        speedAccuracyValue = addRowAndGetView(container, "Speed Accuracy", "---").findViewById(R.id.text_value);
        hvAccuracyValue = addRowAndGetView(container, "H/V Accuracy", "---").findViewById(R.id.text_value);
        satellitesValue = addRowAndGetView(container, "Satellites", "---").findViewById(R.id.text_value);
        bearingValue = addRowAndGetView(container, "Bearing", "---").findViewById(R.id.text_value);
        bearingAccuracyValue = addRowAndGetView(container, "Bearing Accuracy", "---").findViewById(R.id.text_value);
    }

    private void clearAllLocationData(String status) {
        if (providerValue == null) return;
        providerValue.setText(status);
        latitudeValue.setText("---");
        longitudeValue.setText("---");
        altitudeValue.setText("---");
        speedValue.setText("---");
        speedAccuracyValue.setText("---");
        hvAccuracyValue.setText("---");
        satellitesValue.setText("---");
        bearingValue.setText("---");
        bearingAccuracyValue.setText("---");
    }

    private View addRowAndGetView(LinearLayout container, String key, String value) {
        if (inflater == null || container == null) return new View(getContext());
        View rowView = inflater.inflate(R.layout.layout_info_row, container, false);
        TextView textKey = rowView.findViewById(R.id.text_key);
        TextView textValue = rowView.findViewById(R.id.text_value);
        textKey.setText(key);
        textValue.setText(value);
        container.addView(rowView);
        return rowView;
    }
}