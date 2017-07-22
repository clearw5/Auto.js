package com.stardust.autojs.runtime.record.inputevent;

import android.content.Context;

/**
 * Created by Stardust on 2017/3/16.
 */

public class TouchRecorder extends InputEventRecorder {

    public TouchRecorder(Context context) {
        super(context, new InputEventToSendEventJsConverter());
        listen();
    }


    @Override
    public void stop() {
        super.stop();
    }

}
