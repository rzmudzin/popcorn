package com.phoenixroberts.popcorn;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rzmudzinski on 9/13/17.
 */

public class AppSettings {
    public static class Settings {
        public static final String List_Sort_Order = "ListSortOrder";
        public static final String Discovery_Sort_Order = "DiscoverySortOrder";
        public static final String APKI_Key = "APIKey";
    }
    public static String get(String settingName) {
        String settingValue = null;
        Context context = AppMain.getAppContext();
        if(context!=null) {
            SharedPreferences preferences = context.getSharedPreferences(AppMain.getAppName(), MODE_PRIVATE);
            settingValue = preferences.getString(settingName, "");
        }
        return settingValue;
    }
    public static void set(String settingName, String settingValue) {
        Context context = AppMain.getAppContext();
        if(context!=null) {
            SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(AppMain.getAppName(), MODE_PRIVATE).edit();
            preferenceEditor.putString(settingName, settingValue);
            preferenceEditor.apply();
        }
    }
}
