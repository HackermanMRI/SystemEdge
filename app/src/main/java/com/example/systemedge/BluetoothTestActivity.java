package com.example.systemedge;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BluetoothTestActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private TextView statusText;
    public static final String EXTRA_TEST_RESULT_STATUS = "test_result_status";

    // 1. Launcher for the Bluetooth permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Check the Bluetooth status.
                    initializeBluetooth();
                } else {
                    // Permission denied. Show a message and fail the test.
                    Toast.makeText(this, "Bluetooth permission is required for this test.", Toast.LENGTH_LONG).show();
                    statusText.setText("Permission Denied");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);

        statusText = findViewById(R.id.status_text);
        Button buttonYes = findViewById(R.id.button_yes);
        Button buttonNo = findViewById(R.id.button_no);

        // Check for permission and initialize
        checkAndRequestPermission();

        buttonYes.setOnClickListener(v -> setResultAndFinish(TestItem.Status.PASSED));
        buttonNo.setOnClickListener(v -> setResultAndFinish(TestItem.Status.FAILED));
    }

    private void checkAndRequestPermission() {
        // BLUETOOTH_CONNECT permission is only required for Android 12 (API 31) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted
                initializeBluetooth();
            } else {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
            }
        } else {
            // For older versions, permission is granted at install time.
            initializeBluetooth();
        }
    }

    /**
     * Initializes the Bluetooth adapter and checks its initial state.
     */
    private void initializeBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            statusText.setText("Not Supported");
            return;
        }

        // Check the initial state of Bluetooth
        updateBluetoothStatus();
    }

    /**
     * Reads the Bluetooth adapter state and updates the UI text.
     */
    private void updateBluetoothStatus() {
        if (bluetoothAdapter == null) return;

        // This requires the BLUETOOTH_CONNECT permission we requested.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            statusText.setText("Permission Denied");
            return;
        }

        if (bluetoothAdapter.isEnabled()) {
            statusText.setText("BLUETOOTH IS ON");
        } else {
            statusText.setText("BLUETOOTH IS OFF");
        }
    }

    // 2. A BroadcastReceiver to listen for changes in Bluetooth state
    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                // The Bluetooth state has changed, update our UI
                updateBluetoothStatus();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 3. Register the receiver to start listening for state changes
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        // Also update status when resuming
        if (bluetoothAdapter != null) {
            updateBluetoothStatus();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 4. CRITICAL: Unregister the receiver to avoid memory leaks
        unregisterReceiver(bluetoothStateReceiver);
    }

    private void setResultAndFinish(TestItem.Status status) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TEST_RESULT_STATUS, status.name());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}