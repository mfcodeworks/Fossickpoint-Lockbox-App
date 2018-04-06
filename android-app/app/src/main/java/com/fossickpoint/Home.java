package com.fossickpoint;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fossickpoint.Constants.Commons;
import com.fossickpoint.Constants.UserConstants;
import com.fossickpoint.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dennisdarwis on 25/1/18.
 */

public class Home extends Fragment {

    TextView text_name;
    LinearLayout layout_daily_quote, layout_reminder, layout_no_reminder;
    ImageView loading_daily_quote, loading_reminder;
    TextView text_quote, text_quote_name;
    TextView text_datetime;

    SharedPreferences quotePrefs;

    String userName;

    String TAG = "Home";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        quotePrefs = getActivity().getSharedPreferences(UserConstants.QUOTE_PREFS, MODE_PRIVATE);

        text_name = v.findViewById(R.id.text_name);
        layout_daily_quote = v.findViewById(R.id.layout_daily_quote);
        layout_reminder = v.findViewById(R.id.layout_reminder);
        loading_daily_quote = v.findViewById(R.id.loading_daily_quote);
        loading_reminder = v.findViewById(R.id.loading_reminder);
        layout_no_reminder = v.findViewById(R.id.layout_no_reminder);

        text_quote = v.findViewById(R.id.text_quote);
        text_quote_name = v.findViewById(R.id.text_quote_name);

        text_datetime = v.findViewById(R.id.text_datetime);

        SharedPreferences userPrefs = getActivity().getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
        userName = userPrefs.getString(UserConstants.USERNAME, Commons.EMPTY_STRING);

        text_name.setText(userName);

        checkQuote();
        
        //loadQuote();
        loadSchedule();

        return v;
    }

    private void checkQuote() {
        String quoteText = quotePrefs.getString(UserConstants.QUOTE_TEXT, Commons.EMPTY_STRING);
        String quoteAuthor = quotePrefs.getString(UserConstants.QUOTE_AUTHOR, Commons.EMPTY_STRING);
        if(quoteText.isEmpty() || quoteAuthor.isEmpty()){
            loadQuote();
        } else{
            loading_daily_quote.clearAnimation();
            loading_daily_quote.setVisibility(View.GONE);
            layout_daily_quote.setVisibility(View.VISIBLE);
            text_quote.setText(quoteText);
            text_quote_name.setText("- "+quoteAuthor);
        }
    }

    private void loadQuote() {
        Utility.rotateImage(loading_daily_quote);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Commons.QUOTE_URL, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    loading_daily_quote.clearAnimation();
                    loading_daily_quote.setVisibility(View.GONE);
                    layout_daily_quote.setVisibility(View.VISIBLE);
                    Log.d(TAG, "loadQuote: "+response);
                    String quoteText = response.getString("quoteText");
                    String quoteAuthor = response.getString("quoteAuthor");
                    text_quote.setText(quoteText);
                    text_quote_name.setText("- "+quoteAuthor);
                    quotePrefs.edit().putString(UserConstants.QUOTE_TEXT, quoteText).commit();
                    quotePrefs.edit().putString(UserConstants.QUOTE_AUTHOR, quoteAuthor).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + errorResponse);
            }
        });
    }

    private void loadSchedule() {
        try {
            Utility.rotateImage(loading_reminder);
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userName", userName);

            StringEntity body = new StringEntity(jsonParams.toString());
            client.post(getActivity(), Commons.SCHEDULE, body, "application/json",
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            layout_reminder.setVisibility(View.VISIBLE);
                            loading_reminder.clearAnimation();
                            loading_reminder.setVisibility(View.GONE);
                            Log.d(TAG, "loadSchedule: "+response);
                            int status = response.getInt("status");
                            if(status== Commons.SUCCESS){
                                JSONArray available_dates = response.getJSONArray("available_dates");
                                if(available_dates.length()==0){
                                    layout_no_reminder.setVisibility(View.VISIBLE);
                                    layout_reminder.setVisibility(View.GONE);
                                } else{
                                    layout_no_reminder.setVisibility(View.GONE);
                                    layout_reminder.setVisibility(View.VISIBLE);
                                    JSONObject dates = available_dates.getJSONObject(available_dates.length()-1);
                                    String datetime = dates.getString("datetime");
                                    String time = dates.getString("time");
                                    Date parsedDate = Utility.parseDate(datetime);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(parsedDate);
                                    if(calendar.compareTo(Calendar.getInstance())>=0){
                                        String day = Utility.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
                                        int date = calendar.get(Calendar.DATE);
                                        String month = Utility.getMonth(calendar.get(Calendar.MONTH));
                                        int year = calendar.get(Calendar.YEAR);
                                        text_datetime.setText(day+", "+date+" "+month+" "+year+"\nat "+time);
                                    } else{
                                        layout_no_reminder.setVisibility(View.VISIBLE);
                                        layout_reminder.setVisibility(View.GONE);
                                    }
                                }
                            } else{
                                Log.d(TAG, "loadSchedule: FAILURE");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.e(TAG, "onFailure: " + errorResponse);
                    }
                });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
