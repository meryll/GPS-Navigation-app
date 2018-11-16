package tech.hypermiles.hypermiles.Analysis.Comparators;

import org.osmdroid.util.GeoPoint;
import java.util.Comparator;
import tech.hypermiles.hypermiles.Analysis.Utiilities.PointerToLocation;
import tech.hypermiles.hypermiles.Utils.LocationUtils;

/**
 * Created by Asia on 2017-05-23.
 */

public class NearestComparator implements Comparator<PointerToLocation> {

    /** The point to be reached. */
    private GeoPoint destination;

    public NearestComparator(GeoPoint destination) {
        this.destination = destination;
    }

    @Override
    public int compare(PointerToLocation p1, PointerToLocation p2) {

        double p1_distance = LocationUtils.getDistanceInMeters(p1.location, destination);
        double p2_distance = LocationUtils.getDistanceInMeters(p2.location, destination);
        return (p1_distance < p2_distance) ? -1 : ((p1_distance > p2_distance) ? 1 : 0);
    }
}