package com.stardust.pio;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.provider.MediaStore;

import com.stardust.util.Func1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Created by Stardust on 2017/4/1.
 */

public class PFile {

    private static final String TAG = "PFile";

    static final int DEFAULT_BUFFER_SIZE = 8192;
    static final String DEFAULT_ENCODING = Charset.defaultCharset().name();

    public static PFile open(String path, String mode, String encoding, int bufferSize) {
        switch (mode) {
            case "r":
                return new PReadableTextFile(path, encoding, bufferSize);
            case "w":
                return new PWritableTextFile(path, encoding, bufferSize, false);
            case "a":
                return new PWritableTextFile(path, encoding, bufferSize, true);
        }
        return null;
    }

    public static PFile open(String path, String mode, String encoding) {
        return open(path, mode, encoding, DEFAULT_BUFFER_SIZE);
    }

    public static PFile open(String path, String mode) {
        return open(path, mode, DEFAULT_ENCODING, DEFAULT_BUFFER_SIZE);
    }

    public static PFile open(String path) {
        return open(path, "r", DEFAULT_ENCODING, DEFAULT_BUFFER_SIZE);
    }

    public static boolean create(String path) {
        try {
            return new File(path).createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean createIfNotExists(String path) {
        ensureDir(path);
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

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static boolean ensureDir(String path) {
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

    public static String read(String path, String encoding) {
        return read(new File(path), encoding);
    }

    public static String read(String path) {
        return read(new File(path));
    }


    public static String read(File file, String encoding) {
        try {
            return read(new FileInputStream(file), encoding);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String read(File file) {
        return read(file, "utf-8");
    }

    public static String read(InputStream is, String encoding) {
        try {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes, encoding);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String read(InputStream inputStream) {
        return read(inputStream, "utf-8");
    }

    public static boolean copyRaw(Context context, int rawId, String path) {
        InputStream is = context.getResources().openRawResource(rawId);
        return copyStream(is, path);
    }

    public static boolean copyStream(InputStream is, String path) {
        if (!ensureDir(path))
            return false;
        File file = new File(path);
        try {
            if (!file.exists())
                if (!file.createNewFile())
                    return false;
            FileOutputStream fos = new FileOutputStream(file);
            write(is, fos);
            return true;
        } catch (IOException | UncheckedIOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void write(InputStream is, OutputStream os) {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        try {
            while (is.available() > 0) {
                int n = is.read(buffer);
                os.write(buffer, 0, n);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public static void write(String path, String text) {
        write(new File(path), text);
    }

    public static void write(String path, String text, String encoding) {
        try {
            write(new FileOutputStream(path), text, encoding);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(File file, String text) {
        try {
            write(new FileOutputStream(file), text);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(FileOutputStream fileOutputStream, String text) {
        write(fileOutputStream, text, "utf-8");
    }


    public static void write(OutputStream outputStream, String text, String encoding) {
        try {
            outputStream.write(text.getBytes(encoding));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean copy(String pathFrom, String pathTo) {
        try {
            return copyStream(new FileInputStream(pathFrom), pathTo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyAsset(Context context, String assetFile, String path) {
        try {
            return copyStream(context.getAssets().open(assetFile), path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String renameWithoutExtensionAndReturnNewPath(String path, String newName) {
        File file = new File(path);
        File newFile = new File(file.getParent(), newName + "." + getExtension(file.getName()));
        file.renameTo(newFile);
        return newFile.getAbsolutePath();
    }

    public static boolean renameWithoutExtension(String path, String newName) {
        File file = new File(path);
        File newFile = new File(file.getParent(), newName + "." + getExtension(file.getName()));
        return file.renameTo(newFile);
    }

    public static boolean rename(String path, String newName) {
        File f = new File(path);
        return f.renameTo(new File(f.getParent(), newName));
    }

    public static boolean move(String path, String newPath) {
        File f = new File(path);
        return f.renameTo(new File(newPath));
    }

    public static String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i < 0 || i + 1 >= fileName.length() - 1)
            return "";
        return fileName.substring(i + 1);
    }

    public static String generateNotExistingPath(String path, String extension) {
        if (!new File(path + extension).exists())
            return path + extension;
        int i = 0;
        while (true) {
            String pathI = path + "(" + i + ")" + extension;
            if (!new File(pathI).exists())
                return pathI;
            i++;
        }
    }

    public static String getName(String filePath) {
        return new File(filePath).getName();
    }

    public static String getNameWithoutExtension(String filePath) {
        String fileName = getName(filePath);
        int b = fileName.lastIndexOf('.');
        if (b < 0)
            b = fileName.length();
        fileName = fileName.substring(0, b);
        return fileName;
    }

    public static File copyAssetToTmpFile(Context context, String path) {
        String extension = "." + getExtension(path);
        String name = getNameWithoutExtension(path);
        if (name.length() < 5) {
            name += name.hashCode();
        }
        try {
            File tmpFile = File.createTempFile(name, extension, context.getCacheDir());
            copyAsset(context, path, tmpFile.getPath());
            return tmpFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteRecursively(File file) {
        if (file.isFile())
            return file.delete();
        for (File child : file.listFiles()) {
            if (!deleteRecursively(child))
                return false;
        }
        return file.delete();
    }

    public static boolean remove(String path) {
        return new File(path).delete();
    }

    public static boolean removeDir(String path) {
        return deleteRecursively(new File(path));
    }

    public static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String readAsset(AssetManager assets, String path) {
        try {
            return read(assets.open(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String[] listDir(String path) {
        File file = new File(path);
        return file.list();
    }

    public static String[] listDir(String path, final Func1<String, Boolean> filter) {
        final File file = new File(path);
        return file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return filter.call(name);
            }
        });
    }

    public static boolean isFile(String path) {
        return new File(path).isFile();
    }

    public static boolean isDir(String path) {
        return new File(path).isDirectory();
    }

    public static boolean isEmptyDir(String path) {
        File file = new File(path);
        return file.isDirectory() && file.list().length == 0;
    }

    public static String join(String parent, String child) {
        return new File(parent, child).getPath();
    }

    public static String getHumanReadableSize(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".substring(exp - 1, exp);
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
