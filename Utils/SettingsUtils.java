package tech.hypermiles.hypermiles.Utils;

import android.support.v4.app.FragmentActivity;
import tech.hypermiles.hypermiles.Middleware.SavedDataManager;
import tech.hypermiles.hypermiles.Other.Settings;

/**
 * Created by Asia on 2017-01-30.
 */

//TODO zmienic tak zeby nie robic wszedzie kopiuj wklej ze zmiana nazwy
public class SettingsUtils {

    private SavedDataManager mSavedDataManager;

    public SettingsUtils(FragmentActivity activity)
    {
        mSavedDataManager = new SavedDataManager(activity);
    }

    public void readValues()
    {
        Settings.MIN_DISTANCE = mSavedDataManager.getMagicNumberValue("MIN_DISTANCE").intValue();
        Settings.MAX_DISTANCE = mSavedDataManager.getMagicNumberValue("MAX_DISTANCE").intValue();
        Settings.CAR_WEIGHT = mSavedDataManager.getMagicNumberValue("CAR_WEIGHT");
        Settings.VARIANT = mSavedDataManager.getMagicNumberValue("VARIANT").intValue();
        Settings.DECELERATION_PROFILE = mSavedDataManager.getMagicNumberValue("DECELERATION_PROFILE");
        Settings.FIXED_ACCELERATION = mSavedDataManager.getMagicNumberValue("FIXED_ACCELERATION");
        Settings.TURN_SHARP = mSavedDataManager.getMagicNumberValue("TURN_SHARP");
        Settings.UTURN = mSavedDataManager.getMagicNumberValue("UTURN");
        Settings.TURN_SLIGHT = mSavedDataManager.getMagicNumberValue("TURN_SLIGHT");
        Settings.MERGE = mSavedDataManager.getMagicNumberValue("MERGE");
        Settings.MERGE_SLIGHT = mSavedDataManager.getMagicNumberValue("MERGE_SLIGHT");
        Settings.MERGE_STRAIGHT = mSavedDataManager.getMagicNumberValue("MERGE_STRAIGHT");
        Settings.MERGE_SHARP = mSavedDataManager.getMagicNumberValue("MERGE_SHARP");

        Settings.ROUNDABOUT = mSavedDataManager.getMagicNumberValue("ROUNDABOUT");
        Settings.TURN = mSavedDataManager.getMagicNumberValue("TURN");
        Settings.RAMP = mSavedDataManager.getMagicNumberValue("RAMP");
        Settings.RAMP_SLIGHT = mSavedDataManager.getMagicNumberValue("RAMP_SLIGHT");
        Settings.RAMP_STRAIGHT = mSavedDataManager.getMagicNumberValue("RAMP_STRAIGHT");
        Settings.RAMP_SHARP = mSavedDataManager.getMagicNumberValue("RAMP_SHARP");

        Settings.STRAIGHT = mSavedDataManager.getMagicNumberValue("STRAIGHT");
        Settings.FORK = mSavedDataManager.getMagicNumberValue("FORK");
        Settings.FERRY = mSavedDataManager.getMagicNumberValue("FERRY");
        Settings.KEEP = mSavedDataManager.getMagicNumberValue("KEEP");

        if(Settings.MIN_DISTANCE<0) Settings.MIN_DISTANCE = 15;
        if(Settings.MAX_DISTANCE<0) Settings.MAX_DISTANCE = 25;
        if(Settings.VARIANT<0) Settings.VARIANT=0;
        if(Settings.CAR_WEIGHT<0) Settings.CAR_WEIGHT = 1830;
        if(Settings.DECELERATION_PROFILE<0) Settings.DECELERATION_PROFILE = 4.5;
        if(Settings.FIXED_ACCELERATION<0) Settings.FIXED_ACCELERATION = 0.3794013292;
        if(Settings.TURN_SHARP<0) Settings.TURN_SHARP = 10.0;
        if(Settings.UTURN<0) Settings.UTURN = 10.0;
        if(Settings.TURN_SLIGHT<0) Settings.TURN_SLIGHT = 50.0; //0.75 * speed limit w danym miejscu?
        if(Settings.MERGE<0) Settings.MERGE = 50.0;
        if(Settings.MERGE_SHARP<0) Settings.MERGE_SHARP = 60.0; //0.75  *speed limit w danym miejscu? 0.5?
        if(Settings.MERGE_SLIGHT<0) Settings.MERGE_SLIGHT = 70.0; //speed limit w danym miejscu?
        if(Settings.MERGE_STRAIGHT<0) Settings.MERGE_STRAIGHT = 70.0; //speed limit w danym miejscu?
        if(Settings.ROUNDABOUT<0) Settings.ROUNDABOUT = 25.0;
        if(Settings.TURN<0) Settings.TURN = 20.0;
        if(Settings.RAMP<0) Settings.RAMP = 50.0;
        if(Settings.RAMP_SHARP<0) Settings.RAMP_SHARP = 35.0;
        if(Settings.RAMP_STRAIGHT<0) Settings.RAMP_STRAIGHT = 70.0;//speed limit w danym miejscu?
        if(Settings.RAMP_SLIGHT<0) Settings.RAMP_SLIGHT = 50.0; //0.75 * speed limit w danym miejscu?
        if(Settings.STRAIGHT<0) Settings.STRAIGHT = 70.0; //speed limit w danym miejscu?
        if(Settings.FORK<0) Settings.FORK = 60.0; //0.75 * speed limit w danym miejscu?
        if(Settings.FERRY<0) Settings.FERRY = 0.0;
        if(Settings.KEEP<0) Settings.KEEP = 70.0; //speed limit w danym miejscu?
    }


    public void saveValues()
    {
        mSavedDataManager.setMagicNumberValue((double) Settings.MIN_DISTANCE, "MIN_DISTANCE");
        mSavedDataManager.setMagicNumberValue((double) Settings.MAX_DISTANCE, "MAX_DISTANCE");
        mSavedDataManager.setMagicNumberValue((double) Settings.VARIANT, "VARIANT");
        mSavedDataManager.setMagicNumberValue( Settings.CAR_WEIGHT, "CAR_WEIGHT");
        mSavedDataManager.setMagicNumberValue( Settings.DECELERATION_PROFILE, "DECELERATION_PROFILE");
        mSavedDataManager.setMagicNumberValue( Settings.FIXED_ACCELERATION, "FIXED_ACCELERATION");
        mSavedDataManager.setMagicNumberValue( Settings.TURN_SHARP, "TURN_SHARP");
        mSavedDataManager.setMagicNumberValue( Settings.UTURN, "UTURN");
        mSavedDataManager.setMagicNumberValue( Settings.TURN_SLIGHT, "TURN_SLIGHT");
        mSavedDataManager.setMagicNumberValue( Settings.MERGE, "MERGE");
        mSavedDataManager.setMagicNumberValue( Settings.MERGE_SHARP, "MERGE_SHARP");
        mSavedDataManager.setMagicNumberValue( Settings.MERGE_SLIGHT, "MERGE_SLIGHT");
        mSavedDataManager.setMagicNumberValue( Settings.MERGE_STRAIGHT, "MERGE_STRAIGHT");
        mSavedDataManager.setMagicNumberValue( Settings.ROUNDABOUT, "ROUNDABOUT");
        mSavedDataManager.setMagicNumberValue( Settings.TURN, "TURN");
        mSavedDataManager.setMagicNumberValue( Settings.RAMP, "RAMP");
        mSavedDataManager.setMagicNumberValue( Settings.RAMP_SHARP, "RAMP_SHARP");
        mSavedDataManager.setMagicNumberValue( Settings.RAMP_SLIGHT, "RAMP_SLIGHT");
        mSavedDataManager.setMagicNumberValue( Settings.RAMP_STRAIGHT, "RAMP_STRAIGHT");
        mSavedDataManager.setMagicNumberValue( Settings.STRAIGHT, "STRAIGHT");
        mSavedDataManager.setMagicNumberValue( Settings.FORK, "FORK");
        mSavedDataManager.setMagicNumberValue( Settings.FERRY, "FERRY");
        mSavedDataManager.setMagicNumberValue( Settings.KEEP, "KEEP");
    }
}
