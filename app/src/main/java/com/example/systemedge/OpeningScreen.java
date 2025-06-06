package com.example.systemedge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import android.animation.AnimatorSet;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Build;
import android.os.BatteryManager;
import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;
import android.os.Handler;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ImageView;
import androidx.core.graphics.Insets;
import android.util.Log;
import android.Manifest;
import androidx.annotation.NonNull;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import androidx.annotation.RequiresApi;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class OpeningScreen extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    // Required permissions
    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_opening_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

 //permission check
        checkAndRequestPermissions();
    }



//custom methods
private void checkAndRequestPermissions() {
    if (allPermissionsGranted()) {
        // All permissions already granted
        proceedToMainActivity();
    } else {
        // Request permissions
        ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                PERMISSION_REQUEST_CODE
        );
    }
}

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (areAllPermissionsGranted(grantResults)) {
                // Only proceed if ALL permissions were granted
                proceedToMainActivity();
            } else {
                // At least one permission was denied
                handlePermissionDenial();
            }
        }
    }

    private boolean areAllPermissionsGranted(int[] grantResults) {
        if (grantResults.length < REQUIRED_PERMISSIONS.length) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void handlePermissionDenial() {
        Toast.makeText(this,
                "This app cannot function without all permissions",
                Toast.LENGTH_LONG).show();

        // Close the app completely
        finishAffinity();
    }


    private void proceedToMainActivity() {
         ///conecting views with backend
        ImageView Speed_round = findViewById(R.id.speed_round);
        ImageView Nameplate_opening_page = findViewById(R.id.nameplate_opening_page);

        //rotate animation
        ObjectAnimator rotate = ObjectAnimator.ofFloat(Speed_round, "rotation", 0f, 360f);
        rotate.setDuration(700);
        rotate.start();

        //nameplate animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(Nameplate_opening_page,"scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(Nameplate_opening_page,"scaleY", 0f, 1f);
        scaleX.setDuration(800);
        scaleY.setDuration(800);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();

        //jump from opening page to main page
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(OpeningScreen.this, MainActivity.class));
                        finish();
                    }
                }, 1500);// 1.5 seconds delay for splash screen
    }
}