package com.example.systemedge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class FingerprintTestActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private TextView statusText;

    public static final String EXTRA_TEST_RESULT_STATUS = "test_result_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_test);

        statusText = findViewById(R.id.status_text);
        Button buttonYes = findViewById(R.id.button_yes);
        Button buttonNo = findViewById(R.id.button_no);

        // Check if the device can use biometrics
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // App can authenticate.
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                statusText.setText("No biometric hardware found");
                Toast.makeText(this, "No biometric features available on this device.", Toast.LENGTH_LONG).show();
                return; // Exit if no hardware
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                statusText.setText("Biometric hardware unavailable");
                Toast.makeText(this, "Biometric features are currently unavailable.", Toast.LENGTH_LONG).show();
                return;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                statusText.setText("No fingerprints enrolled");
                Toast.makeText(this, "No fingerprints have been enrolled on this device.", Toast.LENGTH_LONG).show();
                return;
        }

        // --- Setup the Biometric Prompt ---

        executor = ContextCompat.getMainExecutor(this);

        // This is the callback that will handle authentication results
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                statusText.setText("Authentication error: " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                statusText.setText("Authentication Succeeded!");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                statusText.setText("Authentication Failed");
            }
        });

        // This builds the dialog that the user will see
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Test")
                .setSubtitle("Place your finger on the sensor")
                .setNegativeButtonText("Cancel")
                .build();

        // Show the prompt immediately when the activity starts
        biometricPrompt.authenticate(promptInfo);

        // --- Setup button listeners ---
        buttonYes.setOnClickListener(v -> setResultAndFinish(TestItem.Status.PASSED));
        buttonNo.setOnClickListener(v -> setResultAndFinish(TestItem.Status.FAILED));
    }

    private void setResultAndFinish(TestItem.Status status) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TEST_RESULT_STATUS, status.name());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}