package tech.hypermiles.hypermiles.Utils;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

import tech.hypermiles.hypermiles.Gps.GpsServices;
import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Other.Settings;

/**
 * Created by Asia on 2017-05-31.
 */

public class SpeedUtils {

    private static final String TAG = "SpeedUtils";

    public static double getCurrentSpeed(Location location)
    {
        Double newSpeed = 0.0;

        if(!Settings.NAVIGATE) return newSpeed;

        GpsServices gpsServices = new GpsServices(location);
        if(location != null)
        {
            try {
                newSpeed = gpsServices.getSpeedInDouble();

            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
        }
        return newSpeed;
    }

}
