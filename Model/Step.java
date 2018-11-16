package tech.hypermiles.hypermiles.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import tech.hypermiles.hypermiles.Rest.Model.SerializableLatLng;

/**
 * Created by Joasi on 10/03/17.
 */

//Retrofit wyrzuca blad przy probuje sparsowania Polyline (wyrzuca zawsze gdy jest taki typ w klasie)
    //wiec trzeba rozdzielic step na ten pobierany z serwera i na ten przeanalizowany przez appke
    //albo na step logiczny i step do wyswietlenia na mapie.
public class Step {

    private static final String TAG = "Step";

    GeoPoint mEndLocation;
    GeoPoint mStartLocation;
    List<GeoPoint> decodedPolyGeoPoint;

    @SerializedName("codedPoly")
    @Expose
    String mCodedPoly;

    @SerializedName("moreDetailedPoly")
    @Expose
    public List<GeoPoint> moreDetailedPoly;

    @SerializedName("decodedPoly")
    @Expose
    List<LatLng> mDecodedPoly;

    @SerializedName("distanceMap")
    @Expose
    double[] distanceMap;

    @SerializedName("endSpeed")
    @Expose
    double mEndSpeed;

    @SerializedName("maxSpeed")
    @Expose
    double mMaxSpeed = 0;

    @SerializedName("bearing")
    @Expose
    double mBearing = 0;

    @SerializedName("direction")
    @Expose
    String mDirection;

    @SerializedName("maneuverType")
    @Expose
    int mManeuverType;

    @SerializedName("instructions")
    @Expose
    String mInstructions;

    @SerializedName("nextRoadLink")
    @Expose
    int mNextRoadLink;

    @SerializedName("length")
    @Expose
    double mLength;

    @SerializedName("duration")
    @Expose
    double mDuration;

    @SerializedName("location")
    @Expose
    SerializableLatLng mLocation;

    public Step(Step step)
    {
        this.mEndLocation = step.mEndLocation;
        this.mStartLocation = step.mStartLocation;
        this.moreDetailedPoly = step.moreDetailedPoly;
        this.mDecodedPoly = step.mDecodedPoly;
        this.distanceMap = step.distanceMap;
        this.mBearing = step.mBearing;
        this.mEndLocation = step.mEndLocation;
        this.mDirection = step.mDirection;
        this.mDuration = step.mDuration;
        this.mManeuverType = step.mManeuverType;
        this.mInstructions = step.mInstructions;
        this.mNextRoadLink = step.mNextRoadLink;
        this.mMaxSpeed = step.mMaxSpeed;
        this.mLength = step.mLength;
        this.mLocation = step.mLocation;
    }

    public double getBearingInDegrees()
    {
        return mBearing;
        //todo mLocation czy mStartLocation?
//        return LocationUtils.getBearingInDegrees(mStartLocation, mEndLocation);
    }

    //region getters

    public List<GeoPoint> getMoreDetailedPoly() {
        return moreDetailedPoly;
    }

    public int getMoreDetailedPolySize()
    {
        return moreDetailedPoly.size();
    }

    public GeoPoint getPointFromMoreDetailedPoly(int i)
    {
        return moreDetailedPoly.get(i);
    }

    public String getInstructions() {
        return mInstructions;
    }

    public double getDuration() {
        return mDuration;
    }

    public double getLength() {
        return mLength;
    }

    public double getMaxSpeed() { return mMaxSpeed; }

    public int getManeuverType() { return mManeuverType; }
    public double getEndSpeed() { return mEndSpeed; }
    //endregion

    public Boolean isNullOrEmpty()
    {
        return (this == null || this.mDecodedPoly == null);
    }
}
