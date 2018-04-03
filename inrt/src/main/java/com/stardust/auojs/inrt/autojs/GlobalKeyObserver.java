package com.stardust.auojs.inrt.autojs;

import android.util.Log;
import android.view.KeyEvent;

import com.stardust.app.GlobalAppContext;
import com.stardust.auojs.inrt.Pref;
import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.core.inputevent.ShellKeyObserver;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.OnKeyListener;

/**
 * Created by Stardust on 2017/8/14.
 */

public class GlobalKeyObserver implements OnKeyListener, ShellKeyObserver.KeyListener {


    private static final String LOG_TAG = "GlobalKeyObserver";
    private static GlobalKeyObserver sSingleton = new GlobalKeyObserver();
    private boolean mVolumeDownFromShell, mVolumeDownFromAccessibility;
    private boolean mVolumeUpFromShell, mVolumeUpFromAccessibility;

    GlobalKeyObserver() {
        AccessibilityService.getStickOnKeyObserver()
                .addListener(this);
        ShellKeyObserver observer = new ShellKeyObserver();
        observer.setKeyListener(this);
        InputEventObserver.getGlobal(GlobalAppContext.get()).addListener(observer);
    }

    public static void init() {
        //do nothing
    }

    public void onVolumeUp() {
        Log.d(LOG_TAG, "onVolumeUp at " + System.currentTimeMillis());
        if (Pref.shouldStopAllScriptsWhenVolumeUp()) {
            AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
        }
    }

    @Override
    public void onKeyEvent(int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP)
            return;
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (mVolumeDownFromShell) {
                mVolumeDownFromShell = false;
                return;
            }
            mVolumeUpFromAccessibility = true;
            onVolumeDown();
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (mVolumeUpFromShell) {
                mVolumeUpFromShell = false;
                return;
            }
            mVolumeUpFromAccessibility = true;
            onVolumeUp();
        }
    }

    public void onVolumeDown() {

    }


    @Override
    public void onKeyDown(String keyName) {

    }

    @Override
    public void onKeyUp(String keyName) {
        if ("KEY_VOLUMEUP".equals(keyName)) {
            if (mVolumeUpFromAccessibility) {
                mVolumeUpFromAccessibility = false;
                return;
            }
            mVolumeUpFromShell = true;
            onVolumeUp();
        } else if ("KEY_VOLUMEDOWN".equals(keyName)) {
            if (mVolumeDownFromAccessibility) {
                mVolumeDownFromAccessibility = false;
                return;
            }
            mVolumeDownFromShell = true;
            onVolumeDown();
        }
    }
}
