package com.example.systemedge;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LoudspeakerTestActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    // A key to send the result back in the Intent
    public static final String EXTRA_TEST_RESULT_STATUS = "test_result_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loudspeaker_test);

        Button buttonYes = findViewById(R.id.button_yes);
        Button buttonNo = findViewById(R.id.button_no);

        // Prepare the MediaPlayer to play our beep sound
        setupMediaPlayer();

        buttonYes.setOnClickListener(v -> {
            // Set the result to PASSED and finish
            setResultAndFinish(TestItem.Status.PASSED);
        });

        buttonNo.setOnClickListener(v -> {
            // Set the result to FAILED and finish
            setResultAndFinish(TestItem.Status.FAILED);
        });
    }

    /**
     * Initializes and configures the MediaPlayer.
     */
    private void setupMediaPlayer() {
        // Create a MediaPlayer instance from our sound file in the res/raw folder
        mediaPlayer = MediaPlayer.create(this, R.raw.beep_sound);
        // Set the player to loop continuously
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
        }
    }

    /**
     * Safely stops and releases the MediaPlayer resources.
     */
    private void stopAndReleasePlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Packages the test result into an Intent and closes the activity.
     * @param status The result of the test (PASSED or FAILED).
     */
    private void setResultAndFinish(TestItem.Status status) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TEST_RESULT_STATUS, status.name());
        setResult(RESULT_OK, resultIntent);
        finish(); // Closes this activity and sends the result back
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start playing the sound when the activity becomes visible
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // CRITICAL: Always stop and release the player when the activity is no longer visible.
        // This prevents sound from continuing to play in the background and avoids memory leaks.
        stopAndReleasePlayer();
    }
}