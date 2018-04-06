package com.fossickpoint;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.fossickpoint.Constants.Commons;
import com.fossickpoint.Constants.UserConstants;
import com.fossickpoint.Utility.CustomDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HomeScreen extends AppCompatActivity {

    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;
    public static Dialog loadingDialog;
    boolean flagExit = false;

    String TAG = "HomeScreen";

    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        fragmentManager = getFragmentManager();

        SharedPreferences userPrefs = getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
        userName = userPrefs.getString(UserConstants.USERNAME, Commons.EMPTY_STRING);
        checkUser();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment fragment = null;
                switch(id){
                    case R.id.menu_home:
                        fragment = new Home();
                        Log.d(TAG, "onNavigationItemSelected: menu_home");
                        break;
                    case R.id.menu_articles:
                        fragment = new Content();
                        Log.d(TAG, "onNavigationItemSelected: menu_articles");
                        break;
                    case R.id.menu_accounts:
                        fragment = new Settings();
                        Log.d(TAG, "onNavigationItemSelected: menu_accounts");
                        break;
                }
                if(fragment!=null){
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.root_view, fragment);
                    fragmentTransaction.commit();
                }
              return true;
            }
        });
        //toHomeScreen();
    }

    private void toHomeScreen() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.root_view, new Home());
        fragmentTransaction.commit();
    }

    private void checkUser() {
        try {
            loadingDialog = new CustomDialog().LoadingDialog(this);
            loadingDialog.setCancelable(false);
            loadingDialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userName", userName);

            StringEntity body = new StringEntity(jsonParams.toString());
            client.post(this, Commons.CHECK_USER_URL, body, "application/json",
                    new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            loadingDialog.dismiss();
                            try {
                                Log.d(TAG, "login: "+response);
                                int status = response.getInt("status");
                                if(status== Commons.SUCCESS){
                                    String username = response.getString("username");
                                    int user_type = response.getInt("user_type");
                                    String appointment_type_id = response.getString("appointment_type_id");
                                    SharedPreferences userPrefs = getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
                                    userPrefs.edit().putString(UserConstants.USERNAME, username).commit();
                                    userPrefs.edit().putInt(UserConstants.USER_TYPE, user_type).commit();
                                    if(user_type==1){
                                        userPrefs.edit().remove(UserConstants.APPOINTMENT_TYPE_ID).commit();
                                    } else{
                                        userPrefs.edit().putString(UserConstants.APPOINTMENT_TYPE_ID, appointment_type_id).commit();
                                    }
                                    toHomeScreen();
                                } else{
                                    Log.d(TAG, "login: FAILURE");
                                    new CustomDialog().AlertDialog(HomeScreen.this, "Login Failed",
                                            "Username does not exist");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                              JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            loadingDialog.dismiss();
                            Log.e(TAG, "onFailure: " + errorResponse);
                        }
                    });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(!flagExit){
            flagExit = true;
            Toast.makeText(HomeScreen.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    flagExit = false;
                }
            }, 2000);
        } else{
            this.finishAffinity();
        }
    }

    @Override
    protected void onStop() {
        /*SharedPreferences userPrefs = getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
        userPrefs.edit().clear().commit();*/
        super.onStop();
    }
}
