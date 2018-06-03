package org.autojs.autojs.autojs.record;

import android.content.Context;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.stardust.app.DialogUtils;
import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.record.Recorder;
import com.stardust.autojs.core.record.inputevent.InputEventRecorder;
import com.stardust.autojs.core.record.inputevent.InputEventToAutoFileRecorder;
import com.stardust.autojs.core.record.inputevent.InputEventToRootAutomatorRecorder;
import com.stardust.autojs.core.record.inputevent.TouchRecorder;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.ui.common.ScriptOperations;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.ClipboardUtil;


import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/8/6.
 */

public class GlobalActionRecorder implements Recorder.OnStateChangedListener {

    private static GlobalActionRecorder sSingleton;
    private CopyOnWriteArrayList<Recorder.OnStateChangedListener> mOnStateChangedListeners = new CopyOnWriteArrayList<>();
    private TouchRecorder mTouchRecorder;
    private Context mContext;
    private boolean mDiscard = false;

    public static GlobalActionRecorder getSingleton(Context context) {
        if (sSingleton == null) {
            sSingleton = new GlobalActionRecorder(context);
        }
        return sSingleton;
    }


    public GlobalActionRecorder(Context context) {
        mContext = new ContextThemeWrapper(context.getApplicationContext(), R.style.AppTheme);
    }


    public void start() {
        if (mTouchRecorder == null) {
            mTouchRecorder = createTouchRecorder();
        }
        mTouchRecorder.reset();
        mDiscard = false;
        mTouchRecorder.setOnStateChangedListener(this);
        mTouchRecorder.start();
    }

    private TouchRecorder createTouchRecorder() {
        return new TouchRecorder(mContext) {
            @Override
            protected InputEventRecorder createInputEventRecorder() {
                if (Pref.rootRecordGeneratesBinary())
                    return new InputEventToAutoFileRecorder(mContext);
                else
                    return new InputEventToRootAutomatorRecorder();
            }
        };
    }

    public void pause() {
        mTouchRecorder.pause();
    }

    public void resume() {
        mTouchRecorder.resume();
    }

    public void stop() {
        mTouchRecorder.stop();
    }

    public String getCode() {
        return mTouchRecorder.getCode();
    }

    public String getPath() {
        return mTouchRecorder.getPath();
    }

    public int getState() {
        if (mTouchRecorder == null)
            return Recorder.STATE_NOT_START;
        return mTouchRecorder.getState();
    }


    public void addOnStateChangedListener(Recorder.OnStateChangedListener listener) {
        mOnStateChangedListeners.add(listener);
    }

    public boolean removeOnStateChangedListener(Recorder.OnStateChangedListener listener) {
        return mOnStateChangedListeners.remove(listener);
    }

    @Override
    public void onStart() {
        if (Pref.isRecordToastEnabled())
            GlobalAppContext.toast(R.string.text_start_record);
        for (Recorder.OnStateChangedListener listener : mOnStateChangedListeners) {
            listener.onStart();
        }
    }

    @Override
    public void onStop() {
        if (!mDiscard) {
            String code = getCode();
            if (code != null)
                handleRecordedScript(code);
            else
                handleRecordedFile(getPath());
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
            GlobalAppContext.post(() -> showRecordHandleDialog(script));
        }
    }

    private void handleRecordedFile(final String path) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            GlobalAppContext.post(() -> handleRecordedFile(path));
            return;
        }
        new ScriptOperations(mContext, null)
                .importFile(path)
                .subscribe();

    }


    private void showRecordHandleDialog(final String script) {
        DialogUtils.showDialog(new ThemeColorMaterialDialogBuilder(mContext)
                .title(R.string.text_recorded)
                .items(getString(R.string.text_new_file), getString(R.string.text_copy_to_clip))
                .itemsCallback((dialog, itemView, position, text) -> {
                    if (position == 0) {
                        new ScriptOperations(mContext, null)
                                .newScriptFileForScript(script);
                    } else {
                        ClipboardUtil.setClip(mContext, script);
                        Toast.makeText(mContext, R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show();
                    }
                })
                .negativeText(R.string.text_cancel)
                .onNegative((dialog, which) -> dialog.dismiss())
                .canceledOnTouchOutside(false)
                .build());
    }

    private String getString(int res) {
        return mContext.getString(res);
    }

}
