package com.example.systemedge;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Build;

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



public class LocationFragment extends Fragment implements LocationListener {

    private LocationManager locationManager;
    private LinearLayout containerCard1;
    private LayoutInflater inflater; // We'll need this for our addRowToContainer method

    private TextView latitudeValue, longitudeValue, altitudeValue, speedValue,
            speedAccuracyValue, hvAccuracyValue, satellitesValue,
            bearingValue, bearingAccuracyValue, providerValue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Save the inflater for later use
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        containerCard1 = view.findViewById(R.id.container_card_1);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        setupInitialUI();

        startLocationUpdates();


    }

    @Override
    public void onResume() {
        super.onResume();
        // Start listening for location updates when the fragment is visible
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop listening for location updates to save battery when the fragment is not visible
        //locationManager.removeUpdates(this);
    }

    private void clearAllLocationData(String status) {
        // Check if the views have been initialized first
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

    private void startLocationUpdates() {
         //Check for permission first
       /* if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            clearAllLocationData("Location permission needed");
           return;
        }*/

        try {
            // Request updates from BOTH providers

             //Request from GPS
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            }

            // Also request from Network
            //if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called every time the device's location is updated.
     * This is where we update our UI.
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        // This method is now very fast because it only sets text.
        // NO MORE removeAllViews() or inflating layouts here!

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
        hvAccuracyValue.setText(hAccuracy + " / " + vAccuracy);

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

    // --- Helper method to add rows ---
    // Make sure this method exists in your fragment, similar to other fragments
    private View addRowAndGetView(LinearLayout container, String key, String value) {
        if (inflater == null || container == null) return null;
        View rowView = inflater.inflate(R.layout.layout_info_row, container, false);
        TextView textKey = rowView.findViewById(R.id.text_key);
        TextView textValue = rowView.findViewById(R.id.text_value);
        textKey.setText(key);
        textValue.setText(value);
        container.addView(rowView);
        return rowView; // Return the created view
    }

    // --- Other required LocationListener methods ---
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // Called when the user disables the GPS.
        // This now updates the existing UI instead of destroying it.
        clearAllLocationData("Please enable GPS");
    }

    private void setupInitialUI() {
        containerCard1.removeAllViews(); // Clear any previous views

        // Create each row once and store a reference to its value TextView
        providerValue = addRowAndGetView(containerCard1, "Location Provider", "Initializing Location Services...").findViewById(R.id.text_value);
        latitudeValue = addRowAndGetView(containerCard1, "Latitude", "Initializing Location Services...").findViewById(R.id.text_value);
        longitudeValue = addRowAndGetView(containerCard1, "Longitude", "Initializing Location Services...").findViewById(R.id.text_value);
        altitudeValue = addRowAndGetView(containerCard1, "Altitude", "Initializing Location Services...").findViewById(R.id.text_value);
        speedValue = addRowAndGetView(containerCard1, "Speed", "Initializing Location Services...").findViewById(R.id.text_value);
        speedAccuracyValue = addRowAndGetView(containerCard1, "Speed Accuracy", "Initializing Location Services...").findViewById(R.id.text_value);
        hvAccuracyValue = addRowAndGetView(containerCard1, "H/V Accuracy", "Initializing Location Services...").findViewById(R.id.text_value);
        satellitesValue = addRowAndGetView(containerCard1, "Satellites", "Initializing Location Services...").findViewById(R.id.text_value);
        bearingValue = addRowAndGetView(containerCard1, "Bearing", "Initializing Location Services...").findViewById(R.id.text_value);
        bearingAccuracyValue = addRowAndGetView(containerCard1, "Bearing Accuracy", "Initializing Location Services...").findViewById(R.id.text_value);
    }

}