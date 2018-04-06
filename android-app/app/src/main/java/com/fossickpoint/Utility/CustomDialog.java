package com.fossickpoint.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.fossickpoint.R;

/**
 * Created by dennisdarwis on 22/1/18.
 */

public class CustomDialog {
    public Dialog LoadingDialog(Activity act){
        Dialog dialog = new Dialog(act);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(act.getLayoutInflater().inflate(R.layout.dialog_loading, null));
        ImageView image_icon = dialog.findViewById(R.id.image_icon);
        float ROTATE_FROM = 0.0f;
        float ROTATE_TO = 10.0f * 360.0f;// 3.141592654f * 32.0f;
        RotateAnimation animation = new RotateAnimation(ROTATE_FROM, ROTATE_TO,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(5000);
        image_icon.startAnimation(animation);
        return dialog;
    }

    public void AlertDialog(Activity act, String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setIcon(R.drawable.icon);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    public void ClickableDialog(Activity act, String title, String message, DialogInterface.OnClickListener clickListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setIcon(R.drawable.icon);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", clickListener);
        dialog.show();
    }
}
