package com.light.fileresizer;

import static com.light.fileresizer.ThreadUtils.getTachiyomiPath;
import static com.light.fileresizer.ThreadUtils.setButtonClean;
import static com.light.fileresizer.ThreadUtils.updateText;

import android.app.Activity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class CleanThread extends Thread {

    Activity activity;
    int count = 0;

    CleanThread(Activity activity) {
        this.activity = activity;
    }

    public void run() {
        updateText(activity, "Cleaning in progress.\nPlease wait");

        cleanNoMediaFiles(getTachiyomiPath());
    }


    private void cleanNoMediaFiles(String basePath) {
        setButtonClean(activity, false);
        count = 0;
        try {
            Files.walk(Paths.get(basePath))
                    .filter(item -> item.getFileName().toString().toLowerCase().compareTo(".nomedia") == 0)
                    .forEach(ThreadUtils::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setButtonClean(activity, true);
        updateText(activity, "Deleted " + count + " nomedia");
    }
}
