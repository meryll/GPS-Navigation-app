package tech.hypermiles.hypermiles.Utils;

/**
 * Created by Asia on 2017-05-30.
 */

import android.content.Context;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.R;

public class MapViewUtils {

    private final String TAG = "MAP UTILS";
    private Context mContext;
    private MapView mMapView;
    private MapController mMapController;

    public MapViewUtils(MapView mapView, Context context)
    {
        this.mContext = context;
        this.mMapView = mapView;
        this.mMapController = (MapController) mMapView.getController();

        init();
    }

    public void setUpMyLocationOverlay() {
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(mContext.getApplicationContext()), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(mLocationOverlay);
    }

    public void invalidate()
    {
        mMapView.invalidate();
    }

    public void addOverlay(int index, ItemizedIconOverlay<OverlayItem> overlay)
    {
        mMapView.getOverlays().add(index, overlay);
    }

    public void addOverlay(int index, Polyline overlay)
    {
        try
        {
            mMapView.getOverlays().add(index, overlay);
        } catch(Exception e)
        {
            Logger.wtf(TAG, e.getMessage());
            Logger.i(TAG, "Index "+index+" "+mMapView.getOverlays().size());
        }
    }

    public void addOverlay(Polyline overlay)
    {
        mMapView.getOverlays().add(overlay);
    }

    public void removeOvelay(int index)
    {
        mMapView.getOverlays().remove(index);
    }

    public void cleanMap() {
        for (int i = mMapView.getOverlays().size() - 1; i >= 1; i--) {
            mMapView.getOverlays().remove(i);
        }
    }

    public void setInitialCameraPosition(BoundingBox boundingBox ) {

        if(boundingBox == null) return;

        mMapView.zoomToBoundingBox(boundingBox, false);
        mMapView.setMapOrientation(0);
    }

    public void updateCamera(GeoPoint location, float bearing) {

        if(mMapView == null) return;

        animateNewCameraPosition(location, mMapView.getZoomLevel(), bearing);

//        animateNewCameraPosition(LocationUtils.castToGeoPoint(navigationSingleton.getCurrentStep().mLocation), mMapView.getZoomLevel(), angle);
    }

    private void animateNewCameraPosition(GeoPoint target, float zoom, float bearing) {

        if(mMapView == null || mMapController==null) return;

        bearing = 0;
        mMapController.animateTo(target);
        mMapController.setZoom((int) zoom);
        mMapView.setMapOrientation(bearing);
    }

    public void moveNewCameraPosition(GeoPoint target, float zoom, float bearing) {

        if(mMapView == null || mMapController==null) return;

        bearing = 0;
        mMapController.setCenter(target);
        mMapController.setZoom((int) zoom);
        mMapView.setMapOrientation(bearing);
    }

    private void init()
    {
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapController.setZoom(16);
    }
}
