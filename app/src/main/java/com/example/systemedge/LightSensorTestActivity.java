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

import java.util.Locale;

// 1. The Activity must implement the SensorEventListener interface
public class LightSensorTestActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private ImageView lightSensorImage;
    private TextView luxValueText;

    public static final String EXTRA_TEST_RESULT_STATUS = "test_result_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sensor_test);

        // Initialize UI elements
        lightSensorImage = findViewById(R.id.light_sensor_image);
        luxValueText = findViewById(R.id.lux_value_text);
        Button buttonYes = findViewById(R.id.button_yes);
        Button buttonNo = findViewById(R.id.button_no);

        // 2. Get the SensorManager and the default ambient light sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        // Check if the device actually has a light sensor
        if (lightSensor == null) {
            Toast.makeText(this, "No Light Sensor found on this device.", Toast.LENGTH_LONG).show();
            setResultAndFinish(TestItem.Status.FAILED);
        }

        // Set button listeners
        buttonYes.setOnClickListener(v -> setResultAndFinish(TestItem.Status.PASSED));
        buttonNo.setOnClickListener(v -> setResultAndFinish(TestItem.Status.FAILED));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 3. CRITICAL: Register the listener when the activity is visible.
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 4. CRITICAL: Unregister the listener when the activity is paused to save battery.
        if (lightSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    /**
     * This method is called by the system whenever the sensor's value changes.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value for the ambient light level in lux (lx).
        float luxValue = event.values[0];

        // Update the TextView to show the live lux reading
        luxValueText.setText(String.format(Locale.US, "%.1f lux", luxValue));

        // Update the image based on a simple brightness threshold
        // A lux value below 50 is typically considered dim/dark.
        if (luxValue < 50) {
            // It's dark
            lightSensorImage.setImageResource(R.drawable.ic_light_dark);
        } else {
            // It's bright
            lightSensorImage.setImageResource(R.drawable.ic_light_bright);
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