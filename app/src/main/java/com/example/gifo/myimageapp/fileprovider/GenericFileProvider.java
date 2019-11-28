package com.example.gifo.myimageapp.fileprovider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import com.example.gifo.myimageapp.R;

import java.io.File;

/**
 * Created by gifo on 24.11.2019.
 */

public class GenericFileProvider extends FileProvider {

    // Каталог данных приложения
    public static String MY_APP_DATA_DERICTORY = Environment.getExternalStorageDirectory() + "/";
    private static boolean isDirectoryCreated = false;

    // Создание каталога приложения и данных, прописанный в file_paths.xml
    public static void appDataDirectory(Context context) {
        if (!isDirectoryCreated) {
            XmlResourceParser xmlPath = context.getResources().getXml(R.xml.file_paths);
            try {
                while (true) {
                    if (xmlPath.getName() != null && xmlPath.getName().equals("external-path")) {
                        MY_APP_DATA_DERICTORY += xmlPath.getAttributeValue(1);
                        break;
                    } else xmlPath.next();
                }
            } catch (Exception e) {
            }
            File folder = new File(MY_APP_DATA_DERICTORY.substring(0, MY_APP_DATA_DERICTORY.length()));
            if (!folder.exists()) folder.mkdirs();
            isDirectoryCreated = true;
        }
    }

    // Разрешение на чтение данных
    public static boolean hasWriteDataPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return false;
            } else return true;
        } else return true;
    }

    // Разрешение на запись данных
    public static boolean hasReadDataPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                return false;
            } else return true;
        } else return true;
    }
}
