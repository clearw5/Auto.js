package com.stardust.autojs.rhino;

import android.util.Log;

import com.android.dx.command.dexer.Main;
import com.stardust.pio.PFiles;
import com.stardust.util.MD5;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;

import org.mozilla.javascript.GeneratedClassLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by Stardust on 2017/4/5.
 */

public class AndroidClassLoader extends ClassLoader implements GeneratedClassLoader {


    private static final String LOG_TAG = "AndroidClassLoader";
    private final ClassLoader parent;
    private final List<DexClassLoader> mDexClassLoaders = new ArrayList<>();
    private final File mCacheDir;

    /**
     * Create a new instance with the given parent classloader and cache dierctory
     *
     * @param parent the parent
     * @param dir    the cache directory
     */
    public AndroidClassLoader(ClassLoader parent, File dir) {
        this.parent = parent;
        mCacheDir = dir;
        if (dir.exists()) {
            PFiles.deleteFilesOfDir(dir);
        } else {
            dir.mkdirs();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> defineClass(String name, byte[] data) {
        Log.d(LOG_TAG, "defineClass: name = " + name + " data.length = " + data.length);
        File classFile = null;
        try {
            classFile = generateTempFile(name, false);
            final ZipFile zipFile = new ZipFile(classFile);
            final ZipParameters parameters = new ZipParameters();
            parameters.setFileNameInZip(name.replace('.', '/') + ".class");
            parameters.setSourceExternalStream(true);
            zipFile.addStream(new ByteArrayInputStream(data), parameters);
            return dexJar(classFile, null).loadClass(name);
        } catch (IOException | ZipException | ClassNotFoundException e) {
            throw new FatalLoadingException(e);
        } finally {
            if (classFile != null) {
                classFile.delete();
            }
        }
    }

    private File generateTempFile(String name, boolean create) throws IOException {
        File file = new File(mCacheDir, name.hashCode() + System.currentTimeMillis() + ".jar");
        if (create) {
            if (!file.exists()) {
                file.createNewFile();
            }
        } else {
            file.delete();
        }
        return file;
    }

    public void loadJar(File jar) throws IOException {
        Log.d(LOG_TAG, "loadJar: jar = " + jar);
        if (!jar.exists() || !jar.canRead()) {
            throw new FileNotFoundException("File does not exist or readable: " + jar.getPath());
        }
        File dexFile = new File(mCacheDir, generateDexFileName(jar));
        if (dexFile.exists()) {
            loadDex(dexFile);
            return;
        }
        try {
            final File classFile = generateTempFile(jar.getPath(), false);
            final ZipFile zipFile = new ZipFile(classFile);
            final ZipFile jarFile = new ZipFile(jar);
            //noinspection unchecked
            for (FileHeader header : (List<FileHeader>) jarFile.getFileHeaders()) {
                if (!header.isDirectory()) {
                    final ZipParameters parameters = new ZipParameters();
                    parameters.setFileNameInZip(header.getFileName());
                    parameters.setSourceExternalStream(true);
                    zipFile.addStream(jarFile.getInputStream(header), parameters);
                }
            }
            dexJar(classFile, dexFile);
            classFile.delete();
        } catch (ZipException e) {
            throw new IOException(e);
        }
    }

    private String generateDexFileName(File jar) {
        String message = jar.getPath() + "_" + jar.lastModified();
        return MD5.md5(message);
    }

    public DexClassLoader loadDex(File file) throws FileNotFoundException {
        Log.d(LOG_TAG, "loadDex: file = " + file);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath());
        }
        DexClassLoader loader = new DexClassLoader(file.getPath(), mCacheDir.getPath(), null, parent);
        mDexClassLoaders.add(loader);
        return loader;
    }

    private DexClassLoader dexJar(File classFile, File dexFile) throws IOException {
        final Main.Arguments arguments = new Main.Arguments();
        arguments.fileNames = new String[]{classFile.getPath()};
        boolean isTmpDex = dexFile == null;
        if (isTmpDex) {
            dexFile = generateTempFile("dex-" + classFile.getPath(), true);
        }
        arguments.outName = dexFile.getPath();
        arguments.jarOutput = true;
        Main.run(arguments);
        DexClassLoader loader = loadDex(dexFile);
        if (isTmpDex) {
            dexFile.delete();
        }
        return loader;
    }

    /**
     * Does nothing
     *
     * @param aClass ignored
     */
    @Override
    public void linkClass(Class<?> aClass) {
        //doesn't make sense on android
    }

    /**
     * Try to load a class. This will search all defined classes, all loaded jars and the parent class loader.
     *
     * @param name    the name of the class to load
     * @param resolve ignored
     * @return the class
     * @throws ClassNotFoundException if the class could not be found in any of the locations
     */
    @Override
    public Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass == null) {
            for (DexClassLoader dex : mDexClassLoaders) {
                loadedClass = dex.loadClass(name);
                if (loadedClass != null) {
                    break;
                }
            }
            if (loadedClass == null) {
                loadedClass = parent.loadClass(name);
            }
        }
        return loadedClass;
    }

    /**
     * Might be thrown in any Rhino method that loads bytecode if the loading failed
     */
    public static class FatalLoadingException extends RuntimeException {
        FatalLoadingException(Throwable t) {
            super("Failed to define class", t);
        }
    }
}
