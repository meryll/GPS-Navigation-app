package tech.hypermiles.hypermiles.Model;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tech.hypermiles.hypermiles.Analysis.StepAnalysis;
import tech.hypermiles.hypermiles.Other.Settings;
import tech.hypermiles.hypermiles.Utils.LocationUtils;
import tech.hypermiles.hypermiles.Middleware.Logger;

/**
 * Created by Asia on 2017-04-20.
 */

public class AnalysedStep extends Step {

    private static final String TAG = "Analysed Step";

    private Polyline mBrakingPolyline;
    private boolean analysed = false;
    private Map<Double, List> brakingPolyMap; //kluczem jest predkosc aktualna, step wie jaka musi byc predkosc koncowa. Na razie przyjmujemy, ze jest to 0

    private StepAnalysis mStepAnalysis = new StepAnalysis();

    public AnalysedStep(Step step)
    {
        super(step);

        mBrakingPolyline = new Polyline();
        int color = Color.rgb(255,0,0);
        mBrakingPolyline.setColor(color);
        mBrakingPolyline.setWidth(20);

        decodedPolyGeoPoint = new ArrayList<>();

        for(int i=0; i<mDecodedPoly.size(); i++) {
            decodedPolyGeoPoint.add(LocationUtils.castToGeoPoint(mDecodedPoly.get(i)));
        }
    }

    public void analyze()
    {
        analysed = false;

        moreDetailedPoly = mStepAnalysis.createMoreDetailedPoly(decodedPolyGeoPoint);
        distanceMap = mStepAnalysis.createDistanceMap(getMoreDetailedPoly());
        brakingPolyMap = mStepAnalysis.createBrakingPath(distanceMap, getMoreDetailedPoly(), getEndSpeed());
        mBearing = getBearingInDegrees();

        analysed = true;
    }

    private void refreshPolylineOptionsForBraking(Double velocity)
    {
        if(analysed = false || brakingPolyMap==null) {
            Logger.wtf(TAG,"Bardzo ciekawe, jak to j est mozliwe "+analysed);
            return;
        }

        List<GeoPoint> newBrakingPolylinePoints = brakingPolyMap.get(velocity);
        if(newBrakingPolylinePoints!=null) {
            mBrakingPolyline.setPoints(newBrakingPolylinePoints);
        } else {
            mBrakingPolyline.setPoints(new ArrayList<GeoPoint>());
        }
    }

    public Polyline buildBrakingOverlay(Double velocity){

        refreshPolylineOptionsForBraking(velocity);
        return mBrakingPolyline;
    }

    private Polyline buildRoadOverlay(List<LatLng> polylineToDraw, int color, float width){

        Polyline roadOverlay = new Polyline();
        roadOverlay.setColor(color);
        roadOverlay.setWidth(width);
        if (polylineToDraw != null) {
            ArrayList<GeoPoint> polyline = new ArrayList<>(decodedPolyGeoPoint);
            roadOverlay.setPoints(polyline);
        }
        return roadOverlay;
    }

    public List getBrakingPathForVelocity(double velocity)
    {
        if(brakingPolyMap==null) return null;

        List brakingPoly = null;

        do {
            brakingPoly = brakingPolyMap.get(velocity);
            velocity-= Settings.VELOCITY_STEP;
            Logger.i(TAG, "Aktualna velocity "+velocity);
        } while(brakingPoly == null && velocity>Settings.VELOCITY_STEP);

        return brakingPoly;
    }

    private int getRandomColor()
    {
        Random rnd = new Random();
//        return Color.argb(255, 0, 0, 255);
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public Polyline buildRoadOverlay(){
        return buildRoadOverlay(mDecodedPoly, getRandomColor(), 5.0f);
    }
}
