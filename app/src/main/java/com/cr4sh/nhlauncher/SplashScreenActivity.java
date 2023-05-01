package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MyPreferences myPreferences = new MyPreferences(this);

        super.onCreate(savedInstanceState);

        // Set the content view for the splash screen
        setContentView(R.layout.splash_screen);

        // Load the animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Find the ImageView in the layout
        ImageView imageView = findViewById(R.id.image_view_splash);

        @SuppressLint("DiscouragedApi") int frame = getResources().getIdentifier(myPreferences.logoIcon(), "drawable", getPackageName());
        imageView.setImageResource(frame);

        // Apply the animation to the ImageView
        imageView.startAnimation(fadeInAnimation);

        // Create a new handler to start the MainActivity after a delay
        new Handler().postDelayed(() -> {
            // Create a new intent to start the MainActivity
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);

            // Finish the splash screen activity
            finish();
        }, myPreferences.splashDuration());
    }
}
