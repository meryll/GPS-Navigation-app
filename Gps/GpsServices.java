package tech.hypermiles.hypermiles.Gps;

import android.location.Location;

import tech.hypermiles.hypermiles.Utils.MathUtils;
import tech.hypermiles.hypermiles.Other.Settings;

/**
 * Created by Asia on 2017-01-17.
 */

public class GpsServices extends Location {


    public GpsServices(Location location) {
        super(location);
    }

    @Override
    public float getSpeed() {
        return super.getSpeed() * 3.6f;
    }

    public Double getSpeedInDouble() throws Exception {
        if(Settings.VELOCITY_STEP==10.0 || Settings.VELOCITY_STEP == 5.0) {
            return MathUtils.round(Double.valueOf(getSpeed()));
        } else if(Settings.VELOCITY_STEP == 1.0) {
            return Double.valueOf((int) getSpeed());
        } else {
            throw new Exception("Invalid velocity step. Must be 10 or 1 or 5");
        }
    }

}
