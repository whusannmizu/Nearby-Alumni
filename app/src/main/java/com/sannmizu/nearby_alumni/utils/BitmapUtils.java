package com.sannmizu.nearby_alumni.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtils extends Util {
    public static Bitmap zoomBitmap(Bitmap bitmap, float scale) {
        int rawWidth = bitmap.getWidth();
        int rawHeight = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeight, matrix, true);
    }
    public static Bitmap zoomBitmap(Bitmap bitmap, float width, float height) {
        int rawWidth = bitmap.getWidth();
        int rawHeight = bitmap.getHeight();

        float scaleWidth = width / rawWidth;
        float scaleHeight = height / rawHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeight, matrix, true);
    }
}
