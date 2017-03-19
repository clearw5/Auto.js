package com.stardust.scriptdroid.record.inputevent;

import android.support.annotation.NonNull;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.ui.main.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/3/7.
 */

public abstract class InputEventConverter {


    public static class RecordStateChangeEvent {

        private boolean mIsRecording;

        public RecordStateChangeEvent(boolean isRecording) {
            this.mIsRecording = isRecording;
        }


        public boolean isRecording() {
            return mIsRecording;
        }

    }

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


    protected boolean mStarted = false;

    public void parseAndAddEventIfFormatCorrect(String eventStr) {
        Event event = parseEventOrNull(eventStr);
        if (event != null) {
            addEvent(event);
        }
    }

    public abstract void addEvent(@NonNull Event event);


    public void start() {
        mStarted = true;
    }

    public void pause() {
        mStarted = false;
    }

    public void stop() {
        mStarted = false;
    }

    public abstract String getCode();

    public Event parseEventOrNull(String eventStr) {
        try {
            return Event.parseEvent(eventStr);
        } catch (EventFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void notifyRecordStopped() {
        EventBus.getDefault().post(new RecordStateChangeEvent(false));
    }

    public void notifyRecordStarted() {
        EventBus.getDefault().post(new RecordStateChangeEvent(true));
    }

}
