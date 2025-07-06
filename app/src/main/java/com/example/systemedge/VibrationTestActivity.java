package com.example.systemedge;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VibrationTestActivity extends AppCompatActivity {

    private Vibrator vibrator;
    public static final String EXTRA_TEST_RESULT_STATUS = "test_result_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibration_test);

        Button buttonYes = findViewById(R.id.button_yes);
        Button buttonNo = findViewById(R.id.button_no);

        // 1. Get the system's VIBRATOR_SERVICE
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Check if the device has a vibrator motor
        if (vibrator == null || !vibrator.hasVibrator()) {
            Toast.makeText(this, "No Vibrator found on this device.", Toast.LENGTH_LONG).show();
            setResultAndFinish(TestItem.Status.FAILED);
            return; // Exit early if no vibrator
        }

        buttonYes.setOnClickListener(v -> setResultAndFinish(TestItem.Status.PASSED));
        buttonNo.setOnClickListener(v -> setResultAndFinish(TestItem.Status.FAILED));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 2. Start vibrating when the activity is visible
        if (vibrator != null) {
            // Create a vibration pattern: pause 500ms, vibrate 500ms
            long[] timings = {500, 500};
            // Use the default vibration amplitude
            int[] amplitudes = {0, VibrationEffect.DEFAULT_AMPLITUDE};
            // Repeat the pattern from the beginning (index 0)
            int repeatIndex = 0;

            // Create the effect and vibrate
            // This requires API 26, but your minSdk is 28, so this is safe.
            VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes, repeatIndex);
            vibrator.vibrate(effect);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 3. CRITICAL: Always cancel the vibration when the activity is not visible
        // to prevent it from vibrating indefinitely.
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void setResultAndFinish(TestItem.Status status) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TEST_RESULT_STATUS, status.name());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}