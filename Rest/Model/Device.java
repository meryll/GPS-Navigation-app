package tech.hypermiles.hypermiles.Rest.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by Asia on 2017-02-14.
 */

public class Device {

    @SerializedName("id")
    @Expose
    private UUID id;
    @SerializedName("hardwareSerialID")
    @Expose
    private String hardwareSerialId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getHardwareSerialId() {
        return hardwareSerialId;
    }

    public void setHardwareSerialId(String hardwareSerialId) {
        this.hardwareSerialId = hardwareSerialId;
    }

}