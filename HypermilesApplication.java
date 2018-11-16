package tech.hypermiles.hypermiles;

import android.app.Application;

import tech.hypermiles.hypermiles.Middleware.SavedDataManager;

/**
 * Created by Asia on 2017-04-07.
 */

public class HypermilesApplication extends Application {

    private static HypermilesApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized HypermilesApplication getInstance() {
        return mInstance;
    }
}