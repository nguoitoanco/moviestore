package com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;

/**
 * Created by KhanhNV10 on 2015/12/01.
 */
public class SharedPreferencesManager {

    public static final String FAV_NUMBER = "fav_number";

    public static void savePreference(Context context,String name, String value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        if (name != null && value != null)
        {
            editor.putString(name, value);
        }
        editor.commit();

    }

    public static String loadPreference(Context context, String name, String valueDefault) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(name, valueDefault);
    }

    public static int loadIntPreference(Context context, String name) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(name, StringUtil.DEFAULT));
    }

    public static String updatePreference(Context context, String name, boolean isIncrease) {
        String reBefore = loadPreference(context, name, StringUtil.DEFAULT);
        int reBeforeNum = Integer.parseInt(reBefore);
        if (isIncrease) {
            reBeforeNum++;
        } else if (reBeforeNum > 0) {
            reBeforeNum--;
        }

        savePreference(context, name, String.valueOf(reBeforeNum));
        return String.valueOf(reBeforeNum);
    }

}
