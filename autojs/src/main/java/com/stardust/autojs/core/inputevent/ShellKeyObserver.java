package com.stardust.autojs.core.inputevent;

import androidx.annotation.NonNull;
import android.util.SparseArray;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/4.
 */

public class ShellKeyObserver implements InputEventObserver.InputEventListener {

    public interface KeyListener {

        void onKeyDown(String keyName);

        void onKeyUp(String keyName);

    }

    private static final Map<String, Integer> keyNameToCode = new HashMap<>();
    private static final SparseArray<String> keyCodeToName = new SparseArray<>();

    private KeyListener mKeyListener;

    public ShellKeyObserver() {

    }

    public void setKeyListener(KeyListener keyListener) {
        mKeyListener = keyListener;
    }

    @Override
    public void onInputEvent(@NonNull InputEventObserver.InputEvent event) {
        if (!event.type.equals("0001")) {
            return;
        }
        if (event.value.equalsIgnoreCase("00000000")) {
            notifyKeyUp(keyCodeToKeyName(Integer.parseInt(event.code, 16)));
        }
        if (event.value.equalsIgnoreCase("00000001")) {
            notifyKeyDown(keyCodeToKeyName(Integer.parseInt(event.code, 16)));
        }
    }

    public static String keyCodeToKeyName(int code) {
        return keyCodeToName.get(code);
    }


    public static int keyNameToCode(String name) {
        Integer code = keyNameToCode.get(name);
        if (code == null)
            return -1;
        return code;
    }

    private void notifyKeyDown(String keyName) {
        if (mKeyListener != null) {
            mKeyListener.onKeyDown(keyName);
        }
    }

    private void notifyKeyUp(String keyName) {
        if (mKeyListener != null) {
            mKeyListener.onKeyUp(keyName);
        }
    }

    static {
        try {
            for (Field field : InputEventCodes.class.getFields()) {
                if (field.getName().startsWith("KEY_")) {
                    int keyCode = (int) field.get(null);
                    keyCodeToName.put(keyCode, field.getName());
                    keyNameToCode.put(field.getName(), keyCode);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
