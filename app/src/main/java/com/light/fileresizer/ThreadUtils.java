package com.light.fileresizer;

import android.app.Activity;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThreadUtils {
    public static void updateText(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = activity.findViewById(R.id.output);
                text.setText(message);
            }
        });
    }

    private static void getFilesRecursive(File pFile, List<File> allImages) {
        for (File files : Objects.requireNonNull(pFile.listFiles())) {
            if (files.isDirectory()) {
                getFilesRecursive(files, allImages);
            } else {
                if (isImage(files.getName())) {
                    allImages.add(files);
                }
            }
        }
    }

    public static List<String> getAllImages(String basePath) throws IOException {
        List<String> results = new ArrayList<>();


        List<File> files = new ArrayList<>();
        getFilesRecursive(new File(basePath), files);
        for (File file : files) {
            if (file.isFile()) {
                String currPath = file.getPath();
                System.out.println("Images: " + currPath);
                results.add(currPath);
            }
        }
        return results;

//        String projectDirAbsolutePath = Paths.get(basePath).toAbsolutePath().toString();
//        Path resourcesPath = Paths.get(projectDirAbsolutePath, ".");
//        try {
//            List<Path> localFiles = Files.list(resourcesPath)
//                    .collect(Collectors.toList());
//            localFiles.forEach(System.out::println);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return Files.walk(Paths.get(basePath))
//                .filter(Files::isRegularFile)
//                .filter(item -> isImage(item.toString()))
//                .collect(Collectors.toList());
    }

    private static boolean isImage(String fileName) {
        String fileNameLower = fileName.toLowerCase();
        return fileNameLower.endsWith(".png") || fileNameLower.endsWith(".jpg") || fileNameLower.endsWith(".jpeg");
    }

    public static String getTachiyomiPath() {
        File dataDir = Environment.getExternalStorageDirectory();
        return dataDir.getPath() + "/Tachiyomi";
    }

    public static void updateBar(final Activity activity, final int max, final int current) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar bar = activity.findViewById(R.id.progressBar);
                bar.setMax(max);
                bar.setProgress(current);
            }
        });
    }

    public static void setButtonSplit(final Activity activity, final boolean enabled) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = activity.findViewById(R.id.split);
                button.setEnabled(enabled);
            }
        });
    }

    public static void setButtonSafeSplit(final Activity activity, final boolean enabled) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = activity.findViewById(R.id.safesplit);
                button.setEnabled(enabled);
            }
        });
    }

    public static void setButtonFolderSplit(final Activity activity, final boolean enabled) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = activity.findViewById(R.id.foldersplit);
                button.setEnabled(enabled);
            }
        });
    }

    public static void delete(String path) {
        System.out.println("Deleting " + path);
        try {
            new File(path).delete();
        } catch (Exception e) {
            System.out.println("Failed to delete" + e);
        }

    }

    public static int getHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
