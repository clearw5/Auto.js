package com.stardust.scriptdroid.record.inputevent;

import com.stardust.scriptdroid.Pref;

/**
 * Created by Stardust on 2017/3/16.
 */

public class TouchRecorder extends InputEventRecorder implements KeyObserver.KeyListener {

    public TouchRecorder() {
        super(new InputEventToSendEventJsConverter());
        listen();
    }


    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void onKeyDown(String keyName) {

    }

    @Override
    public void onKeyUp(String keyName) {

    }

}
