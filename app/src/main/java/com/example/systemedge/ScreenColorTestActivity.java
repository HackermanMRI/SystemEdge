package com.example.systemedge;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScreenColorTestActivity extends AppCompatActivity {

    private FrameLayout colorContainer;
    private int currentColorIndex = 0;

    // An array holding the sequence of colors for the test
    private final int[] testColors = {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.WHITE,
            Color.BLACK
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_color_test);

        // Make the activity truly full-screen
        hideSystemUI();

        colorContainer = findViewById(R.id.color_test_container);

        // Set the initial color and show the first toast
        updateColor();

        // Set a click listener on the container to cycle through colors
        colorContainer.setOnClickListener(v -> {
            // Move to the next color in the array
            currentColorIndex++;

            // Check if there are more colors to display
            if (currentColorIndex < testColors.length) {
                updateColor();
            } else {
                // If we've shown all colors, finish the activity
                finish();
            }
        });
    }

    /**
     * Updates the background color and shows a guiding toast message.
     */
    private void updateColor() {
        colorContainer.setBackgroundColor(testColors[currentColorIndex]);
        Toast.makeText(this, "Tap to continue", Toast.LENGTH_SHORT).show();
    }

    /**
     * Hides the system status bar and navigation bar for an immersive, full-screen experience.
     */
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Use the deprecated system flags for older versions
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }
}