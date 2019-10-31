package com.sannmizu.nearby_alumni.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AccountUtils extends Utils {
    public static int getCurrentUserId() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(sContext);
        return spf.getInt("currentUser", 0);
    }
    public static String getLogToken() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        return sharedPreferences.getString("logToken", "");
    }
    public static String getConnToken() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        return sharedPreferences.getString("connToken", "");
    }
}
