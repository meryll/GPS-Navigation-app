package tech.hypermiles.hypermiles.Analysis;

import android.util.Pair;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tech.hypermiles.hypermiles.Model.AnalysedRoad;
import tech.hypermiles.hypermiles.Model.AnalysedStep;
import tech.hypermiles.hypermiles.Utils.LocationUtils;
import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Model.Step;
import tech.hypermiles.hypermiles.Other.Settings;

/**
 * Created by Joasi on 10/03/17.
 */

public class StepAnalysis {

    private static final String TAG = "StepAnalysis";

    public List<GeoPoint> createMoreDetailedPoly(List<GeoPoint> decodedPoly) {

        //todo! decodePoly w init!
        if(decodedPoly.size()==1) {
            return decodedPoly;
        }

        List<GeoPoint> moreDetailedPoly = new ArrayList<GeoPoint>();

        for(int i=0; i<decodedPoly.size()-1; i++) {

            GeoPoint start = decodedPoly.get(i);
            GeoPoint end = decodedPoly.get(i+1);

            LinkedList<GeoPoint> queue = new LinkedList<>();

            List<GeoPoint> divided = dividePoly(new ArrayList<GeoPoint>(), queue, start, end);
            moreDetailedPoly.addAll(divided);
        }
        if(moreDetailedPoly.size()>1) {
            moreDetailedPoly.remove(0);
        }
        return moreDetailedPoly;
    }

    public double[] createDistanceMap(List<GeoPoint> moreDetailedPoly)
    {
        double[] distanceMap = new double[moreDetailedPoly.size()-1];

        double lastDistanceTillTheEnd = 0;
        double distanceTillTheEnd = 0;
        for(int i=moreDetailedPoly.size()-2; i>=0; i--)
        {
            GeoPoint start = moreDetailedPoly.get(i);
            GeoPoint end = moreDetailedPoly.get(i+1);

            double distance = LocationUtils.getDistanceInMeters(start, end);
            distanceTillTheEnd += distance;

            //todo tymczasowe rozwiazanie
            if(distanceTillTheEnd < lastDistanceTillTheEnd) {
                distanceMap[i] = lastDistanceTillTheEnd;
            } else
            {
                distanceMap[i] = distanceTillTheEnd;

            }
            lastDistanceTillTheEnd = distanceTillTheEnd;
        }
        return distanceMap;
    }

    public Map<Double, List> createBrakingPath(double[] distanceMap, List<GeoPoint> moreDetailedPoly, double endVelocity)
    {
        HashMap<Double, List> brakingPolyMap = new HashMap<Double, List>();

        double velocity = Settings.VELOCITY_STEP;

        while(velocity<= Settings.MAX_VELOCITY)
        {
            List brakingPathForVelocity = new ArrayList<>();
            double velocityDiff = getVelocityDifference(velocity, endVelocity);
            double distanceForVelocity = getDistanceForVelocityDifference(NavigationSingleton.getInstance().getCarProfile(), velocity, velocityDiff);

            for(int i=0; i<distanceMap.length; i++)
            {
                if(distanceMap[i] <= distanceForVelocity) {
                    brakingPathForVelocity.add(moreDetailedPoly.get(i));
                    if(i==distanceMap.length-1) {
                        brakingPathForVelocity.add(moreDetailedPoly.get(i+1));
                    }
                }
            }
            brakingPolyMap.put(velocity, brakingPathForVelocity);
            velocity+= Settings.VELOCITY_STEP;
        }
        return brakingPolyMap;
    }

    private double getVelocityDifference(double velocity, double endVelocity)
    {
        double velocityDiff = velocity - endVelocity;
        if(velocityDiff<0)
        {
            return 0.0;
        } else {
            return velocityDiff;
        }
    }

    private double getDistanceForVelocityDifference(CarProfile carProfile, double velocity, double velocityDifference) {
        if(velocityDifference<=0.0) {
            return 0.0;
        }
        try {
            return carProfile.brakingDistanceMap.get(Pair.create(velocity, velocityDifference));
        }
        catch(Exception e)
        {
            //todo!!!
            return 0;
        }
    }

    private List<GeoPoint> dividePoly(ArrayList<GeoPoint> list, LinkedList<GeoPoint> queue, GeoPoint start, GeoPoint end)
    {
        if(!list.contains(start) && start != end) {
            list.add(start);
        }

        double distance = LocationUtils.getDistanceInMeters(start, end);


        if(distance< Settings.MINIMAL_POLY_LENGHT) {

            if(!list.contains(end)) {
                list.add(end);
            }

            if(queue.size()==1) {
                GeoPoint point = queue.poll();
                if(!list.contains(point)) {
                    list.add(point);
                }
                return list;
            }

            if(queue.size()==0) {
                return list;
            }

            GeoPoint nextA = queue.poll();
            GeoPoint nextB = queue.peek();

            return dividePoly(list, queue, nextA, nextB);
        } else {

            GeoPoint midPoint = LocationUtils.getMidPoint(start, end);
            queue.addFirst(end);
            return dividePoly(list, queue, start, midPoint);
        }
    }


}
