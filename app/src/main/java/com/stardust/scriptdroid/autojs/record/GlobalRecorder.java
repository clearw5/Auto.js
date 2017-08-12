package com.stardust.scriptdroid.autojs.record;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.DialogUtils;
import com.stardust.autojs.core.inputevent.InputEventCodes;
import com.stardust.autojs.core.inputevent.ShellKeyObserver;
import com.stardust.autojs.core.record.Recorder;
import com.stardust.autojs.core.record.inputevent.TouchRecorder;
import com.stardust.autojs.runtime.api.Shell;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.ui.common.ScriptOperations;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.ClipboardUtil;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.OnKeyListener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/8/6.
 */

public class GlobalRecorder implements Recorder.OnStateChangedListener {

    private static GlobalRecorder sSingleton;
    private Recorder mRecorder;
    private CopyOnWriteArrayList<Recorder.OnStateChangedListener> mOnStateChangedListeners = new CopyOnWriteArrayList<>();
    private TouchRecorder mTouchRecorder;
    private Context mContext;
    private boolean mDiscard = false;
    private long mLastVolumeDownEventTime;
    private ShellKeyObserver mShellKeyObserver;

    public static GlobalRecorder getSingleton(Context context) {
        if (sSingleton == null) {
            sSingleton = new GlobalRecorder(context);
        }
        return sSingleton;
    }


    public static void initSingleton(Context context) {
        getSingleton(context);
    }

    public GlobalRecorder(Context context) {
        mContext = new ContextThemeWrapper(context.getApplicationContext(), R.style.AppTheme);
        mTouchRecorder = new TouchRecorder(context);
        addKeyListeners();
    }

    private void addKeyListeners() {
        AccessibilityService.getStickOnKeyObserver().addListener(new OnKeyListener() {
            @Override
            public void onKeyEvent(int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
                    onVolumeDown();
                }
            }

        });
        mShellKeyObserver = new ShellKeyObserver();
        mShellKeyObserver.setKeyListener(new ShellKeyObserver.KeyListener() {
            @Override
            public void onKeyDown(String keyName) {
                if ("KEY_VOLUMEDOWN".equals(keyName)) {
                    onVolumeDown();
                }
            }

            @Override
            public void onKeyUp(String keyName) {

            }
        });
    }


    private void onVolumeDown() {
        if (System.currentTimeMillis() - mLastVolumeDownEventTime < 300) {
            return;
        }
        mLastVolumeDownEventTime = System.currentTimeMillis();
        int state = getState();
        if (state == Recorder.STATE_RECORDING || state == Recorder.STATE_PAUSED) {
            stop();
        } else {
            start();
        }
    }

    public void start() {
        if (Pref.isRecordWithRootEnabled()) {
            mTouchRecorder.reset();
            mRecorder = mTouchRecorder;
        } else {
            mRecorder = AutoJs.getInstance().getAccessibilityActionRecorder();
        }
        mDiscard = false;
        mRecorder.setOnStateChangedListener(this);
        mRecorder.start();
    }

    public void pause() {
        mRecorder.pause();
    }

    public void resume() {
        mRecorder.resume();
    }

    public void stop() {
        mRecorder.stop();
    }

    public String getCode() {
        return mRecorder.getCode();
    }

    public int getState() {
        if (mRecorder == null)
            return Recorder.STATE_NOT_START;
        return mRecorder.getState();
    }


    public void addOnStateChangedListener(Recorder.OnStateChangedListener listener) {
        mOnStateChangedListeners.add(listener);
    }

    public boolean removeOnStateChangedListener(Recorder.OnStateChangedListener listener) {
        return mOnStateChangedListeners.remove(listener);
    }

    @Override
    public void onStart() {
        App.getApp().getUiHandler().toast(R.string.text_start_record);
        for (Recorder.OnStateChangedListener listener : mOnStateChangedListeners) {
            listener.onStart();
        }
    }

    @Override
    public void onStop() {
        if (!mDiscard) {
            handleRecordedScript(getCode());
        }
        for (Recorder.OnStateChangedListener listener : mOnStateChangedListeners) {
            listener.onStop();
        }
    }

    @Override
    public void onPause() {
        for (Recorder.OnStateChangedListener listener : mOnStateChangedListeners) {
            listener.onPause();
        }
    }

    @Override
    public void onResume() {
        for (Recorder.OnStateChangedListener listener : mOnStateChangedListeners) {
            listener.onResume();
        }
    }

    public void discard() {
        mDiscard = true;
        stop();
    }

    private void handleRecordedScript(final String script) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showRecordHandleDialog(script);
        } else {
            App.getApp().getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    showRecordHandleDialog(script);
                }
            });
        }
    }

    private void showRecordHandleDialog(final String script) {
        DialogUtils.showDialog(new ThemeColorMaterialDialogBuilder(mContext)
                .title(R.string.text_recorded)
                .items(getString(R.string.text_new_file), getString(R.string.text_copy_to_clip))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        if (position == 0) {
                            new ScriptOperations(mContext, null)
                                    .newScriptFileForScript(script);
                        } else {
                            ClipboardUtil.setClip(mContext, script);
                            Toast.makeText(mContext, R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .negativeText(R.string.text_cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .canceledOnTouchOutside(false)
                .build());
    }

    private String getString(int res) {
        return mContext.getString(res);
    }

}
