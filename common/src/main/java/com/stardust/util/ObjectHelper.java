package com.stardust.util;

public class ObjectHelper {

    public static void requireNonNull(Object obj, String name){
        if(obj == null){
            throw new NullPointerException(name + " should not be null");
        }
    }

    public static void requireNonNull(Object obj){
        if(obj == null){
            throw new NullPointerException();
        }
    }


}
