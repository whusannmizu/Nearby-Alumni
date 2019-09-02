package com.sannmizu.nearby_alumni.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreUtils extends Util {
    public static SharedPreferences sharedPreferences = null;
    public static void initialize() {
        if(sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        }
    }
    public static void putString(String key, String value) {
        initialize();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static void putInt(String key, int value) {
        initialize();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public static void putLong(String key, long value) {
        initialize();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }
    public static void putBoolean(String key, Boolean value) {
        initialize();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public static String getString(String key, String defaultValue) {
        initialize();
        return sharedPreferences.getString(key, defaultValue);
    }
    public static int getInt(String key, int defaultValue) {
        initialize();
        return sharedPreferences.getInt(key, defaultValue);
    }
    public static long getLong(String key, long defaultValue) {
        initialize();
        return sharedPreferences.getLong(key, defaultValue);
    }
    public static boolean getBoolean(String key, Boolean defaultValue) {
        initialize();
        return sharedPreferences.getBoolean(key, defaultValue);
    }
}
