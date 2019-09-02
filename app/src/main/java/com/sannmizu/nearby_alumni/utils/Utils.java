package com.sannmizu.nearby_alumni.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils extends Util{
    public static String getRandomString(int length) {
        char[] str= new char[length];
        int i = 0;
        int num=3;//数字的个数
        while (i < length) {
            int f = (int) (Math.random() * num);
            if (f == 0)
                str[i] = (char) ('A' + Math.random() * 26);
            else if (f == 1)
                str[i] = (char) ('a' + Math.random() * 26);
            else
                str[i] = (char) ('0' + Math.random() * 10);
            i++;
        }
        String random_str = new String(str);
        return random_str;
    }
    public static String format(String data) {
        data = data.replace("“","\"");
        data = data.replace("”","\"");
        return data;
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
