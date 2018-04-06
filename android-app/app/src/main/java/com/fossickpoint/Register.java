package com.fossickpoint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.fossickpoint.Constants.Commons;
import com.fossickpoint.Constants.UserConstants;
import com.fossickpoint.Utility.CustomDialog;
import com.fossickpoint.fonts.SetFonts;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Register extends AppCompatActivity {

    EditText form_username, form_password, form_email;
    RadioButton radio_button_male, radio_button_female;
    RelativeLayout button_register;

    Dialog loadingDialog;
    String TAG = "Register";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        form_username = findViewById(R.id.form_username);
        form_password = findViewById(R.id.form_password);
        form_email = findViewById(R.id.form_email);
        radio_button_male = findViewById(R.id.radio_button_male);
        radio_button_female = findViewById(R.id.radio_button_female);
        button_register = findViewById(R.id.button_register);

        form_username.setTypeface(SetFonts.HelveticaNeue(this));
        form_password.setTypeface(SetFonts.HelveticaNeue(this));
        form_email.setTypeface(SetFonts.HelveticaNeue(this));
        radio_button_male.setTypeface(SetFonts.HelveticaNeue(this));
        radio_button_female.setTypeface(SetFonts.HelveticaNeue(this));

        radio_button_male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    radio_button_female.setChecked(false);
                }
            }
        });
        radio_button_female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    radio_button_male.setChecked(false);
                }
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    private void validate() {
        boolean usernameValid = false;
        boolean passwordValid = false;
        boolean emailValid = false;
        boolean genderValid = false;

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
        if(!form_email.getText().toString().isEmpty()){
            emailValid = true;
            form_email.setError(null);
        } else{
            form_email.setError("Email cannot be empty");
        }
        if(radio_button_male.isChecked() || radio_button_female.isChecked()){
            genderValid = true;
            radio_button_female.setError(null);
        } else{
            radio_button_female.setError("Gender cannot be empty");
        }

        if(usernameValid && passwordValid && emailValid && genderValid){
            register();
        }
    }

    private void register() {
        try {
            loadingDialog = new CustomDialog().LoadingDialog(this);
            loadingDialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userName", form_username.getText().toString());
            jsonParams.put("password", form_password.getText().toString());
            jsonParams.put("email", form_email.getText().toString());
            String gender = null;
            if(radio_button_male.isChecked()){
                gender = "male";
            } else{
                gender = "female";
            }
            jsonParams.put("gender", gender);

            StringEntity body = new StringEntity(jsonParams.toString());
            client.post(this, Commons.REGISTER_URL, body, "application/json",
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        loadingDialog.dismiss();
                        try {
                            Log.d(TAG, "login: "+response);
                            if(response.has("status")){
                                int status = response.getInt("status");
                                if(status==1){
                                    new CustomDialog().ClickableDialog(Register.this,
                                        "Registration Success", "Please proceed to log in", new Dialog.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                toLogin();
                                            }
                                        });
                                }
                            } else{
                                if(response.has("userName")){
                                    form_username.setError("Username is not available");
                                }
                                if(response.has("email")){
                                    form_email.setError("Email is not available");
                                }
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

    private void toLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
