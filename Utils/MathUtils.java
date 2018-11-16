package tech.hypermiles.hypermiles.Utils;

import tech.hypermiles.hypermiles.Other.Settings;

/**
 * Created by Asia on 2017-01-19.
 */

public class MathUtils {

    public static Double round(double number)
    {
        Double rounded = (Math.round(number/ Settings.VELOCITY_STEP) * Settings.VELOCITY_STEP);
        return  rounded;
    }
}
