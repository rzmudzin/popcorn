package com.phoenixroberts.popcorn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.phoenixroberts.popcorn.dialogs.StatusDialog;

public class MainActivity extends AppCompatActivity {
    StatusDialog m_StatusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbarInstance = (Toolbar)findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarInstance);
        m_StatusDialog = new StatusDialog(new StatusDialog.ShowStatusRequest(this,true, "Loading",
                StatusDialog.MaskType.Black, true));
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
            default:
                break;
        }
        m_StatusDialog.showDialog();
        return super.onOptionsItemSelected(item);
    }
}
