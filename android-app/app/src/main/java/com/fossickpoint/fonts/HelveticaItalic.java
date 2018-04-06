package com.fossickpoint.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by dennisdarwis on 25/1/18.
 */

public class HelveticaItalic extends TextView {
    public HelveticaItalic(Context context) {
        super(context);
        this.setTypeface(SetFonts.HelveticaNeueItalic(context));
    }

    public HelveticaItalic(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(SetFonts.HelveticaNeueItalic(context));
    }

    public HelveticaItalic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(SetFonts.HelveticaNeueItalic(context));
    }

    public HelveticaItalic(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTypeface(SetFonts.HelveticaNeueItalic(context));
    }
}
