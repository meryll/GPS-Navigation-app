package tech.hypermiles.hypermiles.Model;

import java.util.HashMap;
import java.util.Map;

import tech.hypermiles.hypermiles.Other.Settings;

public class Maneuvers {

    public static final Map<String, Double> velocityLookUpMap = new HashMap<String, Double>();

    static {
        velocityLookUpMap.put("new name", Settings.STRAIGHT); //road name change
        velocityLookUpMap.put("turn-straight", Settings.STRAIGHT); //Continue straight
        velocityLookUpMap.put("turn-slight right", Settings.TURN_SLIGHT); //Slight right
        velocityLookUpMap.put("turn-right", Settings.TURN); //Right
        velocityLookUpMap.put("turn-sharp right", Settings.TURN_SHARP); //Sharp right
        velocityLookUpMap.put("turn-uturn", Settings.UTURN); //U-turn
        velocityLookUpMap.put("turn-sharp left", Settings.TURN_SHARP); //Sharp left
        velocityLookUpMap.put("turn-left", Settings.TURN); //Left
        velocityLookUpMap.put("turn-slight left", Settings.TURN_SLIGHT); //Slight left
        velocityLookUpMap.put("depart", Settings.STRAIGHT); //"Head" => used by OSRM as the start node. Considered here as a "waypoint".
        velocityLookUpMap.put("arrive", Settings.STOP); //Arrived (at waypoint)
        velocityLookUpMap.put("roundabout-1", Settings.ROUNDABOUT); //Round-about, 1st exit
        velocityLookUpMap.put("roundabout-2", Settings.ROUNDABOUT); //2nd exit, etc ...
        velocityLookUpMap.put("roundabout-3", Settings.ROUNDABOUT);
        velocityLookUpMap.put("roundabout-4", Settings.ROUNDABOUT);
        velocityLookUpMap.put("roundabout-5", Settings.ROUNDABOUT);
        velocityLookUpMap.put("roundabout-6", Settings.ROUNDABOUT);
        velocityLookUpMap.put("roundabout-7", Settings.ROUNDABOUT);
        velocityLookUpMap.put("roundabout-8", Settings.ROUNDABOUT); //Round-about, 8th exit
        velocityLookUpMap.put("merge-left", Settings.MERGE);
        velocityLookUpMap.put("merge-sharp left", Settings.MERGE_SHARP);
        velocityLookUpMap.put("merge-slight left", Settings.MERGE_SLIGHT);
        velocityLookUpMap.put("merge-right", Settings.MERGE);
        velocityLookUpMap.put("merge-sharp right", Settings.MERGE_SLIGHT);
        velocityLookUpMap.put("merge-slight right", Settings.MERGE_SLIGHT);
        velocityLookUpMap.put("merge-straight", Settings.MERGE_STRAIGHT);
        velocityLookUpMap.put("ramp-left", Settings.RAMP);
        velocityLookUpMap.put("ramp-sharp left", Settings.RAMP_SHARP);
        velocityLookUpMap.put("ramp-slight left", Settings.RAMP_SLIGHT);
        velocityLookUpMap.put("ramp-right", Settings.RAMP);
        velocityLookUpMap.put("ramp-sharp right", Settings.RAMP_SHARP);
        velocityLookUpMap.put("ramp-slight right", Settings.RAMP_SLIGHT);
        velocityLookUpMap.put("ramp-straight", Settings.RAMP_STRAIGHT);
        velocityLookUpMap.put("arrive", Settings.STOP);
        velocityLookUpMap.put("fork", Settings.FORK);
        velocityLookUpMap.put("continue", Settings.STRAIGHT);
        velocityLookUpMap.put("depart", Settings.STRAIGHT);
        velocityLookUpMap.put("new name", Settings.STRAIGHT);
    }
}
