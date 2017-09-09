package com.phoenixroberts.popcorn;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.phoenixroberts.popcorn.threading.IDataServiceListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rzmudzinski on 9/2/17.
 */

public class DataServiceBroadcastReceiver extends BroadcastReceiver {

    private boolean m_bIsRegistered;
    private List<IDataServiceListener> m_DataServiceListeners;
    private static DataServiceBroadcastReceiver m_Instance = new DataServiceBroadcastReceiver();

    public enum DataServicesEventType {
        Invalid,
        ItemFetchSuccess,
        ItemFetchFail,
        ListFetchSuccess,
        ListFetchFail
    }
    public enum DataServicesEventExtra {
        MovieId
    }
    public static final String IntentFilter = "com.dataservice.result";

    private DataServiceBroadcastReceiver() {
        m_DataServiceListeners = new ArrayList<IDataServiceListener>();
    }

    public static DataServiceBroadcastReceiver getInstance() {
        return m_Instance;
    }

    public void addListener(IDataServiceListener listenerInstance) {

        m_DataServiceListeners.add(listenerInstance);
    }
    public void removeListener(IDataServiceListener listenerInstance) {
        m_DataServiceListeners.remove(listenerInstance);
    }
    public void Register(Context c) {
        if(m_bIsRegistered==false) {
            c.registerReceiver(this, new IntentFilter(DataServiceBroadcastReceiver.IntentFilter));
        }
        m_bIsRegistered=true;
    }
    public void Unregister(Context c) {
        if(m_bIsRegistered) {
            c.unregisterReceiver(this);
        }
        m_bIsRegistered=false;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        if(m_DataServiceListeners !=null) {
            DataServicesEventType dataServicesEventType = DataServicesEventType.Invalid;
            String eventTypeKey = dataServicesEventType.getClass().getName();
            if(intent.hasExtra(eventTypeKey)) {
                dataServicesEventType = DataServicesEventType.valueOf(intent.getStringExtra(eventTypeKey));
                for (IDataServiceListener listener : m_DataServiceListeners) {
                    listener.onDataServiceResult(dataServicesEventType,intent);
                }
            }
        }
    }
}


