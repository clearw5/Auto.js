package com.stardust.scriptdroid.droid.runtime;

/**
 * Created by Stardust on 2017/1/29.
 */
public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException(String message) {
        super(message);
    }
}
