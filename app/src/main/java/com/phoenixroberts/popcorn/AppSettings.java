package com.phoenixroberts.popcorn;

import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rzmudzinski on 9/13/17.
 */

public class AppSettings {
    public static class Settings {
        public static final String Sort_Order = "SortOrder";
    }
    public static String get(String settingName) {
        SharedPreferences preferences = AppMain.getAppContext().getSharedPreferences(AppMain.getAppName(), MODE_PRIVATE);
        String settingValue = preferences.getString(settingName, "");
        return settingValue;
    }
    public static void set(String settingName, String settingValue) {
        SharedPreferences.Editor preferenceEditor = AppMain.getAppContext().getSharedPreferences(AppMain.getAppName(), MODE_PRIVATE).edit();
        preferenceEditor.clear();
        preferenceEditor.putString(settingName, settingValue);
        //preferenceEditor.putString("sort_order", DataService.SortOrder.Rating_Ascending);
        preferenceEditor.commit();
    }
}
