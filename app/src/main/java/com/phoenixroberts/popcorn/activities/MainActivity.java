package com.phoenixroberts.popcorn.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.phoenixroberts.popcorn.AppSettings;
import com.phoenixroberts.popcorn.R;
import com.phoenixroberts.popcorn.data.DTO;
import com.phoenixroberts.popcorn.data.DataService;
import com.phoenixroberts.popcorn.data.DataServiceBroadcastReceiver;
import com.phoenixroberts.popcorn.dialogs.DialogService;
import com.phoenixroberts.popcorn.dialogs.Dialogs;
import com.phoenixroberts.popcorn.dialogs.StatusDialog;
import com.phoenixroberts.popcorn.fragments.MovieGridFragment;
import com.phoenixroberts.popcorn.threading.IDataServiceListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements IDataServiceListener {
    private Menu m_Menu;
    private StatusDialog m_StatusDialog;

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
                FragmentManager fragmentManager = getSupportFragmentManager();
                if(fragmentManager!=null) {
                    ActionBar actionBar = getSupportActionBar();
                    if(actionBar!=null) {
                        if (fragmentManager.getBackStackEntryCount() > 0) {
                            actionBar.setDisplayHomeAsUpEnabled(true);
                        } else {
                            actionBar.setDisplayHomeAsUpEnabled(false);
                        }
                    }
                }
            }
        });
        if(savedInstanceState==null) {
            String apiKey = DataService.getInstance().getAPIKey();
            if(TextUtils.isEmpty(apiKey)) {
                promptUserForAPIKey();
            }
            else {
                LoadData();
            }
        }
    }

    public void promptUserForAPIKey() {
        DialogService.getInstance().DisplayTextInputDialog(new Dialogs.TextInputDialogData(this,
                "API Key Entry",
                "Ok",
                "Cancel",
                "Please enter your Movie DB API Key\n(Version 3 format).",
                (eventArgs) -> {
                    //On ok event handler
                    Dialogs.IDialogTextChangedEventData textInputEventArgs = (Dialogs.IDialogTextChangedEventData)eventArgs;
                    //                            Toast.makeText(getActivity(),textInputEventArgs.getText(), Toast.LENGTH_SHORT).show();
                    AppSettings.set(AppSettings.Settings.APKI_Key, textInputEventArgs.getText());
                    DataService.getInstance().setAPIKey(textInputEventArgs.getText());
                    LoadData();
                },
                null,       //On cancel
                null));     //On text changed
    }

    public void LoadData() {
        m_StatusDialog = new StatusDialog(new StatusDialog.ShowStatusRequest(this, true, "Loading",
                StatusDialog.MaskType.Black, true));
        m_StatusDialog.showDialog();
        DataService.getInstance().fetchMoviesData();
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
    }
}
