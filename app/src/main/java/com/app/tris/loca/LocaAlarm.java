package com.app.tris.loca;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;

/**
 * An object that realise an alarm
 */

public class LocaAlarm implements Serializable{

    public String alarmTitle = "";
    public double alarmLatitude = 0;
    public double alarmLongitude = 0;
    public boolean alarmIsActive = true;

    public LocaAlarm(String title, Location loc) {
        alarmTitle = title;
        alarmLatitude = loc.getLatitude();
        alarmLongitude = loc.getLongitude();
    }

    public LocaAlarm(String title, LatLng latlng) {
        alarmTitle = title;
        alarmLatitude = latlng.latitude;
        alarmLongitude = latlng.longitude;
    }
    public LocaAlarm(String title, double lat, double lon) {
        alarmTitle = title;
        alarmLatitude = lat;
        alarmLongitude = lon;
    }

    @Override
    public String toString() {
        return "" + alarmTitle + ": " + alarmLatitude + ", " + alarmLongitude;
    }

    public boolean isEqual(LocaAlarm a) {
        return false;
    }
}