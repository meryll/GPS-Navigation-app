package tech.hypermiles.hypermiles.Rest.Utils;

import java.util.List;

import tech.hypermiles.hypermiles.Rest.Model.SerializableLatLng;

public class RoutePOCO
{
    public String DriverToCarID;
    public List<SerializableLatLng> Waypoints;

    public RoutePOCO(String driverToCarId, List<SerializableLatLng> waypoints)
    {
        this.DriverToCarID = driverToCarId;
        this.Waypoints = waypoints;
    }

}