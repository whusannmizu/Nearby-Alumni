package com.sannmizu.nearby_alumni.Utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static String readAssertsFile(Context context, String fileName) {
        InputStream is = null;
        String msg = null;
        try {
            is = context.getResources().getAssets().open(fileName);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            msg = new String(bytes);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }
}
