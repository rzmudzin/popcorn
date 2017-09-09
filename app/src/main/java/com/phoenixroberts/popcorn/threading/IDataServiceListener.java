package com.phoenixroberts.popcorn.threading;

import android.content.Intent;

import com.phoenixroberts.popcorn.DataServiceBroadcastReceiver;

/**
 * Created by rzmudzinski on 9/2/17.
 */

public interface IDataServiceListener {
    void onDataServiceResult(DataServiceBroadcastReceiver.DataServicesEventType dataServicesEventType, Intent i);
}

