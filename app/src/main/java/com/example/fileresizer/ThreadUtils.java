package com.example.fileresizer;

import android.app.Activity;
import android.os.Environment;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ThreadUtils {
    public static void updateText(Activity activity, String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = activity.findViewById(R.id.output);
                text.setText(message);
            }
        });
    }

    public static List<Path> getAllImages(String basePath) throws IOException {
        String projectDirAbsolutePath = Paths.get(basePath).toAbsolutePath().toString();
        Path resourcesPath = Paths.get(projectDirAbsolutePath, ".");
        try {
            List localFiles = Files.list(resourcesPath)
                    .collect(Collectors.toList());
            localFiles.stream().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Files.walk(Paths.get(basePath))
                .filter(item -> Files.isRegularFile(item))
                .filter(item -> isImage(item.toString()))
                .collect(Collectors.toList());
    }

    private static boolean isImage(String fileName) {
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif");
    }

    public static String getTachiyomiPath() {
        File dataDir = Environment.getExternalStorageDirectory();
        return dataDir.getPath() + "/Tachiyomi";
    }

    public static void updateBar(Activity activity, int max, int current) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar bar = activity.findViewById(R.id.progressBar);
                bar.setMax(max);
                bar.setProgress(current);
            }
        });
    }

    public static void setButtonResize(Activity activity, boolean enabled) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = activity.findViewById(R.id.resize);
                button.setEnabled(enabled);
            }
        });
    }

    public static void setButtonClean(Activity activity, boolean enabled) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = activity.findViewById(R.id.clean);
                button.setEnabled(enabled);
            }
        });
    }

    public static void setButtonSplit(Activity activity, boolean enabled) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = activity.findViewById(R.id.split);
                button.setEnabled(enabled);
            }
        });
    }

    public static void delete(Path path) {
        System.out.println("Deleting " + path.toString());
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
