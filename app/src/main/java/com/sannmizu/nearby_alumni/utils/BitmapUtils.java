package com.sannmizu.nearby_alumni.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

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
    public static String getPNGFromBitmapToBase64(Bitmap bitmap) {
        byte[] bytes;
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            bytes = outStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
}
