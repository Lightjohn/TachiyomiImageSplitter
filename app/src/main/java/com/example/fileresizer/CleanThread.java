package com.example.fileresizer;

import android.app.Activity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.example.fileresizer.ThreadUtils.delete;
import static com.example.fileresizer.ThreadUtils.getTachiyomiPath;
import static com.example.fileresizer.ThreadUtils.setButtonClean;
import static com.example.fileresizer.ThreadUtils.updateText;

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
                    .filter(item -> item.getFileName().toString().compareTo(".nomedia") == 0)
                    .forEach(item -> delete(item));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setButtonClean(activity, true);
        updateText(activity, "Deleted " + count + " nomedia");
    }
}
