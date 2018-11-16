package tech.hypermiles.hypermiles.Rest.Utils;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

import tech.hypermiles.hypermiles.Analysis.NavigationSingleton;
import tech.hypermiles.hypermiles.Utils.LocationUtils;
import tech.hypermiles.hypermiles.Other.TempAppData;
import tech.hypermiles.hypermiles.Rest.Model.RouteChange;
import tech.hypermiles.hypermiles.Rest.Model.RoutePoint;

/**
 * Created by Asia on 2017-03-27.
 */

public class RestBuilder {

    private NavigationSingleton navigationSingleton;

    public RestBuilder()
    {
        navigationSingleton = NavigationSingleton.getInstance();
    }

    public RoutePoint buildRoutePoint(double currentSpeed, GeoPoint currentLocation, Boolean shouldBeBraking)
    {
        RoutePoint currentRoutePoint = new RoutePoint();
        currentRoutePoint.setEndManueverType(navigationSingleton.getCurrentStep().getManeuverType());
        currentRoutePoint.setLegNumber(0);
        currentRoutePoint.setSpeed((int)currentSpeed);
        currentRoutePoint.setPointInStepNumber(navigationSingleton.getCurrentPointInStepIndex());
        currentRoutePoint.setStepInLegNumber(navigationSingleton.getCurrentStepIndex());
        currentRoutePoint.setLocation(LocationUtils.castSerializableMyLatLng(currentLocation));
        currentRoutePoint.setRouteId(TempAppData.ROUTE_ID);
        currentRoutePoint.setShouldBeBraking(shouldBeBraking);
        return currentRoutePoint;
    }

    public RouteChange buildRouteChange(GeoPoint currentLocation)
    {
        RouteChange routeChange = new RouteChange();
        routeChange.setRouteId(TempAppData.ROUTE_ID);
        routeChange.setStartDate(new Date());
        routeChange.setEndDate(new Date());
        routeChange.setStartLocation(LocationUtils.castToLatLng(currentLocation));
        routeChange.setEndLocation(LocationUtils.castToLatLng(currentLocation));
//        routeChange.setRouteData(navigationSingleton.getDownloadedRoad().decodedRoute);
        return routeChange;
    }
}
