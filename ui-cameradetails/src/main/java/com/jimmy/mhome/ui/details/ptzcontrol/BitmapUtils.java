package com.jimmy.mhome.ui.details.ptzcontrol;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public final class BitmapUtils {

    public static Bitmap decode(Resources resources, int id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeResource(resources, id, options);
    }

    public static void decode(Resources resources, int i, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, i, options);
    }

    public static Bitmap scaleDecode(Resources resources, int id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 2;
        return BitmapFactory.decodeResource(resources, id, options);
    }
}