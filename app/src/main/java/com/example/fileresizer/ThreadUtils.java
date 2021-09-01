package com.example.fileresizer;

import android.app.Activity;
import android.os.Environment;
import android.util.DisplayMetrics;
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
        activity.runOnUiThread(() -> {
            TextView text = activity.findViewById(R.id.output);
            text.setText(message);
        });
    }

    public static List<Path> getAllImages(String basePath) throws IOException {
        String projectDirAbsolutePath = Paths.get(basePath).toAbsolutePath().toString();
        Path resourcesPath = Paths.get(projectDirAbsolutePath, ".");
        try {
            List<Path> localFiles = Files.list(resourcesPath)
                    .collect(Collectors.toList());
            localFiles.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Files.walk(Paths.get(basePath))
                .filter(Files::isRegularFile)
                .filter(item -> isImage(item.toString()))
                .collect(Collectors.toList());
    }

    private static boolean isImage(String fileName) {
        String fileNameLower = fileName.toLowerCase();
        return fileNameLower.endsWith(".png") || fileNameLower.endsWith(".jpg") || fileNameLower.endsWith(".jpeg");
    }

    public static String getTachiyomiPath() {
        File dataDir = Environment.getExternalStorageDirectory();
        return dataDir.getPath() + "/Tachiyomi";
    }

    public static void updateBar(Activity activity, int max, int current) {
        activity.runOnUiThread(() -> {
            ProgressBar bar = activity.findViewById(R.id.progressBar);
            bar.setMax(max);
            bar.setProgress(current);
        });
    }

//    public static void setButtonResize(Activity activity, boolean enabled) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Button button = activity.findViewById(R.id.resize);
//                button.setEnabled(enabled);
//            }
//        });
//    }

    public static void setButtonClean(Activity activity, boolean enabled) {
        activity.runOnUiThread(() -> {
            Button button = activity.findViewById(R.id.clean);
            button.setEnabled(enabled);
        });
    }

    public static void setButtonSplit(Activity activity, boolean enabled) {
        activity.runOnUiThread(() -> {
            Button button = activity.findViewById(R.id.split);
            button.setEnabled(enabled);
        });
    }

    public static void setButtonSafeSplit(Activity activity, boolean enabled) {
        activity.runOnUiThread(() -> {
            Button button = activity.findViewById(R.id.safesplit);
            button.setEnabled(enabled);
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

    public static int getHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
