package com.app.tris.loca;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Ringtone;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * App-wide values and utilities
 */

public class Loca implements Defines {

    public static Location location;                //a reference to the current location

    public static TextView textViewStatus = null;   //a reference to the status text
    public static Ringtone ringtone = null;

    public static Context context = null;
    public static void init(Context c) {
        context = c;
        listener = new LocaListener(context);
    }

    public static Intent intentLocaService = null;      //a reference to the service
    public static AlarmListAdapter itemsAdapter = null; //a reference to the adapter
    public static ArrayList<LocaAlarm> listAlarms = new ArrayList<>();


    public static GoogleApiClient mGoogleApiClient;
    public static LocaListener listener = null;

    public static void log(String s) {
        Log.i(TAG, s);
    }

    public static void toast(String s) {
        if (context != null)
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void status(String s) {
        if (textViewStatus != null)
            textViewStatus.setText(s);
    }

    public static void notify(String s) {
        if (context != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("My notification")
                    .setContentText(s);

            Intent resultIntent = new Intent(context, LocaActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(LocaActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            int mNotificationId = 001;
            notificationManager.notify(mNotificationId, mBuilder.build());
        }
    }

    public static String doubleToDegree(double l) {
        double v = Math.abs(l);
        int degrees = (int) v;
        v = v % 1 * 60;
        int minutes = (int) v;
        v = v % 1 * 60;
        int seconds = (int) v;
        return "" + degrees + "°" + minutes + "′" + seconds + "″";
    }
    public static String formatLoc(double lat, double lng) {
//        return format.format(lat) + ", " + format.format(lng);
        return "" + doubleToDegree(lat) + (lat > 0 ? "N" : "S") + " "
                + doubleToDegree(lng) + (lng > 0 ? "E" : "W");
    }

    public static String formatLoc(LatLng latlng) {
        return formatLoc(latlng.latitude, latlng.longitude);
    }



    //reset to the original array; used for debugging
    public static void reset() {
        listAlarms.clear();
        for (int i = 0; i < alarms.length; i++) {
            listAlarms.add(alarms[i]);
        }
        Loca.itemsAdapter.notifyDataSetChanged();
    }

    //writing the array into the file data
    public static void save() {
        log("Saving...");
        try {
            FileOutputStream fos = context.openFileOutput(saveFileName, Context.MODE_PRIVATE);    //no file dir is needed when writing?
            ObjectOutputStream out = new ObjectOutputStream(fos);

            out.writeObject(listAlarms);
            out.close();
            log("Save successfully");
        } catch (Exception e) {
            log("Save exception: " + e.toString());
        }
    }

    //reading alarms from file
    public static void load() {
        log("Loading...");
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(context.getFilesDir() + "/" + saveFileName);
            in = new ObjectInputStream(fis);
            listAlarms = (ArrayList<LocaAlarm>) in.readObject();
            in.close();
            log("Load successfully");
        } catch (Exception e) {
            log("Read exception: " + e.toString());
        }
    }



    public static double getDistance(double lat1, double long1, double lat2, double long2){  // generally used geo measurement function
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLong = (long2 - long1) * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLong/2) * Math.sin(dLong/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS * c;    // in metre
    }

    public static double calculateDistance(double lat1, double long1, double lat2, double long2) {
        double dLat = Math.toRadians(lat2-lat1);
        double dLong = Math.toRadians(long2-long1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLong/2) * Math.sin(dLong/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS * c;
    }
}
