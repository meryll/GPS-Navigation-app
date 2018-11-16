package tech.hypermiles.hypermiles.Rest.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Asia on 2017-04-06.
 */


public class CarProfile {

    @SerializedName("decelerationProfile")
    @Expose
    private Entity[] decelerationProfile;

    public Entity[] getDecelerationProfile()
    {
        return decelerationProfile;
    }


    public class Entity
    {
        @SerializedName("vstart")
        @Expose
        private double vstart;

        @SerializedName("vdiff")
        @Expose
        private double vdiff;

        @SerializedName("distance")
        @Expose
        private double distance;

        public double getVstart() { return vstart;}
        public void setVstart(double vstart) { this.vstart = vstart; }

        public double getVdiff() { return vdiff;}
        public void setVdiff(double vdiff) { this.vdiff = vdiff; }

        public double getDistance() { return distance;}
        public void setDistance(double distance) { this.distance = distance; }

    }

}
