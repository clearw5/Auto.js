package com.stardust.scriptdroid.file;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.stardust.scriptdroid.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;

/**
 * Created by Stardust on 2017/1/23.
 */

public class FileUtils {

    private static final int BUFFER_SIZE = 1024 * 100;

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
            String folder = path.substring(0, i);
            File file = new File(folder);
            if (file.exists())
                return true;
            return file.mkdirs();
        } else {
            return false;
        }
    }

    public static String readString(File file, String encoding) {
        try {
            return readString(new FileInputStream(file), encoding);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String readString(File file) {
        return readString(file, "utf-8");
    }

    public static String readString(InputStream is, String encoding) {
        try {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes, encoding);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String readString(InputStream inputStream) {
        return readString(inputStream, "utf-8");
    }

    public static boolean copy(int rawId, String path) {
        InputStream is = App.getApp().getResources().openRawResource(rawId);
        return copy(is, path);
    }

    public static boolean copy(InputStream is, String path) {
        if (!ensureFolder(path))
            return false;
        File file = new File(path);
        try {
            if (!file.exists())
                if (!file.createNewFile())
                    return false;
            FileOutputStream fos = new FileOutputStream(file);
            return copy(is, fos);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copy(InputStream is, OutputStream os) {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            while (is.available() > 0) {
                int n = is.read(buffer);
                os.write(buffer, 0, n);
            }
            is.close();
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyAsset(String assetFile, String path) {
        try {
            return copy(App.getApp().getAssets().open(assetFile), path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String renameWithoutExtension(String path, String newName) {
        File file = new File(path);
        File newFile = new File(file.getParent(), newName + "." + getExtension(file.getName()));
        file.renameTo(newFile);
        return newFile.getAbsolutePath();
    }

    public static String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i < 0)
            return "";
        return fileName.substring(i + 1);
    }


}