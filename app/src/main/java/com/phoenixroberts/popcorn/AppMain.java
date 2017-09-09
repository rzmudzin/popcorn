package com.phoenixroberts.popcorn;

/**
 * Created by rzmudzinski on 8/27/17.
 */

import android.app.Application;
import android.content.Context;


public class AppMain extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        AppMain.context = getApplicationContext();
        DataServiceBroadcastReceiver.getInstance().Register(AppMain.context);


    }

    public static Context getAppContext() {
        return AppMain.context;
    }


    public static class BundleExtraType {
        public static final String MovieId = "MovieId";
    }

}
