package com.fossickpoint.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.fossickpoint.Constants.Commons;
import com.fossickpoint.Constants.SettingsConstants;
import com.fossickpoint.Constants.UserConstants;
import com.fossickpoint.SplashScreen;
import com.fossickpoint.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DailyQuote extends Service {
    String TAG = "DailyQuote";
    Handler handler;
    Runnable runnable;
    boolean isRunning;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        isRunning = true;
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Log.d(TAG, "run: service is running");
                if(isRunning){
                    loadQuote();
                    handler.postDelayed(runnable, 300000);
                }

            }
        };
        handler.postDelayed(runnable, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void loadQuote() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Commons.QUOTE_URL, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.d(TAG, "loadQuote: "+response);
                    String quoteText = response.getString("quoteText");
                    String quoteAuthor = response.getString("quoteAuthor");
                    String notifMessage = "\""+quoteText+"\""+" - "+quoteAuthor;

                    SharedPreferences quotePrefs = getSharedPreferences(UserConstants.QUOTE_PREFS, MODE_PRIVATE);
                    quotePrefs.edit().putString(UserConstants.QUOTE_TEXT, quoteText).commit();
                    quotePrefs.edit().putString(UserConstants.QUOTE_AUTHOR, quoteAuthor).commit();

                    final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(DailyQuote.this);
                    boolean flagQuotes = sharedPref.getBoolean(SettingsConstants.QUOTES, true);
                    if(flagQuotes){
                        NotificationCompat.Builder notification = Utility.notificationBuilder(notifMessage, DailyQuote.this);
                        Intent resultIntent = new Intent(DailyQuote.this, SplashScreen.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent piResult = PendingIntent.getActivity(DailyQuote.this, 0, resultIntent, 0);
                        notification.setContentIntent(piResult);

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        piResult = PendingIntent.getActivity(DailyQuote.this, 0, resultIntent, 0);
                        notification.setContentIntent(piResult);
                        manager.notify(0, notification.build());
                    }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        handler.removeCallbacks(runnable);
        Log.d(TAG, "onDestroy: ");
    }
}
