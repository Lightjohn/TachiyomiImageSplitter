package com.example.fileresizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static com.example.fileresizer.ThreadUtils.getAllImages;
import static com.example.fileresizer.ThreadUtils.getTachiyomiPath;
import static com.example.fileresizer.ThreadUtils.setButtonResize;
import static com.example.fileresizer.ThreadUtils.updateBar;
import static com.example.fileresizer.ThreadUtils.updateText;

class ResizeThread extends Thread {
    int expectedWidth;
    Activity activity;

    ResizeThread(int expectedWidth, Activity activity) {
        this.expectedWidth = expectedWidth;
        this.activity = activity;
    }



    public void run() {
        try {
            updateText(activity, "Evaluating images to resize.\nPlease wait");

            List<Path> imgPaths = getAllImages(getTachiyomiPath());

            updateText(activity, "Found " + imgPaths.size() + " images.");

            resizeImages(imgPaths);
        } catch (IOException e) {
            System.out.println("ERROR " + e);
        }
    }

    private void resizeImages(List<Path> imagesPath) {
        setButtonResize(activity, false);

        int count = 0;
        int processed = 0;

        for (Path path : imagesPath) {
            String imagePath = path.toString();
            Bitmap image = BitmapFactory.decodeFile(imagePath);
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            if (imageWidth > expectedWidth) {
                double widthRatio = imageWidth / (double) expectedWidth;
                int outputWidth = (int) (imageWidth / widthRatio);
                int outputHeight = (int) (imageHeight / widthRatio);

                String inputSize = imageWidth + "x" + imageHeight;
                String outputSize = outputWidth + "x" + outputHeight;

                String info = String.format(Locale.ENGLISH, "Resizing %s from %s to %s",
                        path.getFileName(),
                        inputSize,
                        outputSize);

                System.out.println(info);
                updateText(activity, info);

                Bitmap resized = Bitmap.createScaledBitmap(image, outputWidth, outputHeight, true);
                try (FileOutputStream out = new FileOutputStream(imagePath)) {
                    resized.compress(Bitmap.CompressFormat.JPEG, 90, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                processed++;
            }
            updateBar(activity, imagesPath.size(), ++count);
        }
        String info = String.format(Locale.ENGLISH, "Process %d images\nResized %d of them", imagesPath.size(), processed);
        updateText(activity, info);
        setButtonResize(activity, true);
    }
}
