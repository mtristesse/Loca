package com.app.tris.loca;


import android.content.Context;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Tris on 21/08/2015.
 */
public class LocaListener implements Defines,
        android.location.LocationListener,   //for network and gps
        com.google.android.gms.location.LocationListener    //for google api
{

    private Context context;
    public LocaListener(Context c) {
        super();
        context = c;
    }

    @Override
    public void onLocationChanged(Location location) {
        Loca.log("....... LocaListener onLocationChanged()");

        Loca.location = LocationServices.FusedLocationApi.getLastLocation(Loca.mGoogleApiClient);

        //TODO: create a separate class for ringtone
        if (Loca.ringtone == null) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Loca.ringtone = RingtoneManager.getRingtone(context, notification);
        }

        Loca.location = location;
        if (location != null) {
//            Loca.log("onLocationChange(), location = " + location.getLatitude() + ", " + location.getLongitude());
            Iterator<LocaAlarm> ite = Loca.listAlarms.iterator();
            boolean isAlarmed = false;
            String title = "";
            double d = 0;

            //to store results
            Map<Double, LocaAlarm> results = new HashMap<Double, LocaAlarm>();

            while (ite.hasNext()) {
                LocaAlarm alarm = ite.next();
                if (!alarm.alarmIsActive)
                    continue;
                d = Loca.getDistance(location.getLatitude(), location.getLongitude(), alarm.alarmLatitude, alarm.alarmLongitude);

                if (d < Loca.currentRadius) {
                    results.put(d, alarm);
                    isAlarmed = true;
                    title = alarm.alarmTitle;
                }
            }   //end while

            if (isAlarmed) {
                String status = "You are close to:";
                for (LocaAlarm item : results.values()) {
                    status += " " + item.alarmTitle;
                }
                Loca.status(status);

                if (Loca.ringtone != null && !Loca.ringtone.isPlaying()) {
                    Loca.ringtone.play();
                }

            } else {
                Loca.toast("... ... ... ...");
                Loca.status("There is no destination nearby");
                if (Loca.ringtone.isPlaying())
                    Loca.ringtone.stop();
            }
        }


    }


    // Implement android.location.LocationListener

    @Override
    public void onProviderDisabled(String provider) {
        Loca.log("onProviderDisabled()");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Loca.log("onProviderEnabled()");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Loca.log("onStatusChanged()");

    }

}
