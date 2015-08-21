package com.app.tris.loca;


import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public interface Defines {

    //constant strings
    public static String saveFileName = "loca.save";
    public static String saveSettingsFileName = "locasettings.save";
    public static final String TAG = "LOCA_LOG";
    public static final String LATLNG = "latlng";
    public static final String ITEM = "item";
    public static final String ALARMLIST = "ListAlarms";

    //flag to turn on/off Network / GPS
    public static final boolean useNetwork = true;
    public static final boolean useGPS = true;

    //constants for calculation
    public static final int EARTH_RADIUS =  63781370;
    public static final int LOCATION_RADIUS_METRES = 100;
    public static final int MIN_UPDATE_TIME_MILLISECONDS = 1000;
    public static final int MIN_UPDATE_DISTANCE_METRES = 10;

    public static final int UPDATE_INTERVAL = 10000; // 10 sec
    public static final int FATEST_INTERVAL = 1000; // 1 sec
    public static final int DISPLACEMENT = 10; // 10 meters

    //Flinders Street Station
    public static final double DEFAULT_LATITUDE = -37.817773;
    public static final double DEFAULT_LONGITUDE = 144.967240;

    //utility to format decimal
    public static DecimalFormat format = new DecimalFormat( "#,###,###,##0.00" );

    public static final LocaAlarm alarms[] = {
            new LocaAlarm("Pier St", -37.865881, 144.830645),
            new LocaAlarm("Altona Station", -37.867252, 144.830287),
            new LocaAlarm("Pier 71 Bar e Cucina", -37.868021, 144.830407),
            new LocaAlarm("Altona Pier", -37.870761, 144.830085),
            new LocaAlarm("Logan Reserve", -37.869790, 144.829559),
            new LocaAlarm("Altona Library", -37.869129, 144.829034),

            new LocaAlarm("Sunshine Station", -37.787851, 144.832531),
            new LocaAlarm("Sunshine Station Bus Interchange", -37.787222, 144.832897),
            new LocaAlarm("Sunshine Station Parking", -37.788502, 144.833603),

            new LocaAlarm("Bunnings Altona", -37.845275, 144.845532),
            new LocaAlarm("Altona Gate", -37.827887, 144.848697),
            new LocaAlarm("Hobsons Bay Fishing Club", -37.864914, 144.846437),
            new LocaAlarm("Apex Park", -37.875171, 144.814054),
/*
            new LocaAlarm("", ),
*/
            new LocaAlarm("Flinders Street Station", -37.817773, 144.967240),
    };
}