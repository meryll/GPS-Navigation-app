package tech.hypermiles.hypermiles.Analysis.Utiilities;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Asia on 2017-05-23.
 */

public class PointerToLocation
{
    public GeoPoint location;
    public int stepIndex;
    public int legIndex;
    public int pointInStepIndex;

    public PointerToLocation(GeoPoint location, int step, int pointInStep)
    {
        this.location = location;
        this.stepIndex = step;
        this.pointInStepIndex = pointInStep;
    }
}