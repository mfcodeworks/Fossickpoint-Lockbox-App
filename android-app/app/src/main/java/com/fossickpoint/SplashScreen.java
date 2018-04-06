package com.fossickpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fossickpoint.Constants.Commons;
import com.fossickpoint.Constants.UserConstants;
import com.fossickpoint.Utility.Utility;

public class SplashScreen extends AppCompatActivity {
    String TAG = "SplashScreen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences userPrefs = getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
        final String userName = userPrefs.getString(UserConstants.USERNAME, Commons.EMPTY_STRING);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userName.isEmpty()){
                    toLogin();
                } else{
                    Log.d(TAG, "Logged in: ");
                    //toLogin(); // TODO: REMOVE THIS NTARAN
                    toHomeScreen();
                    Utility.startServices(SplashScreen.this);
                }
            }
        }, 1500);
    }

    private void toHomeScreen() {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void toLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
