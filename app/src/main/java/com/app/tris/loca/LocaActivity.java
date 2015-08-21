package com.app.tris.loca;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class LocaActivity extends ActionBarActivity implements Defines {

    PlaceholderFragment fragment = new PlaceholderFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Loca.init(this);    //init app-wide values
        setContentView(R.layout.layout_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        if (Loca.intentLocaService == null) {
            Loca.log("start service...");
            Loca.intentLocaService = new Intent(this, LocaService.class);
            Loca.intentLocaService.putExtra(ALARMLIST, Loca.listAlarms);
            startService(Loca.intentLocaService);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkPlayServices();
//        displayLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                SettingsDialogFragment settingsdialogFragment = new SettingsDialogFragment();
                settingsdialogFragment.show(getSupportFragmentManager().beginTransaction(), "Dialog Fragment");
                return true;
            case R.id.action_about:
                Loca.toast(getString(R.string.about));
                return true;
            case R.id.action_quit:
                finish();
                System.exit(0);
                return true;
            case R.id.action_debug_add:    //use to add
                EditAlarmDialogFragment dialogFragment = new EditAlarmDialogFragment();
                dialogFragment.show(getSupportFragmentManager().beginTransaction(), "Dialog Fragment");
                return true;

            case R.id.action_debug_reset:
                Loca.log("Debug reset");
                Loca.reset();
                return true;

            case R.id.action_service:
                //just to test, not necessary, since Loca.listAlarms can be accessed directly (sync)
                if (Loca.intentLocaService == null) {
                    Loca.log("set intentLocaService...");
                    Loca.intentLocaService = new Intent(this, LocaService.class);
                    Loca.intentLocaService.putExtra("ListAlarms", Loca.listAlarms);
                }

                if (item.getTitle().equals("Start service")) {
                    Loca.log("Start service...");
                    item.setTitle("Stop service");
                    startService(Loca.intentLocaService);
                }
                else {
                    Loca.log("Stop service...");
                    item.setTitle("Start service");
                    stopService(Loca.intentLocaService);

                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
