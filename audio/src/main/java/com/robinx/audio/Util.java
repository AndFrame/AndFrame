package com.robinx.audio;

import android.Manifest;
import android.app.Activity;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Util {

    public static void requestPermissions(Activity activity, String[] permissions) {
        if (permissions == null || permissions.length == 0) return;
        ActivityCompat.requestPermissions(activity, permissions, 0);
    }

    public static void requestSDCardPermission(Activity activity) {
        requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    public static void writeFile(File file, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}