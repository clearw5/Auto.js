package com.stardust.scriptdroid.droid;

/**
 * Created by Stardust on 2017/1/22.
 */
public class StringTool {
    public static String removeDoubleQuotes(String str) {
        return str.substring(1, str.length() - 1);
    }
}
