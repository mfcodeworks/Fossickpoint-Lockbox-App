package com.fossickpoint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.fossickpoint.Constants.SettingsConstants;
import com.fossickpoint.Constants.UserConstants;
import com.fossickpoint.Service.DailyQuote;
import com.fossickpoint.Utility.Utility;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dennisdarwis on 27/1/18.
 */

public class Settings extends Fragment {
    Switch switch_quotes, switch_appointment;
    LinearLayout button_rate, button_help, button_contact, button_logout;
    boolean flagQuotes, flagAppointment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        flagQuotes = sharedPref.getBoolean(SettingsConstants.QUOTES, true);
        flagAppointment = sharedPref.getBoolean(SettingsConstants.APPOINTMENTS, true);

        switch_quotes = v.findViewById(R.id.switch_quotes);
        switch_appointment = v.findViewById(R.id.switch_appointment);

        button_rate = v.findViewById(R.id.button_rate);
        button_help = v.findViewById(R.id.button_help);
        button_contact = v.findViewById(R.id.button_contact);
        button_logout = v.findViewById(R.id.button_logout);

        if(flagQuotes){
            switch_quotes.setChecked(true);
        } else{
            switch_quotes.setChecked(false);
        }
        if(flagAppointment){
            switch_appointment.setChecked(true);
        } else{
            switch_appointment.setChecked(false);
        }
        switch_quotes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPref.edit().putBoolean(SettingsConstants.QUOTES, b).commit();
            }
        });
        switch_appointment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPref.edit().putBoolean(SettingsConstants.APPOINTMENTS, b).commit();
            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutConfirmation();
            }
        });

        return v;
    }

    private void logoutConfirmation(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setIcon(R.drawable.icon);
        dialog.setTitle("Log Out");
        dialog.setMessage("Are you sure you want to log out?");
        dialog.setPositiveButton("YES", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences userPrefs = getActivity().getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
                userPrefs.edit().clear().commit();
                SharedPreferences quotePrefs = getActivity().getSharedPreferences(UserConstants.QUOTE_PREFS, MODE_PRIVATE);
                quotePrefs.edit().clear().commit();
                Utility.stopServices(getActivity());
                toLogin();
            }
        });
        dialog.setNegativeButton("NO", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void toLogin() {
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
