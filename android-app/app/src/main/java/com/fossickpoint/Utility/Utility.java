package com.fossickpoint.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import android.support.v4.app.NotificationCompat;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.fossickpoint.R;
import com.fossickpoint.Service.Appointment;
import com.fossickpoint.Service.DailyQuote;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dennisdarwis on 22/1/18.
 */

public class Utility {
    public static void rotateImage(ImageView imageView){
        float ROTATE_FROM = 0.0f;
        float ROTATE_TO = 10.0f * 360.0f;// 3.141592654f * 32.0f;
        RotateAnimation animation = new RotateAnimation(ROTATE_FROM, ROTATE_TO,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(5000);
        imageView.startAnimation(animation);
    }

    public static String getDayOfWeek(int i) {
        switch(i){
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
        }
        return null;
    }

    public static String getMonth(int i) {
        switch(i){
            case Calendar.JANUARY:
                return "January";
            case Calendar.FEBRUARY:
                return "February";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "August";
            case Calendar.SEPTEMBER:
                return "September";
            case Calendar.OCTOBER:
                return "October";
            case Calendar.NOVEMBER:
                return "November";
            case Calendar.DECEMBER:
                return "December";
        }
        return null;
    }

    public static NotificationCompat.Builder notificationBuilder(String text, Context context)
    {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(context)
            .setContentTitle(context.getResources().getString(R.string.app_name))
            .setContentText(text)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setVibrate(new long[] {2000, 500, 500, 0, 500})
            .setSmallIcon(R.drawable.icon)
            //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon))
            .setColor(Color.parseColor("#c1282d"));
    }

    public static Date parseDate(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        Date result = null;
        try {
            result = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void startServices(Activity activity){
        activity.startService(new Intent(activity, DailyQuote.class));
        activity.startService(new Intent(activity, Appointment.class));
    }

    public static void stopServices(Activity activity) {
        activity.stopService(new Intent(activity, DailyQuote.class));
        activity.stopService(new Intent(activity, Appointment.class));
    }
}
