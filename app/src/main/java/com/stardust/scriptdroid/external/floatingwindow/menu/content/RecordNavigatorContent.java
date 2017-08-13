package com.stardust.scriptdroid.external.floatingwindow.menu.content;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.DialogUtils;
import com.stardust.autojs.core.record.Recorder;
import com.stardust.autojs.core.record.accessibility.AccessibilityActionRecorder;
import com.stardust.autojs.core.inputevent.InputEventObserver;
import com.stardust.autojs.core.inputevent.ShellKeyObserver;
import com.stardust.autojs.core.record.inputevent.TouchRecorder;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.accessibility.AccessibilityEventHelper;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.autojs.record.GlobalRecorder;
import com.stardust.scriptdroid.external.floatingwindow.menu.HoverMenuService;
import com.stardust.scriptdroid.ui.common.ScriptOperations;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.ClipboardUtil;
import com.stardust.util.MessageEvent;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.OnKeyListener;
import com.stardust.widget.PrefSwitch;
import com.stardust.widget.ViewSwitcher;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.NavigatorContent;


/**
 * Created by Stardust on 2017/3/12.
 */

public class RecordNavigatorContent implements NavigatorContent, Recorder.OnStateChangedListener, OnKeyListener {

    private View mView;
    @BindView(R.id.sw_recorded_by_root)
    PrefSwitch mRecordedByRootSwitch;

    @BindView(R.id.sw_record_toast)
    PrefSwitch mRecordToastSwitch;

    @BindView(R.id.img_pause_or_resume)
    ImageView mPauseOrResumeImage;

    @BindView(R.id.text_pause_or_resume)
    TextView mPauseOrResumeText;

    @BindView(R.id.view_switcher)
    ViewSwitcher mViewSwitcher;

    private GlobalRecorder mRecorder;
    private Context mContext;
    private long mLastVolumeDownEventTime;

    public RecordNavigatorContent(Context context) {
        mContext = new ContextThemeWrapper(context, R.style.AppTheme);
        mView = View.inflate(mContext, R.layout.floating_window_record, null);
        ButterKnife.bind(this, mView);
        HoverMenuService.getEventBus().register(this);
        mRecorder = GlobalRecorder.getSingleton(context);
        mRecorder.addOnStateChangedListener(this);
        setState(mRecorder.getState());
        AccessibilityService.getStickOnKeyObserver().addListener(this);
    }

    private void onVolumeDown() {
        if (!Pref.isRecordVolumeControlEnable()) {
            return;
        }
        if (System.currentTimeMillis() - mLastVolumeDownEventTime < 300) {
            return;
        }
        mLastVolumeDownEventTime = System.currentTimeMillis();
        int state = mRecorder.getState();
        if (state == Recorder.STATE_RECORDING || state == Recorder.STATE_PAUSED) {
            mRecorder.stop();
        } else {
            mRecorder.start();
        }
    }


    @NonNull
    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void onShown(@NonNull Navigator navigator) {

    }

    @Override
    public void onHidden() {

    }

    @OnClick(R.id.sw_root_container)
    void toggleRecordedByRootSwitch() {
        mRecordedByRootSwitch.toggle();
    }

    @OnClick(R.id.sw_record_toast_container)
    void toggleRecordToastSwitch() {
        mRecordToastSwitch.toggle();
    }

    @OnClick(R.id.start_record)
    void startRecord() {
        mRecorder.start();
        HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_COLLAPSE_MENU));
    }

    @OnClick(R.id.discard_record)
    void discardRecord() {
        mRecorder.discard();
    }


    @OnClick(R.id.pause_or_resume_record)
    void pauseOrResumeRecord() {
        if (mRecorder.getState() == Recorder.STATE_PAUSED) {
            mRecorder.resume();
        } else {
            mRecorder.pause();
        }
        HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_COLLAPSE_MENU));
    }

    private void setState(int state) {
        if (state == Recorder.STATE_NOT_START || state == Recorder.STATE_STOPPED) {
            mViewSwitcher.showFirst();
        } else {
            mViewSwitcher.showSecond();
        }
        mPauseOrResumeImage.setImageResource(state == Recorder.STATE_RECORDING ? R.drawable.ic_pause_white_24dp :
                R.drawable.ic_play_arrow_white_48dp);
        mPauseOrResumeText.setText(
                state == Recorder.STATE_RECORDING ? R.string.text_pause_record : R.string.text_resume_record);
    }

    @OnClick(R.id.stop_record)
    void stopRecord() {
        mRecorder.stop();
        setState(Recorder.STATE_STOPPED);
        HoverMenuService.postIntent(new Intent(HoverMenuService.ACTION_COLLAPSE_MENU));
    }


    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.message.equals(HoverMenuService.ACTION_MENU_EXPANDING)) {
            if (mRecorder.getState() == Recorder.STATE_RECORDING)
                mRecorder.pause();
        } else if (event.message.equals(HoverMenuService.ACTION_MENU_EXIT)) {
            onMenuExit();
        }
    }

    public void onMenuExit() {
        HoverMenuService.getEventBus().unregister(this);
        mRecorder.removeOnStateChangedListener(this);
        AccessibilityService.getStickOnKeyObserver().removeListener(this);
    }

    @Override
    public void onStart() {
        setState(Recorder.STATE_RECORDING);
    }

    @Override
    public void onStop() {
        setState(Recorder.STATE_STOPPED);
    }

    @Override
    public void onPause() {
        setState(Recorder.STATE_PAUSED);
    }

    @Override
    public void onResume() {
        setState(Recorder.STATE_RECORDING);
    }

    @Override
    public void onKeyEvent(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            onVolumeDown();
        }
    }

}