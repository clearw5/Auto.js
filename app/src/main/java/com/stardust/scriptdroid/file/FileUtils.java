package com.stardust.scriptdroid.file;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;

/**
 * Created by Stardust on 2017/1/23.
 */

public class FileUtils {

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getPath(InputStream inputStream) {
        if (inputStream instanceof FileInputStream) {
            FileInputStream fis = (FileInputStream) inputStream;
            try {
                Field field = fis.getClass().getDeclaredField("path");
                field.setAccessible(true);
                return (String) field.get(fis);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean createFileIfNotExists(String path) {
        ensureFolder(path);
        File file = new File(path);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static boolean ensureFolder(String path) {
        int i = path.lastIndexOf("\\");
        if (i < 0)
            i = path.lastIndexOf("/");
        if (i >= 0) {
            String folder = path.substring(i);
            return new File(folder).mkdirs();
        } else {
            return false;
        }
    }

    public static String readString(File file, String encoding) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            return new String(bytes, encoding);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String readString(File file) {
        return readString(file, "utf-8");
    }
}
