package tech.hypermiles.hypermiles.Analysis;

import android.os.Handler;
import android.os.Message;

import org.osmdroid.util.GeoPoint;

import java.util.Collections;
import java.util.List;

import tech.hypermiles.hypermiles.Analysis.Comparators.NearestComparator;
import tech.hypermiles.hypermiles.Analysis.Utiilities.PointerToLocation;
import tech.hypermiles.hypermiles.Utils.LocationUtils;
import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Model.AnalysedRoad;
import tech.hypermiles.hypermiles.Model.AnalysedStep;
import tech.hypermiles.hypermiles.Other.Messages;
import tech.hypermiles.hypermiles.Other.Settings;

/**
 * Created by Joasi on 23/02/17.
 */

public class NavigationSingleton {

    private final String TAG = "NavigationSingleton";
    private static NavigationSingleton mInstance = null;
    private Handler mHandler;

    private AnalysedRoad mCurrentAnalysedRoad;

    private int mCurrentPointInStepIndex;
    private int mCurrentStepIndex;
    private Boolean mCurrentShouldBeBrakingValue;
    private GeoPoint mDestination;
    private CarProfile mCarProfile;

    private int offRouteCounter = 0;
    private final int offRouteMaxCounter = 1;

    public static NavigationSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new NavigationSingleton();
        }
        return mInstance;
    }

    //region public getters
    private GeoPoint getLastPoint()
    {
        GeoPoint point = null;
        try
        {
            point = getCurrentStep().getPointFromMoreDetailedPoly(mCurrentPointInStepIndex);
        }
        catch(Exception e)
        {
            Logger.wtf(TAG, e.getMessage());
            Logger.i(TAG, "MCurrentpoiny in step "+mCurrentPointInStepIndex+" size "+getCurrentStep().getMoreDetailedPolySize());
        }
        return point;
    }

    public CarProfile getCarProfile() {
        return mCarProfile;
    }

    public AnalysedStep getCurrentStep() {
        return mCurrentAnalysedRoad.getStep(mCurrentStepIndex);
    }

    public AnalysedStep getNextStep() {
        return mCurrentAnalysedRoad.getStep(mCurrentStepIndex + 1);
    }

    public int getCurrentPointInStepIndex() {
        return mCurrentPointInStepIndex;
    }

    public int getCurrentStepIndex() {
        return mCurrentStepIndex;
    }

    public AnalysedRoad getCurrentAnalysedRoad() {return mCurrentAnalysedRoad; }
    public void setCurrentAnalysedRoad(AnalysedRoad analysedRoad) { mCurrentAnalysedRoad = analysedRoad; }
    //endregion

    //region messages

    private void sentOffRoadMessage() {
        if (offRouteCounter<=offRouteMaxCounter)
        {
            offRouteCounter++;
            return;
        }

        Message mMessage = Message.obtain(mHandler, Messages.OFF_ROUTE);
        mHandler.sendMessage(mMessage);
    }

    private void sendNewStepMessage() {
        Message mMessage = Message.obtain(mHandler, Messages.NEW_STEP);
        mHandler.sendMessage(mMessage);
    }

    private void sendReachDestinationMessage() {
        Message mMessage = Message.obtain(mHandler, Messages.REACHED_DESTINATION);
        mHandler.sendMessage(mMessage);
    }
    //endregion

    public void init(Handler handler) {
        mHandler = handler;
    }

    public void setCarProfile(tech.hypermiles.hypermiles.Rest.Model.CarProfile restCarProfile) {
        mCarProfile = new CarProfile(restCarProfile);
    }

    public void deletePreviousRoad()
    {
        mCurrentAnalysedRoad = null;
    }

    public void startRoad(GeoPoint location, GeoPoint destination) {

        Logger.i(TAG, "We are starting new road");
        mDestination = destination;
        mCurrentShouldBeBrakingValue = false;

        setFirstClosestPoint(location);
    }

    public Boolean shouldBeBraking(GeoPoint location, Double velocity) {

        if (getCurrentStep().isNullOrEmpty() || isVelocityTooSmall(velocity)) {
            mCurrentShouldBeBrakingValue = false;
            return mCurrentShouldBeBrakingValue;
        }

        getNextClosestPoint(location);

        if (exceededSpeedLimit(velocity)) {
            mCurrentShouldBeBrakingValue = true;
            return mCurrentShouldBeBrakingValue;
        }

        mCurrentShouldBeBrakingValue = shouldBeBrakingForVelocity(velocity);
        return mCurrentShouldBeBrakingValue;
    }

    private void getNextClosestPoint(GeoPoint currentLocation) {

        double distance = LocationUtils.getDistanceInMeters(getLastPoint(), currentLocation);
        if (distance <= Settings.MIN_DISTANCE) {
            offRouteCounter = 0;
            return;
        }
        getClosestPoint(currentLocation);
    }

    private void getClosestPoint(GeoPoint currentLocation) {
        if (currentLocation == null) return;

        PointerToLocation nearest = getNearestPointerToLocation(currentLocation);
        double distance = LocationUtils.getDistanceInMeters(currentLocation, nearest.location);

        if (distance > Settings.MAX_DISTANCE) {
            Logger.d(TAG, "Odleglosc wieksza niz MIN distance. Zjechaliśmy z trasy?");
            sentOffRoadMessage();
        } else {
            setCurrentPointerToLocation(nearest);
        }
    }

    public void setFirstClosestPoint(GeoPoint currentLocation) {
        if (currentLocation == null) return;

        PointerToLocation nearest = getNearestPointerToLocation(currentLocation);
        setCurrentPointerToLocation(nearest);
        Logger.i(TAG,"setFirstClosestPoint "+nearest.legIndex+" "+nearest.stepIndex);
    }

    private void setCurrentPointerToLocation(PointerToLocation pointer) {

        offRouteCounter = 0;

        if (mCurrentStepIndex != pointer.stepIndex) {
            sendNewStepMessage();
        }
        mCurrentStepIndex = pointer.stepIndex;
        mCurrentPointInStepIndex = pointer.pointInStepIndex;

        if(destinationReached(pointer.location)) {
            sendReachDestinationMessage();
        }
    }

    private Boolean destinationReached(GeoPoint currentLocation)
    {
        double distanceToLastPoint = LocationUtils.getDistanceInMeters(currentLocation, mDestination);
        Logger.i(TAG, "DISTANCE "+distanceToLastPoint+" "+Settings.DESTINATION_REACHED_LIMIT);
        return distanceToLastPoint<=Settings.DESTINATION_REACHED_LIMIT;
    }

    private PointerToLocation getNearestPointerToLocation(GeoPoint location) {
        return Collections.min(mCurrentAnalysedRoad.getListOfAllThePointers(), new NearestComparator(location));
    }

    public Boolean roadIsNotNull() {
        return mCurrentAnalysedRoad != null && mCurrentAnalysedRoad.stepExists();
    }

    private Boolean shouldBeBrakingForVelocity(Double velocity)
    {
        List brakingPathForVelocity = getCurrentStep().getBrakingPathForVelocity(velocity);

        if(brakingPathForVelocity==null) return false;

        return brakingPathForVelocity.contains(getLastPoint());
    }

    private Boolean exceededSpeedLimit(Double velocity)
    {
        return Settings.USE_MAX_SPEED && velocity > getCurrentStep().getMaxSpeed();
    }

    private Boolean isVelocityTooSmall(Double velocity)
    {
        return velocity < Settings.VELOCITY_STEP;
    }

}
