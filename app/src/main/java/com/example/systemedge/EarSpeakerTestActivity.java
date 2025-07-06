package com.example.systemedge;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class EarSpeakerTestActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    // We will store the user's original volume for BOTH streams
    private int originalVoiceCallVolume;
    private int originalMusicVolume;

    public static final String EXTRA_TEST_RESULT_STATUS = "test_result_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ear_speaker_test);

        Button buttonYes = findViewById(R.id.button_yes);
        Button buttonNo = findViewById(R.id.button_no);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // We will now set up and start the player entirely in onResume
        // to ensure the audio environment is correct first.

        buttonYes.setOnClickListener(v -> setResultAndFinish(TestItem.Status.PASSED));
        buttonNo.setOnClickListener(v -> setResultAndFinish(TestItem.Status.FAILED));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startEarpieceSound();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopEarpieceSound();
    }

    private void startEarpieceSound() {
        if (audioManager == null) return;

        // --- SETUP THE AUDIO ENVIRONMENT FIRST ---

        // 1. Save the original volumes for both streams
        originalVoiceCallVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        originalMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // 2. Set the audio mode for earpiece playback
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(false);

        // 3. Max out the earpiece volume for the test
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);

        // 4. THIS IS THE NEW STEP: Mute the main loudspeaker stream completely.
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);


        // --- NOW, CREATE AND START THE PLAYER ---

        mediaPlayer = MediaPlayer.create(this, R.raw.beep_sound);
        if (mediaPlayer != null) {
            // We still set these attributes as it's good practice
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    private void stopEarpieceSound() {
        // Stop and release the player
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // CRITICAL: Always restore the audio settings back to their original state
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            // Restore BOTH original volumes
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, originalVoiceCallVolume, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalMusicVolume, 0);
        }
    }

    private void setResultAndFinish(TestItem.Status status) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TEST_RESULT_STATUS, status.name());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}