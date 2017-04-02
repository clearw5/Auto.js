package com.stardust.pio;

/**
 * Created by Stardust on 2017/4/1.
 */

public class PFile {

    public static PFile open(String path, String mode) {
        switch (mode){
            case "r":
                return new PReadableFile(path);
            case "w":
                return new PWritableFile();
        }
        return null;
    }

    public static void create(String path){

    }

    public static void createIfNotExists(String path){

    }

    public static void delete(String path){

    }

}
