package com.phoenixroberts.popcorn;

import android.content.Intent;

/**
 * Created by rzmudzinski on 9/2/17.
 */

public interface IDataServiceListener {
    void onDataServiceResult(DataServiceBroadcastReceiver.DataServicesEventType dataServicesEventType, Intent i);
}

