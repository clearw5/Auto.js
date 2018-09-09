package com.stardust.autojs.rhino;

import com.android.dx.command.dexer.Main;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;

import org.mozilla.javascript.GeneratedClassLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by Stardust on 2017/4/5.
 */

public class AndroidClassLoader extends ClassLoader implements GeneratedClassLoader {


    private final ClassLoader parent;
    private List<DexClassLoader> mDexClassLoaders;
    private final File dexFile;
    private final File odexOatFile;
    private final File classFile;
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
        mDexClassLoaders = new ArrayList<>();
        dexFile = new File(dir, "dex-" + hashCode() + ".jar");
        odexOatFile = new File(dir, "odex_oat-" + hashCode() + ".tmp");
        classFile = new File(dir, "class-" + hashCode() + ".jar");
        dir.mkdirs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> defineClass(String name, byte[] data) {
        try {
            final ZipFile zipFile = new ZipFile(classFile);
            final ZipParameters parameters = new ZipParameters();
            parameters.setFileNameInZip(name.replace('.', '/') + ".class");
            parameters.setSourceExternalStream(true);
            zipFile.addStream(new ByteArrayInputStream(data), parameters);
            return dexJar().loadClass(name);
        } catch (IOException | ZipException | ClassNotFoundException e) {
            throw new FatalLoadingException(e);
        } finally {
            dexFile.delete();
            odexOatFile.delete();
        }
    }

    public void loadJar(File jar) throws IOException {
        try {
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
            dexJar();
        } catch (ZipException e) {
            IOException exception = new IOException();
            //noinspection UnnecessaryInitCause
            exception.initCause(e);
            throw exception;
        }
    }

    public DexClassLoader loadDex(File file) throws IOException {
        DexClassLoader loader = new DexClassLoader(file.getPath(), mCacheDir.getPath(), null, parent);
        mDexClassLoaders.add(loader);
        return loader;
    }

    private DexClassLoader dexJar() throws IOException {
        if (!classFile.exists()) {
            classFile.createNewFile();
        }
        final Main.Arguments arguments = new Main.Arguments();
        arguments.fileNames = new String[]{classFile.getPath()};
        arguments.outName = dexFile.getPath();
        arguments.jarOutput = true;
        Main.run(arguments);
        return loadDex(dexFile);
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
