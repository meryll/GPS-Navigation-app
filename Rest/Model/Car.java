package tech.hypermiles.hypermiles.Rest.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by Asia on 2017-02-14.
 */

public class Car {

    @SerializedName("device")
    @Expose
    private Device device;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("deviceID")
    @Expose
    private String deviceId;

    @SerializedName("licencePlateNumber")
    @Expose
    private String licencePlateNumber;

    @SerializedName("carProfileId")
    @Expose
    private String carProfileId;

    @SerializedName("carProfile")
    @Expose
    private CarProfile carProfile;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public CarProfile getCarProfile() {
        return carProfile;
    }

    public void setCarProfile(CarProfile carProfile) {
        this.carProfile = carProfile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceID) {
        this.deviceId = deviceID;
    }

    public String getLicencePlateNumber() {
        return licencePlateNumber;
    }

    public void setLicencePlateNumber(String licencePlateNumber) {
        this.licencePlateNumber = licencePlateNumber;
    }

    public String toString()
    {
        return "Autko "+licencePlateNumber;
    }
}