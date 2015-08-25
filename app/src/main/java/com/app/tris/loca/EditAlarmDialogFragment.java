package com.app.tris.loca;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class EditAlarmDialogFragment extends DialogFragment implements Defines {

    LocaAlarm item = null; //to save the original, unmodified item object
    EditText editTextLocation = null;
    LatLng latlng = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
            item = (LocaAlarm)args.getSerializable(ITEM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View dialogView = inflater.inflate(R.layout.editalarmdialogfragment, container, false);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        editTextLocation = (EditText) dialogView.findViewById(R.id.editTextLocation);
        if (item == null) { //create new item, all empty field
            Loca.log("item = null, create new");
            this.getDialog().setTitle(R.string.create_alarm);
        }
        else {  //editing existing item
            this.getDialog().setTitle(R.string.edit_alarm);
            editTextName.setText(item.alarmTitle);
            latlng = new LatLng(item.alarmLatitude, item.alarmLongitude);
            editTextLocation.setText(Loca.formatLoc(latlng));
        }


        editTextLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMapActivity = new Intent(getActivity().getApplicationContext(), MapActivity.class);
                if (item != null)
                    intentMapActivity.putExtra(LATLNG, latlng);
                startActivityForResult(intentMapActivity, MapActivity.REQUEST_CODE);
            }
        });


        //buttons
        Button buttonSave = (Button) dialogView.findViewById((R.id.buttonSave));
        Button buttonCancel = (Button) dialogView.findViewById((R.id.buttonCancel));
        Button buttonDelete = (Button) dialogView.findViewById((R.id.buttonDelete));
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null) {
                    Loca.log("update existing");
                    //get the current position of the alarm in the array list
                    int index = Loca.listAlarms.indexOf(item);

                    //update item using data in EditTexts update before dismiss
                    item.alarmTitle = editTextName.getText().toString();
                    item.alarmLatitude = latlng.latitude;
                    item.alarmLongitude = latlng.longitude;

                    //save back to the original position
                    Loca.listAlarms.set(index, item);
                }
                else {  //add new
                    Loca.log("add new");
                    //create item: only create before dismiss
                    item = new LocaAlarm(editTextName.getText().toString(), latlng);
                    Loca.listAlarms.add(item);
                }
                Loca.itemsAdapter.notifyDataSetChanged();
                dismiss();

            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null) {
                    Loca.listAlarms.remove(item);
                    Loca.itemsAdapter.notifyDataSetChanged();
                }
                dismiss();
            }
        });
        return dialogView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MapActivity.REQUEST_CODE) {
            if (resultCode == MapActivity.RESULT_OK) {
                latlng = (LatLng) data.getExtras().get(LATLNG);
                if (editTextLocation != null)
                    editTextLocation.setText(Loca.formatLoc(latlng));
            }
        }
    }
}