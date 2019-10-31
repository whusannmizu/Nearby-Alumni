package com.sannmizu.nearby_alumni.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sannmizu.nearby_alumni.denglu.LandingActivity;

public class AccountUtils extends Utils {
    private static boolean sLocked = getCurrentUserId() == 0;
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
    public static void setLocked(boolean b) {
        sLocked = b;
    }
    public static boolean getLocked() {
        return sLocked;
    }
    public static void requestLogin(Context context) {
        Intent intent = new Intent(context, LandingActivity.class);
        context.startActivity(intent);
    }
}
