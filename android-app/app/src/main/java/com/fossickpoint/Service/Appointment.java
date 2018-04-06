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

import com.fossickpoint.Constants.Commons;
import com.fossickpoint.Constants.SettingsConstants;
import com.fossickpoint.Constants.UserConstants;
import com.fossickpoint.SplashScreen;
import com.fossickpoint.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Appointment extends Service {
    String TAG = "Appointment";
    Handler handler;
    Runnable runnable;
    boolean isRunning;
    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Log.d(TAG, "run: service is running");
                if(isRunning){
                    loadSchedule();
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

    private void loadSchedule() {
        try {
            SharedPreferences userPrefs = getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
            String userName = userPrefs.getString(UserConstants.USERNAME, Commons.EMPTY_STRING);
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userName", userName);

            StringEntity body = new StringEntity(jsonParams.toString());
            client.post(Appointment.this, Commons.SCHEDULE, body, "application/json",
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Log.d(TAG, "loadSchedule: "+response);
                            int status = response.getInt("status");
                            String notifMessage = null;
                            if(status== Commons.SUCCESS){
                                JSONArray available_dates = response.getJSONArray("available_dates");
                                if(available_dates.length()==0){

                                } else{
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
                                        notifMessage = "You have upcoming schedule at: "+day+", "+date+" "+month+" "+year+", at "+time;

                                        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Appointment.this);
                                        boolean flagAppointment = sharedPref.getBoolean(SettingsConstants.APPOINTMENTS, true);
                                        if(flagAppointment){
                                            NotificationCompat.Builder notification = Utility.notificationBuilder(notifMessage, Appointment.this);
                                            Intent resultIntent = new Intent(Appointment.this, SplashScreen.class);
                                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            PendingIntent piResult = PendingIntent.getActivity(Appointment.this, 0, resultIntent, 0);
                                            notification.setContentIntent(piResult);

                                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            piResult = PendingIntent.getActivity(Appointment.this, 0, resultIntent, 0);
                                            notification.setContentIntent(piResult);
                                            manager.notify(0, notification.build());
                                        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        isRunning = false;
        handler.removeCallbacks(runnable);
    }
}
