package tech.hypermiles.hypermiles.Rest.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Asia on 2017-03-28.
 */

public class RouteChange {

    @SerializedName("id")
    @Expose
    private UUID id;

    @SerializedName("routeId")
    @Expose
    private UUID routeId;

    @SerializedName("startLocation")
    @Expose
    private LatLng startLocation;
    @SerializedName("endLocation")
    @Expose
    private LatLng endLocation;

    @SerializedName("startDate")
    @Expose
    private Date startDate;

    @SerializedName("endDate")
    @Expose
    private Date endDate;

    @SerializedName("routeData")
    @Expose
    private String routeData;

    public UUID getId()
    {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getRouteId() { return routeId; }
    public void setRouteId(UUID routeId) { this.routeId = routeId; }
    public LatLng getStartLocation() { return startLocation; }
    public void setStartLocation(LatLng startLocation) { this.startLocation = startLocation; }
    public LatLng getEndLocation() { return endLocation; }
    public void setEndLocation(LatLng endLocation) { this.endLocation = endLocation; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getRouteData() { return routeData; }
    public void setRouteData(String routeData) { this.routeData = routeData; }
}
