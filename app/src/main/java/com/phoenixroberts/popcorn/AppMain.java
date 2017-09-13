package com.phoenixroberts.popcorn;

/**
 * Created by rzmudzinski on 8/27/17.
 */

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.phoenixroberts.popcorn.data.DataService;
import com.phoenixroberts.popcorn.data.DataServiceBroadcastReceiver;


public class AppMain extends Application {

    private static Context m_Context;
    private static final String m_AppName = "popcorn";

    public void onCreate() {
        super.onCreate();
        AppMain.m_Context = getApplicationContext();
        DataServiceBroadcastReceiver.getInstance().Register(AppMain.m_Context);
        //DataService.SortOrder.values();

    }

    public static Context getAppContext() {
        return AppMain.m_Context;
    }

    public static String getAppName() {
        return m_AppName;
    }

    public static class BundleExtraType {
        public static final String MovieId = "MovieId";
    }

}
