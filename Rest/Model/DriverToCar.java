package tech.hypermiles.hypermiles.Rest.Model;

import java.util.UUID;

/**
 * Created by Asia on 2017-02-14.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverToCar {

    @SerializedName("driver")
    @Expose
    private Driver driver;

    @SerializedName("car")
    @Expose
    private Car car;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("driverID")
    @Expose
    private String driverId;

    @SerializedName("carID")
    @Expose
    private String carId;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getCarId() {
        return car.getId();
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String toString()
    {
        return car.toString();
    }
}
