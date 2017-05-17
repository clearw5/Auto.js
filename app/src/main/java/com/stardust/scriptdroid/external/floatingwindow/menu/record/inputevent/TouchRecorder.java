package com.stardust.scriptdroid.external.floatingwindow.menu.record.inputevent;

/**
 * Created by Stardust on 2017/3/16.
 */

public class TouchRecorder extends InputEventRecorder {

    public TouchRecorder() {
        super(new InputEventToSendEventJsConverter());
        listen();
    }


    @Override
    public void stop() {
        super.stop();
    }

}
