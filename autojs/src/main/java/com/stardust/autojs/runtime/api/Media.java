package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.stardust.pio.UncheckedIOException;
import com.stardust.util.MimeTypes;

import java.io.IOException;

/**
 * Created by Stardust on 2018/2/12.
 */

public class Media implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mScannerConnection;
    private MediaPlayerWrapper mMediaPlayer;

    public Media(Context context) {
        mScannerConnection = new MediaScannerConnection(context, this);
    }

    public void scan(String path) {
        String mimeType = MimeTypes.fromFileOr(path, null);
        mScannerConnection.scanFile(path, mimeType);
    }

    @Override
    public void onMediaScannerConnected() {

    }

    public void playMusic(String path, float volume, boolean looping) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayerWrapper();
        }
        mMediaPlayer.stopAndReset();
        try {
            mMediaPlayer.setDataSource(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        mMediaPlayer.setVolume(volume, volume);
        mMediaPlayer.setLooping(looping);
        mMediaPlayer.start();
    }

    public void pauseMusic() {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.pause();
    }

    public void resumeMusic() {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.start();
    }

    public void stopMusic() {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.stop();
    }


    @Override
    public void onScanCompleted(String path, Uri uri) {

    }

    public void recycle() {
        if (mScannerConnection != null) {
            mScannerConnection.disconnect();
            mScannerConnection = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    private static class MediaPlayerWrapper extends MediaPlayer {

        static final int STATE_NOT_INITIALIZED = 0;
        static final int STATE_PREPARING = 1;
        static final int STATE_PREPARED = 2;
        static final int STATE_START = 3;
        static final int STATE_PAUSED = 4;
        static final int STATE_STOPPED = 5;
        static final int STATE_RELEASED = 6;

        private int mState = STATE_NOT_INITIALIZED;

        public int getState() {
            return mState;
        }

        @Override
        public void prepare() throws IOException, IllegalStateException {
            mState = STATE_PREPARING;
            super.prepare();
            mState = STATE_PREPARED;
        }

        @Override
        public void prepareAsync() throws IllegalStateException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void start() throws IllegalStateException {
            super.start();
            mState = STATE_START;
        }

        @Override
        public void stop() throws IllegalStateException {
            super.stop();
            mState = STATE_STOPPED;
        }

        @Override
        public void pause() throws IllegalStateException {
            super.pause();
            mState = STATE_PAUSED;
        }

        @Override
        public void release() {
            super.release();
            mState = STATE_RELEASED;
        }

        @Override
        public void reset() {
            super.reset();
            mState = STATE_NOT_INITIALIZED;
        }

        public void stopAndReset() {
            try {
                if (mState == STATE_START || mState == STATE_PAUSED) {
                    stop();
                }
                if (mState != STATE_NOT_INITIALIZED) {
                    reset();
                }
            } catch (IllegalStateException ignored) {

            }
        }
    }
}
