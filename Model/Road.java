package tech.hypermiles.hypermiles.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tech.hypermiles.hypermiles.Analysis.Utiilities.PointerToLocation;

/**
 * Created by Joasi on 10/03/17.
 */

@Root(name = "Road", strict = false)
public class Road implements Serializable {

    @SerializedName("status")
    @Expose
    int mStatus;
    @SerializedName("length")
    @Expose
    double mLength;
    @SerializedName("duration")
    @Expose
    double mDuration;

    @SerializedName("decodedRoute")
    @Expose
    String decodedRoute;

    @SerializedName("steps")
    @Expose
    ArrayList<Step> mSteps;

    @SerializedName("legs")
    @Expose
    ArrayList<Leg> mLegs;

    public Road() {
        init();
    }

    private void init(){
//        mStatus = STATUS_INVALID;
        mLength = 0.0;
        mDuration = 0.0;
//        mRouteHigh = new ArrayList<>();
//        mRouteLow = null;
        mLegs = new ArrayList<>();
//        mBoundingBox = null;
    }

    public ArrayList<Leg> getLegs()
    {
        return mLegs;
    }

    public Boolean isNullOrEmpty()
    {
        return (this==null || this.mSteps == null);
    }

    public int getStartNodeOfTheLastLeg()
    {
        return mLegs.get(this.getLegsSize()-1).getStartIndex();
    }

    public int getLegsSize()
    {
        return mLegs.size();
    }

    public Leg getLeg(int i)
    {
        return mLegs.get(i);
    }

    public int getStepsSize()
    {
        return mSteps.size();
    }

    public Step getStep(int i)
    {
        return mSteps.get(i);
    }

}
