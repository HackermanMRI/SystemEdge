package com.example.systemedge;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// 1. The Activity must implement the SensorEventListener interface
public class EarProximityTestActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;

    private ImageView proximityImage;
    private TextView instructionText;

    public static final String EXTRA_TEST_RESULT_STATUS = "test_result_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ear_proximity_test);

        // Initialize UI elements
        proximityImage = findViewById(R.id.proximity_image);
        instructionText = findViewById(R.id.instruction_text);
        Button buttonYes = findViewById(R.id.button_yes);
        Button buttonNo = findViewById(R.id.button_no);

        // 2. Get the SensorManager and the default proximity sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        // Check if the device actually has a proximity sensor
        if (proximitySensor == null) {
            Toast.makeText(this, "No Proximity Sensor found on this device.", Toast.LENGTH_LONG).show();
            setResultAndFinish(TestItem.Status.FAILED); // Fail the test automatically if no sensor
        }

        // Set button listeners
        buttonYes.setOnClickListener(v -> setResultAndFinish(TestItem.Status.PASSED));
        buttonNo.setOnClickListener(v -> setResultAndFinish(TestItem.Status.FAILED));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 3. CRITICAL: Register the listener when the activity is visible.
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 4. CRITICAL: Unregister the listener when the activity is paused to save battery.
        if (proximitySensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    /**
     * This method is called by the system whenever the sensor's value changes.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // The proximity sensor returns a distance value in centimeters.
        float distance = event.values[0];

        // A small distance value means an object is NEAR.
        // A large distance value (usually the sensor's max range) means an object is FAR.
        if (distance < proximitySensor.getMaximumRange()) {
            // Object is NEAR
            instructionText.setText("Object Detected!\nSensor working");
            proximityImage.setImageResource(R.drawable.ic_proximity_near);
        } else {
            // Object is FAR
            instructionText.setText("Cover the top of your screen");
            proximityImage.setImageResource(R.drawable.ic_proximity_far);
        }
    }

    /**
     * This method is also required by the interface but is often not needed.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used for this test.
    }

    private void setResultAndFinish(TestItem.Status status) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TEST_RESULT_STATUS, status.name());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}