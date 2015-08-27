package com.app.tris.loca;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


public class SettingsDialogFragment extends DialogFragment implements Defines {

    TextView textViewAlarmRadius = null;
    SeekBar seekBarAlarmRadius = null;

    public static int[] radiusArray = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.settingdialogfragment, container, false);
        this.getDialog().setTitle(R.string.action_settings);

        textViewAlarmRadius = (TextView)dialogView.findViewById(R.id.textViewAlarmRadius);
        seekBarAlarmRadius = (SeekBar) dialogView.findViewById(R.id.seekBarAlarmRadius);


        radiusArray = getResources().getIntArray(R.array.alarm_radius_array);
        Loca.log("radiusArray.length = " + radiusArray.length);

        Settings.load();

        int maxProgress = radiusArray.length - 1;
        if (Settings.currentRadius > radiusArray[maxProgress]) {
            seekBarAlarmRadius.setProgress(maxProgress);
            Settings.currentRadius = radiusArray[maxProgress];
        }
        else {
            for (int i = 0; i < radiusArray.length; i++) {
                if (radiusArray[i] >= Settings.currentRadius) {
                    seekBarAlarmRadius.setProgress(i);
                    Settings.currentRadius = radiusArray[i];
                    break;
                }
            }
        }

        textViewAlarmRadius.setText(getString(R.string.alarm_radius) + " " + Settings.currentRadius + " m");

        seekBarAlarmRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                updateProgress(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        return dialogView;
    }

    void updateProgress(int progress) {
        Loca.log("updateProgress: " + progress);
        if (progress < radiusArray.length) {
            Settings.currentRadius = radiusArray[progress];
            textViewAlarmRadius.setText(getString(R.string.alarm_radius) + " " + Settings.currentRadius + " m");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Loca.log("onDismiss....");
        Settings.save();

    }

}
