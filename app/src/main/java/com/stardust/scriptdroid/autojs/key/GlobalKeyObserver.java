package com.stardust.scriptdroid.autojs.key;

import android.util.Log;
import android.view.KeyEvent;

import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.core.inputevent.ShellKeyObserver;
import com.stardust.event.EventDispatcher;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.OnKeyListener;

/**
 * Created by Stardust on 2017/8/14.
 */

public class GlobalKeyObserver implements OnKeyListener, ShellKeyObserver.KeyListener {


    public interface OnVolumeDownListener {
        void onVolumeDown();
    }

    private static final EventDispatcher.Event<OnVolumeDownListener> VOLUME_DOWN_EVENT = OnVolumeDownListener::onVolumeDown;
    private static final String LOG_TAG = "GlobalKeyObserver";
    private static final long EVENT_TIMEOUT = 200;
    private static GlobalKeyObserver sSingleton = new GlobalKeyObserver();
    private EventDispatcher<OnVolumeDownListener> mVolumeDownEventDispatcher = new EventDispatcher<>();
    private boolean mVolumeDownFromShell, mVolumeDownFromAccessibility;
    private boolean mVolumeUpFromShell, mVolumeUpFromAccessibility;

    GlobalKeyObserver() {
        AccessibilityService.getStickOnKeyObserver()
                .addListener(this);
        ShellKeyObserver observer = new ShellKeyObserver();
        observer.setKeyListener(this);
        InputEventObserver.getGlobal().addListener(observer);
    }

    public static GlobalKeyObserver getSingleton() {
        return sSingleton;
    }

    public static void init() {
        //do nothing
    }

    public void onVolumeUp() {
        Log.d(LOG_TAG, "onVolumeUp at " + System.currentTimeMillis());
        if (Pref.isRunningVolumeControlEnabled()) {
            AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
        }
    }

    public void onVolumeDown() {
        Log.d(LOG_TAG, "onVolumeDown at " + System.currentTimeMillis());
        mVolumeDownEventDispatcher.dispatchEvent(VOLUME_DOWN_EVENT);
    }

    public void addVolumeDownListener(OnVolumeDownListener listener) {
        mVolumeDownEventDispatcher.addListener(listener);
    }

    public boolean removeVolumeDownListener(OnVolumeDownListener listener) {
        return mVolumeDownEventDispatcher.removeListener(listener);
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
