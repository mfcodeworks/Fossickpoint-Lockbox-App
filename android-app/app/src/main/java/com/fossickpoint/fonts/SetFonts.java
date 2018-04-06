package com.fossickpoint.fonts;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by dennisdarwis on 25/1/18.
 */

public class SetFonts {
    public static Typeface HelveticaNeue(Context ctx){
        return Typeface.createFromAsset(ctx.getAssets(), "fonts/HelveticaNeue.ttf");
    }
    public static Typeface HelveticaNeueItalic(Context ctx){
        return Typeface.createFromAsset(ctx.getAssets(), "fonts/HelveticaNeueIt.ttf");
    }
    public static Typeface HelveticaNeueBold(Context ctx){
        return Typeface.createFromAsset(ctx.getAssets(), "fonts/HelveticaNeueBd.ttf");
    }
    public static Typeface HelveticaNeueMedium(Context ctx){
        return Typeface.createFromAsset(ctx.getAssets(), "fonts/HelveticaNeueMd.ttf");
    }
}
