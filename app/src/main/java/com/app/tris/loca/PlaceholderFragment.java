package com.app.tris.loca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class PlaceholderFragment extends Fragment implements Defines {

    public PlaceholderFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = (ListView)getView().findViewById(R.id.listView);
        if (listView != null)
        {
            Loca.load();
            Loca.itemsAdapter = new AlarmListAdapter(getActivity(), R.layout.item, Loca.listAlarms);
            listView.setAdapter(Loca.itemsAdapter);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Loca.log("... onItemLongClick; position = " + position);
                    return false;
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LocaAlarm item = (LocaAlarm)parent.getItemAtPosition(position);
                    //LocaAlarm item = Loca.itemsAdapter.getItem(position);
                    Loca.log("item " + item.alarmTitle + " is clicked.");
                    EditAlarmDialogFragment dialogFragment = new EditAlarmDialogFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    Bundle args = new Bundle();
                    args.putSerializable(ITEM, item);
                    dialogFragment.setArguments(args);

                    dialogFragment.show(fm.beginTransaction(), "Dialog Fragment");
                }
            });
        }

        Button buttonSettings = (Button)getView().findViewById(R.id.buttonSettings);
        Button buttonAdd = (Button)getView().findViewById(R.id.buttonAdd);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loca.log("buttonSettings is clicked");
                SettingsDialogFragment settingsdialogFragment = new SettingsDialogFragment();
                settingsdialogFragment.show(getActivity().getSupportFragmentManager().beginTransaction(), "Dialog Fragment");
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loca.log("buttonAdd is clicked");
                EditAlarmDialogFragment dialogFragment = new EditAlarmDialogFragment();
                dialogFragment.show(getActivity().getSupportFragmentManager().beginTransaction(), "Dialog Fragment");

            }
        });

        Loca.textViewStatus = (TextView)getView().findViewById(R.id.textViewStatus);
    }
}