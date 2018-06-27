package com.domotica.domotica_bsnet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
	// Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 1000;
    static final String TAG = "SplashActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Log.d(TAG, "OnCreate Splash Activity");
        
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_splash);
		
		TimerTask task = new TimerTask() {
            @Override
            public void run() {
 
                // Start the next activity
                Intent mainIntent = new Intent().setClass(
                		SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
 
                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };
 
        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
	}
}
