package com.example.tejpatel.colorpalette;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread splashScreenThread = new Thread() {
            @Override
            public void run() {
                try {

                    // Splash screen will remain on screen for 2 seconds
                    sleep(2000);

                    // Intent to open MainActivity after splash screen
                    Intent splashScreenIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(splashScreenIntent);

                    // Destroy splash screen activity from memory after MainActivity is opened
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        splashScreenThread.start();

    }
}
