package com.stardust.autojs.runtime.record.inputevent;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.stardust.autojs.runtime.record.Recorder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/3/7.
 */

public abstract class InputEventConverter {

    static class Event {

        static final Pattern PATTERN = Pattern.compile("^\\[([^\\]]*)\\]\\s+([^:]*):\\s+([^\\s]*)\\s+([^\\s]*)\\s+([^\\s]*)\\s*$");

        static Event parseEvent(String eventStr) {
            Matcher matcher = Event.PATTERN.matcher(eventStr);
            if (!matcher.matches()) {
                throw new EventFormatException(eventStr);
            }
            double time;
            try {
                time = Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                throw new EventFormatException(eventStr, e);
            }
            return new Event(time, matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
        }


        double time;
        String device;
        String type;
        String code;
        String value;

        public Event(double time, String device, String type, String code, String value) {
            this.time = time;
            this.device = device;
            this.type = type;
            this.code = code;
            this.value = value;
        }


        @Override
        public String toString() {
            return "Event{" +
                    "time=" + time +
                    ", device='" + device + '\'' +
                    ", type='" + type + '\'' +
                    ", code='" + code + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }


    private final static Pattern LAST_INT_PATTERN = Pattern.compile("[^0-9]+([0-9]+)$");


    protected boolean mConverting = false;
    private int mState = Recorder.STATE_NOT_START;

    public void convertEventIfFormatCorrect(String eventStr) {
        if (!mConverting)
            return;
        if (TextUtils.isEmpty(eventStr) || !eventStr.startsWith("["))
            return;
        Event event = parseEventOrNull(eventStr);
        if (event != null) {
            convertEvent(event);
        }
    }

    public abstract void convertEvent(@NonNull Event event);

    public String getGetEventCommand() {
        return "getevent -t -l";
    }


    public void start() {
        mConverting = true;
        mState = Recorder.STATE_RECORDING;
    }

    public void resume() {
        mConverting = true;
        mState = Recorder.STATE_RECORDING;
    }

    public void pause() {
        mConverting = false;
        mState = Recorder.STATE_PAUSED;
    }

    public void stop() {
        mConverting = false;
        mState = Recorder.STATE_STOPPED;
    }

    public abstract String getCode();

    private boolean mFirstEventFormatError = true;

    public Event parseEventOrNull(String eventStr) {
        try {
            return Event.parseEvent(eventStr);
        } catch (EventFormatException e) {
            e.printStackTrace();
            if (mFirstEventFormatError) {
                mFirstEventFormatError = false;
            }
            return null;
        }

    }

    public static int parseDeviceNumber(String device) {
        Matcher matcher = LAST_INT_PATTERN.matcher(device);
        if (matcher.find()) {
            String someNumberStr = matcher.group(1);
            return Integer.parseInt(someNumberStr);
        }
        return -1;
    }


}
