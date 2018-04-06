package com.fossickpoint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.fossickpoint.fonts.SetFonts;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RedeemCode extends AppCompatActivity {

    EditText form_code;
    RelativeLayout button_verify;
    TextView text_buy_plan;

    Dialog loadingDialog;
    String TAG = "RedeemCode";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_code);

        form_code = findViewById(R.id.form_code);
        form_code.setTypeface(SetFonts.HelveticaNeue(this));

        button_verify = findViewById(R.id.button_verify);

        text_buy_plan = findViewById(R.id.text_buy_plan);

        button_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });


    }

    private void validate() {
        boolean codeValid = false;
        if(!form_code.getText().toString().isEmpty()){
            codeValid = true;
            form_code.setError(null);
        } else{
            form_code.setError("Code cannot be empty");
        }
        if(codeValid){
            redeemCode();
        }
    }

    private void redeemCode() {
        try {
            loadingDialog = new CustomDialog().LoadingDialog(this);
            loadingDialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            SharedPreferences userPrefs = getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
            String userName = userPrefs.getString(UserConstants.USERNAME, Commons.EMPTY_STRING);
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userName", userName);
            jsonParams.put("coupon_code", form_code.getText().toString());
            Log.d(TAG, "redeemCode: jsonParams: "+jsonParams);
            StringEntity body = new StringEntity(jsonParams.toString());
            client.post(this, Commons.REDEEM_CODE, body, "application/json",
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            Log.d(TAG, "login: "+response);
                            int status = response.getInt("status");
                            if(status== Commons.SUCCESS){
                                String appointment_type_id = response.getString("appointment_type_id");
                                String coupon_code = response.getString("coupon_code");
                                int userType = response.getInt("userType");
                                String plan_name = response.getString("plan_name");

                                new CustomDialog().ClickableDialog(RedeemCode.this,
                                    "CONGRATS!", "Your account has been assigned with " + plan_name,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            toHomeScreen();
                                        }
                                    });

                                Log.d(TAG, "login: SUCCESS");
                            } else{
                                loadingDialog.dismiss();
                                Log.d(TAG, "login: FAILURE");
                                new CustomDialog().AlertDialog(RedeemCode.this, "Invalid",
                                    "Coupon code is invalid");
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
}
