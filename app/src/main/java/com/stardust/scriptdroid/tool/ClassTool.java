package com.stardust.scriptdroid.tool;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ClassTool {

    public static void loadClass(Class c) {
        try {
            Class.forName(c.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadClasses(Class... classes) {
        for (Class c : classes) {
            loadClass(c);
        }
    }
}
