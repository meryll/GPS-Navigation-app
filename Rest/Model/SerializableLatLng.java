package tech.hypermiles.hypermiles.Rest.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Asia on 2017-03-31.
 */

public class SerializableLatLng implements Serializable {

//    @SerializedName("latitude")
//    @Expose
    public float latitude;
//    @SerializedName("longitude")
//    @Expose
    public float longitude;

    public SerializableLatLng(double lat, double lng) {
        latitude = (float)lat;
        longitude = (float)lng;
    }
}
