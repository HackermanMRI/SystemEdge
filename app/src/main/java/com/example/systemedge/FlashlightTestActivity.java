package com.example.systemedge;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class FlashlightTestActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private String cameraId;
    private boolean hasFlashlight;
    public static final String EXTRA_TEST_RESULT_STATUS = "test_result_status";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight_test);

        // Initialize UI elements
        Button buttonYes = findViewById(R.id.button_yes);
        Button buttonNo = findViewById(R.id.button_no);

        // Prepare the flashlight functionality
        setupFlashlight();

        // Set listener for the 'Yes' button
        buttonYes.setOnClickListener(v -> {
            // Here you would set the test result to "Passed"
            setResultAndFinish(TestItem.Status.PASSED);
            // For now, we just finish the activity
            //finish();
        });

        // Set listener for the 'No' button
        buttonNo.setOnClickListener(v -> {
            // Here you would set the test result to "Failed"
            setResultAndFinish(TestItem.Status.FAILED);
            // For now, we just finish the activity
            //finish();
        });
    }


    private void setResultAndFinish(TestItem.Status status) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TEST_RESULT_STATUS, status.name());
        setResult(RESULT_OK, resultIntent);
        finish(); // Closes this activity and sends the result back
    }

    /**
     * Initializes the CameraManager and gets the camera ID for the flashlight.
     * Also checks if the device has a flashlight feature.
     */
    private void setupFlashlight() {
        // Check if the device has a flashlight
        hasFlashlight = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlashlight) {
            Toast.makeText(this, "No flashlight found on this device.", Toast.LENGTH_SHORT).show();
            // Optionally finish the activity if no flash is present
            finish();
            return;
        }

        // Get the CameraManager service
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Get the ID of the camera that has the flash unit (usually the back camera)
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Turns the flashlight ON.
     */
    private void turnOnFlashlight() {
        if (!hasFlashlight) return; // Don't proceed if there's no flashlight
        try {
            // setTorchMode requires API 23, but your minSdk is 28, so this is safe.
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Turns the flashlight OFF.
     */
    private void turnOffFlashlight() {
        if (!hasFlashlight) return; // Don't proceed if there's no flashlight
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Turn the flashlight on when the activity becomes visible
        turnOnFlashlight();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // CRITICAL: Always turn the flashlight off when the activity is paused or closed.
        // This prevents the light from staying on if the user leaves the app.
        turnOffFlashlight();
    }
}