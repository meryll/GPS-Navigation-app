package tech.hypermiles.hypermiles.Middleware;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.javaxinject.Singleton;

import java.util.Date;

import tech.hypermiles.hypermiles.Other.Settings;


/**
 * Created by Asia on 2017-02-02.
 */
public class Logger {

    public final static String FILE_PATH = "/sdcard/hypermiles";

    public final static String FILE_NAME = "_logs.txt";
    public final static String DATA_FILE_NAME = "_data.txt";

    private static String mStartDate;

    public static void initialize()
    {
        mStartDate = (new Date()).toString();
    }

    public static void e(String tag, String message)
    {
        if(message== null) {
            return;
        }
        Log.e(tag, message);
        writeToLogFile("Error "+tag+" "+message);
    }

    public static void w(String tag, String message)
    {
        Log.w(tag, message);
        writeToLogFile("Warning "+tag+" "+message);
    }

    public static void i(String tag, String message)
    {
        Log.i(tag, message);
    }

    public static void d(String tag, String message)
    {
        Log.d(tag, message);
        writeToLogFile("Debug "+tag+" "+message);

    }

    public static void wtf(String tag, String message)
    {
        Log.wtf(tag, message);
        writeToLogFile("Wtf "+tag+" "+message);
    }

    public static void log(LatLng location, double speed, int legNumber, int stepInLegNumber, int pointInStepNumber, int maneuver, Boolean shouldBeBraking)
    {
        String separator = ";";
        String lineSeparator = "\r\n";
        String message = new Date()+separator+location+separator+speed+separator+legNumber+separator+stepInLegNumber+separator+pointInStepNumber+separator+maneuver+separator+shouldBeBraking+lineSeparator;

        writeToDataFile(message);
    }

    private static void writeToLogFile(String message) {

        String fileName = mStartDate+FILE_NAME;
        String fullMessage = new Date()+" "+message+"\r\n";
        writeToFile(fullMessage, fileName);
    }

    private static void writeToDataFile(String message) {

        String fileName = mStartDate+DATA_FILE_NAME;
        writeToFile(message, fileName);
    }

    private static void writeToFile(String fullMessage, String fileName) {

        if(!Settings.SAVE_DATA) return;

        FileManager.write(FILE_PATH, fileName, fullMessage);
    }
}
