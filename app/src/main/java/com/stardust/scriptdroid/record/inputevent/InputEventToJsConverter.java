package com.stardust.scriptdroid.record.inputevent;

import android.support.annotation.NonNull;

import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.util.MapEntries;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/3/7.
 */

public class InputEventToJsConverter extends InputEventConverter {


    interface EventHandler {
        void handle(Event event);
    }

    abstract static class Router implements EventHandler {

        private Map<String, EventHandler> mEventHandlerMap = new HashMap<>();

        EventHandler getHandler(String key) {
            return mEventHandlerMap.get(key);
        }

        Router put(String key, EventHandler handler) {
            mEventHandlerMap.put(key, handler);
            return this;
        }

        @Override
        public void handle(Event event) {
            EventHandler handler = mEventHandlerMap.get(getKey(event));
            if (handler != null) {
                handler.handle(event);
            }
        }

        protected abstract String getKey(Event event);
    }

    private class TypeRouter extends Router {

        TypeRouter() {
            put("EV_ABS", new AbsHandler());
            put("EV_SYN", new SyncHandler());
            put("EV_KEY", new KeyHandler());
        }

        @Override
        protected String getKey(Event event) {
            return event.type;
        }
    }

    private static class Point {
        int x, y;
    }

    private class AbsHandler implements EventHandler {

        @Override
        public void handle(Event event) {
            if (event.code.equals("ABS_MT_POSITION_X")) {
                mTouchPoint.x = Integer.parseInt(event.value, 16);
                mTouchTime = event.time;
            } else if (event.code.equals("ABS_MT_POSITION_Y")) {
                mTouchPoint.y = Integer.parseInt(event.value, 16);
                mTouchTime = event.time;
            }
        }
    }

    private class SyncHandler implements EventHandler {

        @Override
        public void handle(Event event) {
            if (mTouchDown) {
                if (mSwipe) {
                    appendCodeIfNotStopped(event);
                    mSwipe = false;
                } else {
                    mLastTouchPoint.x = mTouchPoint.x;
                    mLastTouchPoint.y = mTouchPoint.y;
                    mSwipe = true;
                }
            }
        }

        private void appendCodeIfNotStopped(Event event) {
            if (!mStarted)
                return;
            long interval = (long) (1000 * (event.time - mTouchTime));
            mCode.append("sh.Swipe(")
                    .append(mLastTouchPoint.x).append(", ").append(mLastTouchPoint.y).append(", ")
                    .append(mTouchPoint.x).append(", ").append(mTouchPoint.y);
            if (interval >= 1) {
                mCode.append(", ").append(interval);
            }
            mCode.append(");\n");
        }
    }

    private class KeyHandler implements EventHandler {

        private Map<String, String> mKeyPressCodeMap = new MapEntries<>(new HashMap<String, String>())
                .entry("KEY_HOME", "Home")
                .entry("KEY_MENU", "Menu")
                .entry("KEY_VOLUMEDOWN", "VolumeDown")
                .entry("KEY_VOLUMEUP", "VolumeUp")
                .entry("KEY_BACK", "Back")
                .map();

        @Override
        public void handle(Event event) {
            if (event.code.equals("BTN_TOUCH")) {
                mTouchDown = event.value.equals("DOWN");
                if (!mTouchDown && mSwipe) {
                    if (mStarted)
                        mCode.append("sh.Tap(").append(mLastTouchPoint.x).append(", ").append(mLastTouchPoint.y).append(");\n");
                    mSwipe = false;
                }
            } else if (event.value.equals("UP")) {
                if (!mStarted && event.code.equals("KEY_VOLUMEUP")) {
                    mStarted = true;
                    notifyRecordStarted();
                } else if (mStarted && event.code.equals("KEY_VOLUMEDOWN")) {
                    mStarted = false;
                    notifyRecordStopped();
                } else {
                    appendKeyPressCode(event);
                }
            }
        }

        private void appendKeyPressCode(Event event) {
            String code = mKeyPressCodeMap.get(event.code);
            if (code != null) {
                mCode.append("sh.").append(code).append("();\n");
            }
        }
    }

    private EventHandler mEventHandler = new TypeRouter();
    private StringBuilder mCode = new StringBuilder().append("var sh = new Shell(true);\n");
    private Point mTouchPoint = new Point(), mLastTouchPoint = new Point();
    private boolean mTouchDown = false, mSwipe = false;
    private double mTouchTime;


    @Override
    public void addEvent(@NonNull Event event) {
        mEventHandler.handle(event);
    }

    @Override
    public void stop() {
        super.stop();
        mCode.append("sh.exitAndWaitFor();");
    }

    public String getCode() {
        return mCode.toString();
    }


}
