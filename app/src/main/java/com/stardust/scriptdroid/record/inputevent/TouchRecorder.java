package com.stardust.scriptdroid.record.inputevent;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.record.Recorder;

/**
 * Created by Stardust on 2017/3/16.
 */

public class TouchRecorder extends InputEventToJsRecorder {


    public TouchRecorder() {
        listen();
        setUpTriggers();
    }

    private void setUpTriggers() {
        setStartTriggerKey(Pref.def().getString(App.getResString(R.string.key_start_record_trigger), null));
        setStopTriggerKey(Pref.def().getString(App.getResString(R.string.key_stop_record_trigger), null));
    }


}
