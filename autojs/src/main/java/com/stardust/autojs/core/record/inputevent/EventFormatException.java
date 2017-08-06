package com.stardust.autojs.core.record.inputevent;

/**
 * Created by Stardust on 2017/3/7.
 */

public class EventFormatException extends RuntimeException {


    public static EventFormatException forEventStr(String eventStr, NumberFormatException e) {
        return new EventFormatException(eventStr, e);
    }

    public EventFormatException(Exception e) {
        super(e);
    }

    public EventFormatException(String eventStr, Exception e) {
        super("eventStr=" + eventStr, e);
    }

    public EventFormatException(String eventStr) {
        super("eventStr=" + eventStr);
    }
}