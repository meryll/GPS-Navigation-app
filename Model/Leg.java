package tech.hypermiles.hypermiles.Model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.osmdroid.bonuspack.routing.RoadLeg;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Joasi on 10/03/17.
 */

@Root(name = "Leg", strict = false)
public class Leg {

    @SerializedName("length")
    @Expose
    private double mLength;

    //in seconds
    @SerializedName("duration")
    @Expose
    private double mDuration;
    //starting node of the leg, as index in nodes array
    @SerializedName("startNodeIndex")
    @Expose
    private int mStartNodeIndex;
    //and ending node
    @SerializedName("endNodeIndex")
    @Expose
    private int mEndNodeIndex;

    public Leg(){
        mLength = mDuration = 0.0;
        mStartNodeIndex = mEndNodeIndex = 0;
    }

    public int getStartIndex()
    {
        return  mStartNodeIndex;
    }

}
