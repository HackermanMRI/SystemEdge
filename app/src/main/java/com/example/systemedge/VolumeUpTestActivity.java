package com.example.systemedge;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VolumeUpTestActivity extends AppCompatActivity {

    private TextView statusText;
    public static final String EXTRA_TEST_RESULT_STATUS = "test_result_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_up_test);

        statusText = findViewById(R.id.status_text);
        Button buttonYes = findViewById(R.id.button_yes);
        Button buttonNo = findViewById(R.id.button_no);

        // Hide the status text initially
        statusText.setText("");

        buttonYes.setOnClickListener(v -> setResultAndFinish(TestItem.Status.PASSED));
        buttonNo.setOnClickListener(v -> setResultAndFinish(TestItem.Status.FAILED));
    }

    /**
     * This method is called by the Android system every time a physical key is pressed down.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 1. We check if the key that was pressed is the VOLUME_UP key.
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // 2. If it is, we update our UI to give the user feedback.
            statusText.setText("Volume Up Pressed!");

            // 3. We return 'true' to tell the system that we have handled this event.
            // This prevents the default system volume slider from appearing.
            return true;
        }

        // 4. For any other key, we let the system handle it as it normally would.
        return super.onKeyDown(keyCode, event);
    }

    private void setResultAndFinish(TestItem.Status status) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TEST_RESULT_STATUS, status.name());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}