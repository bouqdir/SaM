package com.dm.sam.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.dm.sam.R;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(2 * 1000);
                    // After 2 seconds redirect to another intent
                    Intent i = new Intent(getBaseContext(), PermissionsActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();

                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();
    }

}