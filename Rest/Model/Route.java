package tech.hypermiles.hypermiles.Rest.Model;

/**
 * Created by Asia on 2017-02-14.
 */

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import tech.hypermiles.hypermiles.Other.Settings;

public class Route {

    @SerializedName("driverToCar")
    @Expose
    private DriverToCar driverToCar;

    @SerializedName("id")
    @Expose
    private UUID id;

    @SerializedName("mass")
    @Expose
    private int mass;

    @SerializedName("driverToCarID")
    @Expose
    private UUID driverToCarId;

    public Route(String driverToCarId)
    {
        this.driverToCarId = UUID.fromString(driverToCarId);
        this.mass = (int)Settings.CAR_WEIGHT;
    }

    public DriverToCar getDriverToCar() {
        return driverToCar;
    }

    public void setDriverToCar(DriverToCar driverToCar) {
        this.driverToCar = driverToCar;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDriverToCarId() {
        return driverToCarId;
    }

    public void setDriverToCarId(UUID driverToCarId) {
        this.driverToCarId = driverToCarId;
    }

    public int getMass() {
        return mass;
    }

    public void setMass(int mass) {
        this.mass = mass;
    }

}
