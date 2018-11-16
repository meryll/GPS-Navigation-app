package tech.hypermiles.hypermiles.Exceptions;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

import tech.hypermiles.hypermiles.Middleware.FileManager;
import tech.hypermiles.hypermiles.Middleware.Logger;

/**
 * Created by Asia on 2017-01-31.
 */

public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    private static String TAG = "DefaultUncaughtExceptionHandler";
    private String localPath = "/sdcard/hypermiles";

    public DefaultUncaughtExceptionHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {

        final Writer stringBuffSync = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringBuffSync);
        e.printStackTrace(printWriter);
        String stacktrace = stringBuffSync.toString();
        printWriter.close();

        if (localPath != null) {
            writeToFile(stacktrace);
        }
        Logger.wtf(TAG, stacktrace);
        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String currentStacktrace) {

        String filename = new Date() + ".txt";
        FileManager.write(localPath, filename, currentStacktrace);
    }

}