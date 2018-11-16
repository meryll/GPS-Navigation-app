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