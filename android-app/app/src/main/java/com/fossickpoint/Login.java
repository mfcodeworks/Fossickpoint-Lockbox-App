package com.fossickpoint;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fossickpoint.Constants.Commons;
import com.fossickpoint.Constants.UserConstants;
import com.fossickpoint.Utility.CustomDialog;
import com.fossickpoint.Utility.Utility;
import com.fossickpoint.fonts.SetFonts;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Login extends AppCompatActivity {

    EditText form_username, form_password;
    RelativeLayout button_login;
    TextView text_create_account;

    Dialog loadingDialog;
    String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        form_username = findViewById(R.id.form_username);
        form_password = findViewById(R.id.form_password);
        form_username.setTypeface(SetFonts.HelveticaNeue(this));
        form_password.setTypeface(SetFonts.HelveticaNeue(this));
        text_create_account = findViewById(R.id.text_create_account);
        button_login = findViewById(R.id.button_login);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        text_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openBrowser();
                toRegister();
            }
        });
    }

    private void toRegister() {
        Intent intent = new Intent(this, Register.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void openBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://18.220.130.171:8000/toolbox/register/"));
        startActivity(browserIntent);
    }

    private void validate() {
        boolean usernameValid = false;
        boolean passwordValid = false;
        if(!form_username.getText().toString().isEmpty()){
            usernameValid = true;
            form_username.setError(null);
        } else{
            form_username.setError("Username cannot be empty");
        }

        if(!form_password.getText().toString().isEmpty()){
            passwordValid = true;
            form_password.setError(null);
        } else{
            form_password.setError("Password cannot be empty");
        }

        if(usernameValid && passwordValid){
            login();
        }
    }

    private void login() {
        try {
            loadingDialog = new CustomDialog().LoadingDialog(this);
            loadingDialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userName", form_username.getText().toString());
            jsonParams.put("password", form_password.getText().toString());

            StringEntity body = new StringEntity(jsonParams.toString());
            client.post(this, Commons.LOGIN_URL, body, "application/json",
                    new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Log.d(TAG, "login: "+response);
                        int status = response.getInt("status");
                        if(status== Commons.SUCCESS){
                            SharedPreferences userPrefs = getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
                            userPrefs.edit().putString(UserConstants.USERNAME, form_username.getText().toString()).commit();
                            toHomeScreen();
                            Utility.startServices(Login.this);
                            //checkUser();
                            Log.d(TAG, "login: SUCCESS");
                        } else{
                            loadingDialog.dismiss();
                            Log.d(TAG, "login: FAILURE");
                            new CustomDialog().AlertDialog(Login.this, "Login Failed",
                                    "Username or password is invalid");
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

    private void toHomeScreen() {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
        //super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
