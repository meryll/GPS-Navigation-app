package tech.hypermiles.hypermiles.Utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Asia on 2017-05-30.
 */

public class FullScreenUtils {

    public static void setKeepScreenOn(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void clearKeepScreenOn(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
