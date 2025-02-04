package com.example.systemedge;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OpeningScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.opening_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //conecting views with backend
        ImageView Speed_round = findViewById(R.id.speed_round);
        ImageView Speed_stick = findViewById(R.id.speed_stick);
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
                }, 1500);
    }
}