package com.stardust.util;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Stardust on 2017/9/26.
 */

public class HashUtils {

    public static String md5(String text) {
        MessageDigest md;
        byte[] bytesOfMessage = text.getBytes();
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] thedigest = md.digest(bytesOfMessage);
        return Base64.encodeToString(thedigest, Base64.DEFAULT);
    }
}
