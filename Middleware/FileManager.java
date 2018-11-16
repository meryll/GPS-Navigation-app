package tech.hypermiles.hypermiles.Middleware;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Asia on 2017-02-02.
 */

public class FileManager {

    public static final String TAG = "FileManager";

    public static void write(String filePath, String fileName, String message) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.append(message);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
