package com.stardust.scriptdroid.external.floating_window.menu.record.inputevent;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;


/**
 * Created by Stardust on 2017/3/7.
 */

public class InputEventToSendEventConverter extends InputEventConverter {

    private static final DecimalFormat DELAY_FORMAT = new DecimalFormat("#.###");

    private double mLastEventTime;
    private StringBuilder mSendEventCommands = new StringBuilder();

    @Override
    public void convertEvent(@NonNull Event event) {
        if (mLastEventTime == 0) {
            mLastEventTime = event.time;
        } else if (event.time - mLastEventTime > 0.1) {
            mSendEventCommands.append("sleep ").append(DELAY_FORMAT.format(event.time - mLastEventTime)).append("\n");
            mLastEventTime = event.time;
        }
        mSendEventCommands.append("sendevent ")
                .append(event.device).append(" ")
                .append(hex2dec(event.type)).append(" ")
                .append(hex2dec(event.code)).append(" ")
                .append(hex2dec(event.value)).append("\n");

    }

    @Override
    public String getGetEventCommand() {
        return "getevent -t";
    }

    public String getCode() {
        return mSendEventCommands.toString();
    }

    private static String hex2dec(String hex) {
        try {
            return String.valueOf((int) Long.parseLong(hex, 16));
        } catch (NumberFormatException e) {
            throw new EventFormatException(e);
        }
    }

}
