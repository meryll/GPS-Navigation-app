package tech.hypermiles.hypermiles.Rest.Model;

/**
 * Created by Asia on 2017-02-14.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class RoutePoint implements Serializable {

    @SerializedName("route")
    @Expose
    private Route route;

    @SerializedName("id")
    @Expose
    private UUID id;

    @SerializedName("routeID")
    @Expose
    private UUID routeId;

    @SerializedName("location")
    @Expose
    private SerializableLatLng location;

    @SerializedName("speed")
    @Expose
    private Integer speed;

    @SerializedName("legNumber")
    @Expose
    private Integer legNumber;

    @SerializedName("stepInLegNumber")
    @Expose
    private Integer stepInLegNumber;

    @SerializedName("pointInStepNumber")
    @Expose
    private Integer pointInStepNumber;

    @SerializedName("endManueverType")
    @Expose
    private Integer endManueverType;

    @SerializedName("speedLimit")
    @Expose
    private Integer speedLimit;

    @SerializedName("inclination")
    @Expose
    private Integer inclination;

    @SerializedName("shouldBeBraking")
    @Expose
    private Boolean shouldBeBraking;

    @SerializedName("wasBraking")
    @Expose
    private Boolean wasBraking;

    @SerializedName("obdData")
    @Expose
    private Object obdData;

    @SerializedName("visitedAt")
    @Expose
    private Date visitedAt;

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRouteId() {
        return routeId;
    }

    public RoutePoint()
    {
        visitedAt = new Date();
    }

    public void setRouteId(UUID routeId) {
        this.routeId = routeId;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(SerializableLatLng location) {
        this.location = location;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getLegNumber() {
        return legNumber;
    }

    public void setLegNumber(Integer legNumber) {
        this.legNumber = legNumber;
    }

    public Integer getStepInLegNumber() {
        return stepInLegNumber;
    }

    public void setStepInLegNumber(Integer stepInLegNumber) {
        this.stepInLegNumber = stepInLegNumber;
    }

    public Integer getPointInStepNumber() {
        return pointInStepNumber;
    }

    public void setPointInStepNumber(Integer pointInStepNumber) {
        this.pointInStepNumber = pointInStepNumber;
    }

    public Integer getEndManueverType() {
        return endManueverType;
    }

    public void setEndManueverType(Integer endManueverType) {
        this.endManueverType = endManueverType;
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }

    public Integer getInclination() {
        return inclination;
    }

    public void setInclination(Integer inclination) {
        this.inclination = inclination;
    }

    public Boolean getShouldBeBraking() {
        return shouldBeBraking;
    }

    public void setShouldBeBraking(Boolean shouldBeBraking) {
        this.shouldBeBraking = shouldBeBraking;
    }

    public Boolean getWasBraking() {
        return wasBraking;
    }

    public void setWasBraking(Boolean wasBraking) {
        this.wasBraking = wasBraking;
    }

    public Object getObdData() {
        return obdData;
    }

    public void setObdData(Object obdData) {
        this.obdData = obdData;
    }

    public Date getVisitedAt() {
        return visitedAt;
    }

    public void setVisitedAt(Date visitedAt) {
        this.visitedAt = visitedAt;
    }
}