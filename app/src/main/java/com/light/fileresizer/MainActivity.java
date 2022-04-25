package com.light.fileresizer;

import static com.light.fileresizer.ThreadUtils.getTachiyomiPath;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nononsenseapps.filepicker.FilePickerActivity;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    static final int READ_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String tachiyomiPath = getTachiyomiPath();
        int screenHeight = ThreadUtils.getHeight(MainActivity.this);

        checkPermissions();

        Button split = findViewById(R.id.split);

        split.setOnClickListener(view -> {
            SplitThread thread = new SplitThread(tachiyomiPath, screenHeight, MainActivity.this);
            thread.start();
        });

        Button safeSplit = findViewById(R.id.safesplit);

        safeSplit.setOnClickListener(view -> {
            SplitThread thread = new SplitThread(tachiyomiPath, screenHeight, MainActivity.this);
            thread.setSafe();
            thread.start();
        });

        Button folderSplit = findViewById(R.id.foldersplit);

        folderSplit.setOnClickListener(view -> {

            // Asking user for folder path
            Intent intent = new Intent(getBaseContext(), FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
            intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
            startActivityForResult(intent, READ_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case READ_REQUEST_CODE:
                System.out.println("Test Result URI " + data.getData());
                Uri uri = data.getData();
                String folderPath = uri.getPath();

                String cleanFolderPath = folderPath.replace("/root" , "");
                System.out.println("DEBUG path: " + folderPath + " " + cleanFolderPath);
//                String folderPath = uri.getPath().split(":")[1];

                int screenHeight = ThreadUtils.getHeight(MainActivity.this);
                System.out.println("DEBUG path: " + folderPath);
                SplitThread thread = new SplitThread(cleanFolderPath ,screenHeight, MainActivity.this);
                thread.setSafe();
                thread.start();
                break;
        }
    }


    private void checkPermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Using file access", Toast.LENGTH_LONG).show();
        } else {
            EasyPermissions.requestPermissions(this, "",
                    1, perms);
        }

    }


}