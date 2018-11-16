package tech.hypermiles.hypermiles.Utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import org.osmdroid.util.GeoPoint;

import tech.hypermiles.hypermiles.Rest.Model.SerializableLatLng;

/**
 * Created by Asia on 2016-11-28.
 */

public class LocationUtils {

    //in km
    final static private double earhRadiusInKilometers = 6371;
    final static private double earhRadiusInMeters = earhRadiusInKilometers*1000;


    public static Boolean areEqual(GeoPoint location1, GeoPoint location2) {
        return location1.getLongitude()==location2.getLongitude() && location1.getLatitude()==location1.getLatitude();
    }

    public static Boolean areEqual(Location location1, Location location2) {
        if(location1==null || location2 == null) return false;
        return location1.getLongitude()==location2.getLongitude() && location1.getLatitude()==location1.getLatitude();
    }

    public static Boolean areEqual(GeoPoint location1, Location location2) {
        if(location1==null || location2 == null) return false;
        GeoPoint geoPoint = new GeoPoint(location2.getLatitude(), location2.getLongitude());

        return areEqual(location1, geoPoint);
    }

    public static LatLng castToLatLng(GeoPoint location)
    {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static SerializableLatLng castSerializableMyLatLng(GeoPoint location)
    {
        return new SerializableLatLng(location.getLatitude(), location.getLongitude());
    }

    public static GeoPoint castToGeoPoint(Location location)
    {
        return new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    public static GeoPoint castToGeoPoint(SerializableLatLng location)
    {
        return new GeoPoint(location.latitude, location.longitude);
    }

    public static GeoPoint castToGeoPoint(LatLng location)
    {
        return new GeoPoint(location.latitude, location.longitude);
    }


    public static double getDistanceInMeters(GeoPoint start, GeoPoint end) {
        double distanceLong = Math.toRadians(end.getLongitude() - start.getLongitude());
        double distanceLat = Math.toRadians(end.getLatitude() - start.getLatitude());

        double latStart = Math.toRadians(start.getLatitude());
        double latEnd = Math.toRadians(end.getLatitude());

        double a = Math.pow(Math.sin(distanceLat / 2), 2) + (Math.cos(latStart) * Math.cos(latEnd) * Math.pow(Math.sin(distanceLong / 2), 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = earhRadiusInMeters * c;

        return d;
    }

    public static double getBearingInDegrees(GeoPoint start, GeoPoint end) {
        double distanceLong = Math.toRadians(end.getLongitude() - start.getLongitude());

        double latStart = Math.toRadians(start.getLatitude());
        double latEnd = Math.toRadians(end.getLatitude());

        double y = Math.sin(distanceLong) * Math.cos(latEnd);
        double x = (Math.cos(latStart) * Math.sin(latEnd)) - (Math.sin(latStart) * Math.cos(latEnd) * Math.cos(distanceLong));
        double brng = Math.atan2(y, x);
        double inDegres = Math.toDegrees(brng) + 360; //bo atan2 zwraca z przedzialu 180, 180
        return inDegres%360;
        //todo modulo 360
    }

    public static GeoPoint getMidPoint(GeoPoint start, GeoPoint end) {

        double dLon = Math.toRadians(end.getLongitude() - start.getLongitude());

        double lat1 = Math.toRadians(start.getLatitude());
        double lat2 = Math.toRadians(end.getLatitude());
        double lon1 = Math.toRadians(start.getLongitude());

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        double lat3deg = Math.toDegrees(lat3);
        double lon3deg = (Math.toDegrees(lon3) + 540) % 360 - 180;

        return new GeoPoint(lat3deg, lon3deg);
    }

    public static LatLng getNextPointByDistance(GeoPoint start, GeoPoint end, double distance) {
        double bearing = Math.toRadians(getBearingInDegrees(start, end));
        double dOverR = distance / earhRadiusInKilometers;

        double lat1 = start.getLatitude();
        double lon1 = start.getLongitude();

        double lat2 = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(lat1)) * Math.cos(dOverR) + Math.cos(Math.toRadians(lat1)) * Math.sin(dOverR) * Math.cos(bearing)));
        double lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(dOverR) * Math.cos(lat1),Math.cos(dOverR) - Math.sin(lat1) * Math.sin(lat2));

        return new LatLng(lat2, lon2);

    }
}
