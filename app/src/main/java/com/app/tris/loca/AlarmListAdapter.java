package com.app.tris.loca;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class AlarmListAdapter extends ArrayAdapter<LocaAlarm> implements Defines {

    Context context = null;
    int resource = R.layout.item;

    public AlarmListAdapter(Context context, int resource, ArrayList<LocaAlarm> values) {
        super(context, resource, values);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resource, parent, false);

        LocaAlarm item = getItem(position);
        rowView.setTag(item);

        TextView textViewTitle = (TextView) rowView.findViewById(R.id.textViewTitle);
        TextView textViewLocation = (TextView) rowView.findViewById(R.id.textViewLocation);
        textViewTitle.setText(item.alarmTitle);
        textViewLocation.setText(Loca.formatLoc(item.alarmLatitude, item.alarmLongitude));

        CheckBox checkBoxIsActive = (CheckBox) rowView.findViewById(R.id.checkBoxIsActive);
        if (checkBoxIsActive != null)
            checkBoxIsActive.setChecked(item.alarmIsActive);

        checkBoxIsActive.setTag(item);
        checkBoxIsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocaAlarm item = (LocaAlarm) buttonView.getTag();
                Loca.log("item = " + item);
                if (item != null) {
                    int index = Loca.listAlarms.indexOf(item);
                    item.alarmIsActive = isChecked;
                    Loca.listAlarms.set(index, item);
                    Loca.itemsAdapter.notifyDataSetChanged();
                }
            }
        });


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaAlarm item = (LocaAlarm)v.getTag();

                EditAlarmDialogFragment editAlarmDialogFragment = new EditAlarmDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable(ITEM, item);
                editAlarmDialogFragment.setArguments(args);

                LocaActivity activity = (LocaActivity) context;
                editAlarmDialogFragment.show(activity.getSupportFragmentManager().beginTransaction(), "Dialog Fragment");
            }
        });
        return rowView;
    }

    @Override
    public void notifyDataSetChanged () {
        super.notifyDataSetChanged();
        Loca.save();

        //TODO: call service LocationListener's onLocationChanged
        Loca.listener.onLocationChanged(Loca.location);

    }
}