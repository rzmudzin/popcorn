package com.phoenixroberts.popcorn;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.phoenixroberts.popcorn.data.DTO;
import com.phoenixroberts.popcorn.data.DataService;
import com.phoenixroberts.popcorn.dialogs.StatusDialog;
import com.phoenixroberts.popcorn.fragments.MovieGridFragment;
import com.phoenixroberts.popcorn.threading.IDataServiceListener;

public class MainActivity extends AppCompatActivity implements IDataServiceListener {
    StatusDialog m_StatusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataServiceBroadcastReceiver.getInstance().addListener(this);
        Toolbar toolbarInstance = (Toolbar)findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarInstance);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                new MovieGridFragment()).commit();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });
        if(savedInstanceState==null) {
            m_StatusDialog = new StatusDialog(new StatusDialog.ShowStatusRequest(this, true, "Loading",
                    StatusDialog.MaskType.Black, true));
            m_StatusDialog.showDialog();
            DataService.getInstance().fetchMoviesData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataServiceBroadcastReceiver.getInstance().removeListener(this);
    }

    @Override
    public void onDataServiceResult(DataServiceBroadcastReceiver.DataServicesEventType dataServicesEventType, Intent i) {
        //If the status dialog is displayed close it
        m_StatusDialog.dismissDialog();
        DTO.MoviesListItem movieData = DataService.getInstance().getMovieData(211672);
        if(movieData!=null) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.onBackPressed();
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
