package com.stardust.autojs.runtime.record.inputevent;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Stardust on 2017/7/20.
 */

public class TouchObserver {

    public interface OnTouchEventListener {
        void onTouch(int x, int y);
    }

    private int mTouchX, mTouchY;
    private InputEventRecorder mRecorder;
    private OnTouchEventListener mOnTouchEventListener;

    public TouchObserver(Context context) {
        mRecorder = new InputEventRecorder(context, new TouchEventObserver());
    }

    public void observe() {
        mRecorder.listen();
        mRecorder.start();
    }

    public void stop() {
        mRecorder.stop();
    }

    public void setOnTouchEventListener(OnTouchEventListener onTouchEventListener) {
        mOnTouchEventListener = onTouchEventListener;
    }

    private void onTouch(int x, int y) {
        mTouchX = x;
        mTouchY = y;
        if(mOnTouchEventListener != null){
            mOnTouchEventListener.onTouch(x, y);
        }
    }



    private class TouchEventObserver extends InputEventConverter {

        private int mLastTouchX, mLastTouchY;

        @Override
        public void convertEvent(@NonNull Event event) {
            int device = parseDeviceNumber(event.device);
            int type = (int) Long.parseLong(event.type, 16);
            int code = (int) Long.parseLong(event.code, 16);
            int value = (int) Long.parseLong(event.value, 16);
            if (type != 3) {
                return;
            }
            if (code == 53) {
                onTouchX(value);
                return;
            }
            if (code == 54) {
                onTouchY(value);
                return;
            }
            if (mLastTouchX >= 0) {
                onTouch(mLastTouchX, mTouchY);
                mLastTouchX = -1;
                return;
            }
            if (mLastTouchY >= 0) {
                onTouch(mTouchX, mLastTouchY);
                mLastTouchY = -1;
            }
        }

        private void onTouchX(int value) {
            mLastTouchX = value;
        }

        private void onTouchY(int value) {
            if (mLastTouchX > 0) {
                onTouch(mLastTouchX, value);
                return;
            }
            mLastTouchY = value;
        }

        @Override
        public String getGetEventCommand() {
            return "getevent -t";
        }

        @Override
        public String getCode() {
            return null;
        }
    }

}
