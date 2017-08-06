package com.stardust.autojs.core.record.inputevent;

import android.content.Context;

import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.core.record.Recorder;

/**
 * Created by Stardust on 2017/3/16.
 */

public class TouchRecorder extends Recorder.AbstractRecorder {

    private static TouchRecorder sInstance;
    private InputEventRecorder mInputEventRecorder;
    private Context mContext;
    private InputEventObserver mInputEventObserver;

    public TouchRecorder(Context context, InputEventObserver observer) {
        mContext = context;
        mInputEventObserver = observer;
    }

    public TouchRecorder(Context context) {
        mContext = context;
        mInputEventObserver = new InputEventObserver(context);
        mInputEventObserver.observe();
    }

    public static TouchRecorder getGlobal(Context context) {
        if (sInstance == null)
            sInstance = new TouchRecorder(context);
        return sInstance;
    }

    @Override
    protected void startImpl() {
        mInputEventRecorder = new InputEventToAutoFileRecorder(mContext);
        mInputEventObserver.addListener(mInputEventRecorder);
        mInputEventRecorder.start();
    }

    @Override
    protected void pauseImpl() {
        super.pauseImpl();
        mInputEventRecorder.pause();
    }

    @Override
    protected void resumeImpl() {
        super.resumeImpl();
        mInputEventRecorder.resume();
    }

    @Override
    protected void stopImpl() {
        mInputEventRecorder.stop();
        mInputEventObserver.removeListener(mInputEventRecorder);
    }

    @Override
    public String getCode() {
        return mInputEventRecorder.getCode();
    }


    public void reset() {
        setState(STATE_NOT_START);
    }
}
