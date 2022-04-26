package com.light.fileresizer;

import static com.light.fileresizer.ThreadUtils.delete;
import static com.light.fileresizer.ThreadUtils.getAllImages;
import static com.light.fileresizer.ThreadUtils.setButtonFolderSplit;
import static com.light.fileresizer.ThreadUtils.setButtonSafeSplit;
import static com.light.fileresizer.ThreadUtils.setButtonSplit;
import static com.light.fileresizer.ThreadUtils.updateBar;
import static com.light.fileresizer.ThreadUtils.updateText;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class SplitThread extends Thread {
    int SPLIT_THREADS = 4;
    int expectedHeight;
    Activity activity;
    String workingPath;

    SplitThread(String workingPath, int expectedHeight, Activity activity) {
        this.expectedHeight = expectedHeight;
        this.activity = activity;
        this.workingPath = workingPath;
    }

    public void run() {
        try {
            System.out.println("DEBUG: working path: " + this.workingPath);
            updateText(activity, "Evaluating images to resize.\nPlease wait");
            setButtonSplit(activity, false);
            setButtonSafeSplit(activity, false);
            setButtonFolderSplit(activity, false);

            List<String> imgPaths = getAllImages(this.workingPath);
            // Trying to split in //
            List<String> imagesTooBig = new ArrayList<>();
            for (String path : imgPaths) {
                if (shouldBeSpliced(path)) {
                    imagesTooBig.add(path);
                }
            }

            updateText(activity, "Found " + imgPaths.size() + " images.\nSplitting "
                    + imagesTooBig.size() + " images");

            // For the image too big, split 1 by 1
            // First free some memory
            System.gc();

            List<String> imagesFarTooBig = splitImages(imagesTooBig);
            if (SPLIT_THREADS != 1) {
                setSafe();
                updateText(activity, "There was  " + imagesFarTooBig.size() + " Errors.\n" +
                        "Retrying in Safe Mode");
                List<String> imagesThatFailed = splitImages(imagesFarTooBig);
                updateText(activity, "Images that could not be splitted: " + imagesThatFailed.size());
            }
        } catch (IOException e) {
            System.out.println("ERROR " + e);
        } finally {
            setButtonSplit(activity, true);
            setButtonSafeSplit(activity, true);
            setButtonFolderSplit(activity, true);
        }
    }

    public void setSafe() {
        SPLIT_THREADS = 1;
    }

    private int[] getImageSizeWithoutLoading(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);
        int width = options.outWidth;
        int height = options.outHeight;
        return new int[]{width, height};
    }

    private boolean shouldBeSpliced(String path) {
        int[] imageDimension = getImageSizeWithoutLoading(path);
        int imageHeight = imageDimension[1];
        return imageHeight > expectedHeight;

    }

    private Bitmap loadBitmap(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imagePath);
    }

    private void splitOneImage(String imagePath) {
        // At this point we know the image is too big
        Bitmap image = loadBitmap(imagePath);
        int bitmapSizeMb = image.getByteCount() / (1024 * 1024);
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int numImagesToSplitIn = (int) Math.ceil(imageHeight / (double) expectedHeight);

        String inputSize = imageWidth + "x" + imageHeight;

        String info = String.format(Locale.ENGLISH, "Splitting %s\n%s in %d images\nSize: %d MB",
                imagePath,
                inputSize,
                numImagesToSplitIn,
                bitmapSizeMb);

        System.out.println(info);

        updateText(activity, info);

        int baseHeight = imageHeight / numImagesToSplitIn;

        for (int i = 0; i < numImagesToSplitIn; i++) {
            Bitmap resized = Bitmap.createBitmap(image, 0, i * baseHeight, imageWidth, baseHeight);
            String splitImagePath = getSplitImageName(imagePath, i);
            System.out.println(imagePath + " => " + splitImagePath);
            try (FileOutputStream out = new FileOutputStream(splitImagePath)) {
                resized.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(splitImagePath + " written");
        }

        delete(imagePath);
    }

    private List<String> splitImages(final List<String> imagesPath) {
        final int[] count = {0, 0}; // {countDone, countErr}
        final List<String> imagesFarTooBig = new ArrayList<>();
//        int nbCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(SPLIT_THREADS);
        int size = imagesPath.size();
        for (final String path : imagesPath) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        SplitThread.this.splitOneImage(path);
                    } catch (Exception e) {
                        imagesFarTooBig.add(path);
                        count[1]++;
                    }
                    updateBar(activity, imagesPath.size(), ++count[0]);
                }
            });
        }
        updateBar(activity, size, size);    // Making sure that bar is fully loaded
        executorService.shutdown();
        try {
            executorService.awaitTermination(10L, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String errInfo = count[1] == 0 ? "" : String.format(Locale.ENGLISH, "There was %d errors\nPlease re run with SAFE split", count[1]);
        String info = String.format(Locale.ENGLISH, "Sliced %d images\n%s", size, errInfo);
        updateText(activity, info);
        return imagesFarTooBig;
    }

    private String getSplitImageName(String imagePath, int imageNumber) {
        String number = String.format(".%02d", imageNumber);
        String[] extensions = new String[]{".jpg", ".jpeg", ".png", ".gif"};
        for (String extension : extensions) {
            if (imagePath.endsWith(extension)) {
                return imagePath.replace(extension, number + extension);
            }
        }
        throw new IllegalArgumentException("Input image is not a known format: " + imagePath);
    }
}
