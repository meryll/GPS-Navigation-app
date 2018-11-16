package tech.hypermiles.hypermiles.Analysis;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Asia on 2016-11-24.
 */

public class CarProfile
{
    public Map<Pair<Double, Double>, Double> brakingDistanceMap; //<Vo, Vdiff>. Wartoscia jest ilosc METROW kiedy trzeba zaczac hamowac

    public CarProfile(tech.hypermiles.hypermiles.Rest.Model.CarProfile restCarProfile) {

        createBrakingDistanceMap(restCarProfile);
    }

    private void createBrakingDistanceMap(tech.hypermiles.hypermiles.Rest.Model.CarProfile restCarProfile)
    {
        brakingDistanceMap = new HashMap();

        for(int i=0; i<restCarProfile.getDecelerationProfile().length; i++)
        {
            tech.hypermiles.hypermiles.Rest.Model.CarProfile.Entity entity = restCarProfile.getDecelerationProfile()[i];

            Pair<Double, Double> pair = Pair.create(entity.getVstart(), entity.getVdiff());
            brakingDistanceMap.put(pair, entity.getDistance());
        }
    }
}

//public class CarProfile {
//
//    public Map<Pair<Double, Double>, Double> accelerationLookUpTable; //<Vo, Vdiff>. Wartoscia jest srednie przyspieszenie
//    public Map<Pair<Double, Double>, Double> brakingDistanceMap; //<Vo, Vdiff>. Wartoscia jest ilosc METROW kiedy trzeba zaczac hamowac
//    private double[] mAccelerationTable; //tablica przechowujaca pod indeksem i  przyspieszenie dla danej predkosci i*velocityStep) w m/s^2
//
//    public CarProfile() {
//        createAccelerationTable();
//
//        //0  - stale przyspieszenie, 1 - srednia z przyspieszen, 2 - dodawanie drog
//        if (Settings.VARIANT == 0) {
//            createBrakingProfile();
//        } else if (Settings.VARIANT == 1) {
//            createAccelerationLookUpTable();
//            createBrakingProfile();
//        } else if(Settings.VARIANT == 2) {
//            createBrakingDistanceMap();
//        }
//    }
//
//    private void createAccelerationTable() {
//        int max = (int) (Settings.MAX_VELOCITY / Settings.VELOCITY_STEP) + 1;
//        mAccelerationTable = new double[max];
//
//        for (int i = 0; i < max; i++) {
//
//            double currentVelocityInKm = i * Settings.VELOCITY_STEP;
//            double currentVelocityInMeters = PhysicsUtils.changeUnitsKilometersHoursToMetersSeconds(currentVelocityInKm);
//
//            mAccelerationTable[i] = PhysicsUtils.calculateAcceleration(Settings.DECELERATION_PROFILE, currentVelocityInMeters, Settings.CAR_WEIGHT);
//        }
//    }
//
//    private void createBrakingDistanceMap() {
//        brakingDistanceMap = new HashMap<Pair<Double, Double>, Double>();
//
//        double currentVelocityInKM = Settings.VELOCITY_STEP;
//        double currentVelocityDifferenceInKM = Settings.VELOCITY_STEP;
//
//        while (currentVelocityInKM <= Settings.MAX_VELOCITY) {
//
//            //przyspieszenie dla predkosci currentVelocity
//            double deceleration = mAccelerationTable[(int) (currentVelocityInKM / Settings.VELOCITY_STEP)];
//
//            double currentVelocityDifferenceInMeters = PhysicsUtils.changeUnitsKilometersHoursToMetersSeconds(currentVelocityDifferenceInKM);
//            double currentVelocityInMeters = PhysicsUtils.changeUnitsKilometersHoursToMetersSeconds(currentVelocityInKM);
//
//            double deltaTime = PhysicsUtils.calculateTime(currentVelocityDifferenceInMeters, deceleration);
//            double distance = PhysicsUtils.calculateDistance(deltaTime, currentVelocityInMeters, -1 * deceleration);
//
//            Pair<Double, Double> pair = Pair.create(currentVelocityInKM, currentVelocityDifferenceInKM);
//            brakingDistanceMap.put(pair, distance);
//
//            currentVelocityInKM += Settings.VELOCITY_STEP;
//        }
//
//        //--------------------------
//
//        currentVelocityDifferenceInKM += Settings.VELOCITY_STEP;
//        currentVelocityInKM = currentVelocityDifferenceInKM;
//
//        //todo do while?
//        while (currentVelocityDifferenceInKM <= Settings.MAX_VELOCITY) {
//
//            while (currentVelocityInKM <= Settings.MAX_VELOCITY) {
//
//                double sumOfDistance = brakingDistanceMap.get(Pair.create(currentVelocityInKM, Settings.VELOCITY_STEP));
//                double helpVelocity = currentVelocityInKM - Settings.VELOCITY_STEP;
//                while (helpVelocity > 0) {
//
//                    sumOfDistance += brakingDistanceMap.get(Pair.create(helpVelocity, Settings.VELOCITY_STEP));
//                    helpVelocity -= Settings.VELOCITY_STEP;
//                }
//
//                Pair<Double, Double> pair = Pair.create(currentVelocityInKM, currentVelocityDifferenceInKM);
//                brakingDistanceMap.put(pair, sumOfDistance);
//
//                currentVelocityInKM += Settings.VELOCITY_STEP;
//            }
//
//            currentVelocityDifferenceInKM += Settings.VELOCITY_STEP;
//            currentVelocityInKM = currentVelocityDifferenceInKM;
//        }
//    }
//
//    private void createAccelerationLookUpTable() {
//
//        accelerationLookUpTable = new HashMap<Pair<Double, Double>, Double>();
//
//        double currentVelocityInKM = Settings.VELOCITY_STEP;
//        double currentVelocityDifferenceInKM;
//
//        while (currentVelocityInKM <= Settings.MAX_VELOCITY) {
//
//            double currentVelocityInMeters = PhysicsUtils.changeUnitsKilometersHoursToMetersSeconds(currentVelocityInKM);
//            currentVelocityDifferenceInKM = Settings.VELOCITY_STEP;
//
//            while (currentVelocityDifferenceInKM <= currentVelocityInKM) {
//
//                double currentVelocityDifferenceInMeters = PhysicsUtils.changeUnitsKilometersHoursToMetersSeconds(currentVelocityDifferenceInKM);
//                Pair<Double, Double> pair = Pair.create(currentVelocityInKM, currentVelocityDifferenceInKM);
//                double endVelocityInKM = currentVelocityInKM - currentVelocityDifferenceInKM;
//                double endVelocityInMeters = PhysicsUtils.changeUnitsKilometersHoursToMetersSeconds(endVelocityInKM);
//
//                int startIndex = (int) (endVelocityInKM / Settings.VELOCITY_STEP);
//                int endIndex = (int) (currentVelocityInKM / Settings.VELOCITY_STEP);
//
//                double sumOfAcceleration = 0;
//                for (int i = startIndex; i <= endIndex; i++) {
//                    sumOfAcceleration += mAccelerationTable[i];
//                }
//                double averageOfAcceleration = sumOfAcceleration / (endIndex - startIndex);
//                accelerationLookUpTable.put(pair, averageOfAcceleration);
//                currentVelocityDifferenceInKM += Settings.VELOCITY_STEP;
//            }
//            currentVelocityInKM += Settings.VELOCITY_STEP;
//
//        }
//    }
//
//    private void createBrakingProfile() {
//        brakingDistanceMap = new HashMap<Pair<Double, Double>, Double>();
//
//        double currentVelocityInKM = Settings.VELOCITY_STEP;
//        double currentVelocityDifferenceInKM;
//
//        while (currentVelocityInKM <= Settings.MAX_VELOCITY) {
//
//            double currentVelocityInMeters = PhysicsUtils.changeUnitsKilometersHoursToMetersSeconds(currentVelocityInKM);
//            currentVelocityDifferenceInKM = Settings.VELOCITY_STEP;
//
//            while (currentVelocityDifferenceInKM <= currentVelocityInKM) {
//
//                double currentVelocityDifferenceInMeters = PhysicsUtils.changeUnitsKilometersHoursToMetersSeconds(currentVelocityDifferenceInKM);
//
//                double deceleration;
//
//                if(Settings.VARIANT== 0) {
//                    deceleration = Settings.FIXED_ACCELERATION;
//                } else {
//                    deceleration = accelerationLookUpTable.get(Pair.create(currentVelocityInKM, currentVelocityDifferenceInKM));
//                }
//
//                double deltaTime = PhysicsUtils.calculateTime(currentVelocityDifferenceInMeters, deceleration);
//                double distance = PhysicsUtils.calculateDistance(deltaTime, currentVelocityInMeters, -1 * deceleration);
//
//                Pair<Double, Double> pair = Pair.create(currentVelocityInKM, currentVelocityDifferenceInKM);
//                brakingDistanceMap.put(pair, distance);
//                currentVelocityDifferenceInKM += Settings.VELOCITY_STEP;
//            }
//            currentVelocityInKM += Settings.VELOCITY_STEP;
//        }
//    }
//}
