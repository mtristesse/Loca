package com.app.tris.loca;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Tris on 25/08/2015.
 * Encapsulates all settings
 */
public class Settings {

    //unsave settings
    public static boolean IsDebugMode = true;

    private static final int CHECK_VALUE = 1233;


    //saved settings
    public static int checkValue = CHECK_VALUE;
    public static boolean IsFullVersion = false;
    public static int currentRadius = 1000;

    public static boolean useNetwork = true;
    public static boolean useGPS = true;

    public static void reset() {
        checkValue = CHECK_VALUE;
        IsFullVersion = false;
        int currentRadius = 1000;
        useNetwork = true;
        useGPS = true;
        save();
    }


    public static void save() {
        try {
            Loca.log("Settings.save()");

            FileOutputStream fos = Loca.context.openFileOutput(Loca.saveSettingsFileName, Context.MODE_PRIVATE);    //no file dir is needed when writing?
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeInt(checkValue);
            out.writeBoolean(IsFullVersion);
            out.writeInt(currentRadius);
            out.writeBoolean(useNetwork);
            out.writeBoolean(useGPS);
            out.close();
        } catch (Exception e) {
            Loca.log("Error saving settings: " + e);
        }
    }

    public static void load() {
        Loca.log("Settings.load()");
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(Loca.context.getFilesDir() + "/" + Loca.saveSettingsFileName);
            in = new ObjectInputStream(fis);

            checkValue = in.readInt();
            if (checkValue == CHECK_VALUE) {
                IsFullVersion = in.readBoolean();
                currentRadius = in.readInt();
                useNetwork = in.readBoolean();
                useGPS = in.readBoolean();
                Loca.log("bFullVersion = " + IsFullVersion);
                Loca.log("currentRadius = " + currentRadius);
                Loca.log("useNetwork = " + useNetwork);
                Loca.log("useGPS = " + useGPS);
                in.close();

            } else {
                in.close();
                reset();
            }


        } catch (Exception e) {
            Loca.log("Error loading settings: " + e.toString());
        }

    }

}
