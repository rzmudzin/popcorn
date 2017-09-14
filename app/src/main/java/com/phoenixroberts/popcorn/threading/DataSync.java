package com.phoenixroberts.popcorn.threading;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by robz on 9/6/17.
 */

public class DataSync {

    public interface IDataSyncAction {
        void execute();                         //Invoked by doInBackground
        void cancel();                          //Supports canceling
    }
    public interface IDataSyncListener {
        void onDataSyncComplete();                  //Callback for when completed
        void onProgressUpdate(Integer amount);      //Callback for progress
    }

    static public class DataSyncTask extends AsyncTask<String, Integer, Void> {
        private final Object m_TaskLock = new Object();
        private boolean m_TaskIsCompleted = false;
        private IDataSyncListener m_OnCompletedListener;
        private IDataSyncAction m_DataSyncAction;
        private String m_TaskName;

        public DataSyncTask(String taskName, IDataSyncAction dataSyncAction) {
            m_TaskName=taskName;
            m_DataSyncAction=dataSyncAction;
        }
        public boolean isCompleted() {
            synchronized (m_TaskLock) {
                return m_TaskIsCompleted;
            }
        }
        public boolean registerOnCompletedListener(IDataSyncListener onCompletedListener) {
            synchronized (m_TaskLock) {
                if(m_TaskIsCompleted ==false) {
                    m_OnCompletedListener=onCompletedListener;
                }
                return m_TaskIsCompleted;
            }
        }
        public void removeOnCompletedListener() {
            synchronized (m_TaskLock) {
                m_OnCompletedListener=null;
            }
        }
        public IDataSyncAction getDataSyncAction() {
            return m_DataSyncAction;
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.d("TASK", "Running background task for " + m_TaskName);
            m_DataSyncAction.execute();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            synchronized (m_TaskLock) {
                Log.d("TASK", "Post execute processing for " + m_TaskName);
                m_TaskIsCompleted =true;
                if(m_OnCompletedListener!=null) {
                    Log.d("TASK", "Invoking onDataSyncComplete for " + m_TaskName);
                    m_OnCompletedListener.onDataSyncComplete();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            synchronized (m_TaskLock) {
                if(m_OnCompletedListener!=null) {
                    if(values!=null && values.length>0) {
                        m_OnCompletedListener.onProgressUpdate(values[0]);
                    }
                }
            }
        }
    }

}

