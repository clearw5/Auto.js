package com.stardust.scriptdroid.external.floatingwindow.menu.record.inputevent;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Stardust on 2017/5/4.
 */

public class KeyObserver {

    public interface KeyListener {

        void onKeyDown(String keyName);

        void onKeyUp(String keyName);

    }

    private InputEventRecorder mObserver;
    private KeyListener mKeyListener;

    public KeyObserver(Context context) {
        mObserver = new InputEventRecorder(context, new InputEventConverter() {
            @Override
            public void convertEvent(@NonNull Event event) {
                if (event.value.equalsIgnoreCase("UP")) {
                    notifyKeyUp(event.code);
                }
                if (event.value.equalsIgnoreCase("DOWN")) {
                    notifyKeyDown(event.code);
                }
            }

            @Override
            public String getCode() {
                return null;
            }
        });
    }

    public void setKeyListener(KeyListener keyListener) {
        mKeyListener = keyListener;
    }

    public void startListening() {
        mObserver.listen();
        mObserver.start();
    }

    public void stopListening() {
        mObserver.stopImpl();
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

}
