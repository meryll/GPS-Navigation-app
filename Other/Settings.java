package tech.hypermiles.hypermiles.Other;

/**
 * Created by Asia on 2017-01-27.
 */

public class Settings {

    public static int MIN_DISTANCE;
    public static int MAX_DISTANCE; //po jakim dystansie przelicza sie trasa
    public static int DESTINATION_REACHED_LIMIT = 50;
    public static int VARIANT;
    public static double CAR_WEIGHT;
    public static double MAX_VELOCITY = 130; //maksymalna predkosc jaka moze osiagnac pojazd
    public static double VELOCITY_STEP = 5; //o ile bedziemy iterowac predkosc tak dla szybkich testow, w km/h
    public static double FIXED_ACCELERATION; // m/s^2
    public static double DECELERATION_PROFILE; // 0.5 m^2

    public static double STOP = 0; // m/s^2
    public static double TURN_SHARP;
    public static double UTURN;
    public static double TURN_SLIGHT;
    public static double MERGE;
    public static double MERGE_SHARP;
    public static double MERGE_SLIGHT;
    public static double MERGE_STRAIGHT;
    public static double ROUNDABOUT;
    public static double TURN;
    public static double RAMP;
    public static double RAMP_SHARP;
    public static double RAMP_SLIGHT;
    public static double RAMP_STRAIGHT;
    public static double STRAIGHT;
    public static double FORK;
    public static double FERRY;
    public static double KEEP;

    public static int TIMEOUT_IN_SECONDS = 900000;

    public static Boolean NAVIGATE = true;
    public static Boolean SAVE_DATA = true;
    public static Boolean POST_TO_SERVER = false;

    public static float CAMERA_ZOOM = 16;
    public static int MINIMAL_POLY_LENGHT = 10;
    public static int MAP_PADDING = 20;
    public static Boolean PLAY_SOUND = false;
    public static Boolean USE_MAX_SPEED = false;

}
