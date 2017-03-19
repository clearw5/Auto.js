package com.stardust.scriptdroid.record;

/**
 * Created by Stardust on 2017/3/16.
 */

public interface Recorder {

    interface OnStateChangedListener {

        void onStart();

        void onStop();

        void onPause();

        void onResume();

    }

    OnStateChangedListener NO_OPERATION_LISTENER = new OnStateChangedListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onResume() {

        }
    };

    int STATE_NOT_START = 0;
    int STATE_RECORDING = 1;
    int STATE_PAUSED = 2;
    int STATE_STOPPED = 3;

    void start();

    void stop();

    void pause();

    void resume();

    String getCode();

    int getState();

    void setOnStateChangedListener(OnStateChangedListener onStateChangedListener);

    abstract class DefaultIMPL implements Recorder {


        private OnStateChangedListener mOnStateChangedListener = NO_OPERATION_LISTENER;


        private final boolean mSync;
        private int mState = STATE_NOT_START;

        public DefaultIMPL(boolean syncOfState) {
            mSync = syncOfState;
        }

        public DefaultIMPL() {
            this(false);
        }

        public void start() {
            checkState(STATE_NOT_START);
            setState(STATE_RECORDING);
            startImpl();
            mOnStateChangedListener.onStart();
        }


        private void checkState(int... expectedStates) {
            for (int expectedState : expectedStates) {
                if (mState == expectedState)
                    return;
            }
            throw new IllegalStateException();
        }


        protected abstract void startImpl();

        public void stop() {
            checkState(STATE_RECORDING, STATE_PAUSED);
            setState(STATE_STOPPED);
            stopImpl();
            mOnStateChangedListener.onStop();
        }

        protected abstract void stopImpl();

        public void pause() {
            checkState(STATE_RECORDING);
            setState(STATE_PAUSED);
            pauseImpl();
            mOnStateChangedListener.onPause();
        }

        protected synchronized void setState(int state) {
            if (mSync) {
                synchronized (this) {
                    mState = state;
                }
            } else {
                mState = state;
            }
        }

        public synchronized int getState() {
            if (mSync) {
                synchronized (this) {
                    return mState;
                }
            } else {
                return mState;
            }
        }

        protected void pauseImpl() {

        }

        public void resume() {
            checkState(STATE_PAUSED);
            setState(STATE_RECORDING);
            resumeImpl();
            mOnStateChangedListener.onResume();
        }

        protected void resumeImpl() {

        }

        public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
            mOnStateChangedListener = onStateChangedListener == null ? NO_OPERATION_LISTENER : onStateChangedListener;
        }

    }
}
