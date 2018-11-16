package tech.hypermiles.hypermiles.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import tech.hypermiles.hypermiles.R;

/**
 * Created by Asia on 2017-05-30.
 */

public class MarkersFactory {

    private ItemizedIconOverlay<OverlayItem> mMarkersOverlay;
    private ArrayList<OverlayItem> mMarkerItems = new ArrayList<OverlayItem>();
    private Context mContext;

    private Drawable mLocationMarker;
    private Drawable mWaypointMarker;

    public MarkersFactory(Context context)
    {
        mContext = context;

        setUpDrawable();
    }

    private void setUpDrawable()
    {
        mLocationMarker = ContextCompat.getDrawable(mContext, R.drawable.map_marker);
        mWaypointMarker = ContextCompat.getDrawable(mContext, R.drawable.waypoint_marker);
    }

    public void buildMarkerItems(ArrayList<GeoPoint> waypoints)
    {
        mMarkerItems.clear();

        for(int i=0; i<waypoints.size(); i++)
        {
            OverlayItem item = new OverlayItem("i: "+i, "Waypoint.", waypoints.get(i));

            if(i==0 || i==waypoints.size()-1) {
                item.setMarker(mLocationMarker);
            } else {
                item.setMarker(mWaypointMarker);
            }

            mMarkerItems.add(item);
        }
    }

    public ItemizedIconOverlay<OverlayItem> getMarkersOverlay()
    {
        mMarkersOverlay = new ItemizedIconOverlay<OverlayItem>(mMarkerItems,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        return true;
                    }

                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return true;
                    }
                }, mContext);

        return mMarkersOverlay;
    }

    public BoundingBox getBoundingBox()
    {
        if(mMarkerItems == null || mMarkerItems.size()<=0) return null;

        double minLat = Integer.MAX_VALUE;
        double maxLat = Integer.MIN_VALUE;
        double minLong = Integer.MAX_VALUE;
        double maxLong = Integer.MIN_VALUE;


        for (OverlayItem item : mMarkerItems) {
            GeoPoint point = (GeoPoint) item.getPoint();
            //todo
            if (point == null) return null;
            if (point.getLatitude() < minLat)
                minLat = point.getLatitude();
            if (point.getLatitude() > maxLat)
                maxLat = point.getLatitude();
            if (point.getLongitude() < minLong)
                minLong = point.getLongitude();
            if (point.getLongitude() > maxLong)
                maxLong = point.getLongitude();
        }

        BoundingBox boundingBox = new BoundingBox(maxLat, maxLong, minLat, minLong);
        return boundingBox;
    }
}
