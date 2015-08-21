package com.app.tris.loca;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LocaService extends Service implements Defines, Runnable,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,   //for network and gps
        com.google.android.gms.location.LocationListener    //for google api
{

    Context context;
    public LocaService() {
//        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread thread = new Thread (this);  //TODO: move to onCreate()
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Loca.location = getLocation();  //can't call it in onCreate() because the Service's context hasn't been available yet
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Implement Runnable
    @Override
    public void run()
    {
        //move from onCreate
        if (checkPlayServices()) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
            createLocationRequest();
        }
    }


    // Implement LocationListener
    @Override
    public void onLocationChanged(Location location) {

        Loca.log("....... LocaService onLocationChanged()");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        displayLocation();
        Loca.location = mLastLocation;
        //if (true) return;

        if (Loca.ringtone == null) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Loca.ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
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



    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    public LocationManager locationManager = null;

    //only call once
    public Location getLocation() {
        Loca.log("getLocation()");

        Location location = null;

        if (locationManager == null)
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Loca.log("locationManager object = " + locationManager);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled && useNetwork) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_UPDATE_TIME_MILLISECONDS, MIN_UPDATE_DISTANCE_METRES, this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location == null && isGPSEnabled && useGPS) {
            Loca.log("GPS enabled");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME_MILLISECONDS, MIN_UPDATE_DISTANCE_METRES, this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return location;
    }

    protected void startLocationUpdates() {
        //TODO: "this" should be a LocationListener
        Loca.log("...startLocationUpdates");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    protected void stopLocationUpdates() {
        Loca.log("...stopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Method to verify google play services on the device
     * */
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private boolean checkPlayServices() {
        Loca.log("checkPlayServices()");
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        Loca.log("resultCode =" + resultCode);
        if (resultCode != ConnectionResult.SUCCESS) {
            Loca.log("ConnectionResult.SUCCESS");
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Loca.log("This device is not supported.");
            }
            return false;
        }
        return true;
    }




    /**
     * Creating location request object
     * */
    private LocationRequest mLocationRequest = null;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    /**
     * Creating google api client object
     * */
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    protected synchronized void buildGoogleApiClient() {
        Loca.log("buildGoogleApiClient()");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        Loca.log("mGoogleApiClient = " + mGoogleApiClient);
    }


    /**
     * Method to display the location on UI
     * */
    private Location mLastLocation;
    public void displayLocation() {

//        Loca.log("displayLocation()");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
//            Loca.toast("From Google: " + latitude + ", " + longitude);
//            Loca.log("From Google: " + latitude + ", " + longitude);

        } else {
            Loca.toast("(Couldn't get the location. Make sure location is enabled on the device)");
            Loca.log("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }


    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Loca.log("Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        Loca.log(("onConnected"));
        startLocationUpdates();
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Loca.log(("onConnectionSuspended"));
        mGoogleApiClient.connect();
    }
}
