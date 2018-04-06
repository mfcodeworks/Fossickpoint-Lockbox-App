package com.fossickpoint.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by dennisdarwis on 25/1/18.
 */

public class HelveticaNeue extends TextView {
    public HelveticaNeue(Context context) {
        super(context);
        this.setTypeface(SetFonts.HelveticaNeue(context));
    }

    public HelveticaNeue(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(SetFonts.HelveticaNeue(context));
    }

    public HelveticaNeue(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(SetFonts.HelveticaNeue(context));
    }

    public HelveticaNeue(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTypeface(SetFonts.HelveticaNeue(context));
    }
}
