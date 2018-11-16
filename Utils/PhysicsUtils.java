package tech.hypermiles.hypermiles.Utils;

/**
 * Created by Asia on 2016-11-24.
 */

public class PhysicsUtils {

    //todo airDensity
    private static final double airDensity = 1;

    public static double calculateAcceleration(double carProfile, double velocity, double carWeight) {
        return (0.5*airDensity*carProfile*velocity*velocity)/carWeight;
    }

    public static double calculateTime(double velocityDiff, double acceleration) {
        return velocityDiff/acceleration;
    }

    public static double calculateDistance(double time, double velocity0, double acceleration)
    {
        return velocity0*time+((acceleration*time*time)/2);
    }

    public static double changeUnitsKilometersHoursToMetersSeconds(double velocity) {
        return velocity*1000/3600;
    }
}
