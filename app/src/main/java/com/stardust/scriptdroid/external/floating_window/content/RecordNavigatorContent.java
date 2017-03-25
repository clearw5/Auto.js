package com.stardust.scriptdroid.external.floating_window.content;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.accessibility.AccessibilityEventHelper;
import com.stardust.scriptdroid.external.floating_window.HoverMenuService;
import com.stardust.scriptdroid.record.Recorder;
import com.stardust.scriptdroid.record.accessibility.AccessibilityActionRecorder;
import com.stardust.scriptdroid.record.inputevent.InputEventConverter;
import com.stardust.scriptdroid.record.inputevent.TouchRecorder;
import com.stardust.scriptdroid.service.VolumeChangeObverseService;
import com.stardust.scriptdroid.ui.main.MainActivity;
import com.stardust.util.MessageEvent;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.NavigatorContent;


/**
 * Created by Stardust on 2017/3/12.
 */

public class RecordNavigatorContent implements NavigatorContent, Recorder.OnStateChangedListener {

    private View mView;
    @ViewBinding.Id(R.id.sw_recorded_by_root)
    private SwitchCompat mRecordedByRootSwitch;

    @ViewBinding.Id(R.id.sw_record_toast)
    private SwitchCompat mRecordToastSwitch;

    @ViewBinding.Id(R.id.img_start_or_pause)
    private ImageView mStartOrPauseRecordIcon;

    @ViewBinding.Id(R.id.text_start_or_pause)
    private TextView mStartOrPauseRecordText;


    @ViewBinding.Id(R.id.stop_record)
    private View mStopRecord;

    private Recorder mRecorder;
    private Context mContext;

    private VolumeChangeObverseService.OnVolumeChangeListener mOnVolumeChangeListener = new VolumeChangeObverseService.OnVolumeChangeListener() {
        @Override
        public void onVolumeChange() {
            if (Pref.isRecordVolumeControlEnable()) {
                if (mRecorder == null) {
                    startRecord();
                } else if (mRecorder.getState() == Recorder.STATE_RECORDING) {
                    stopRecord();
                }
            }
        }
    };

    public RecordNavigatorContent(Context context) {
        mContext = context;
        mView = View.inflate(context, R.layout.floating_window_record, null);
        ViewBinder.bind(this);
        EventBus.getDefault().register(this);
        App.getApp().startService(new Intent(App.getApp(), VolumeChangeObverseService.class));
        VolumeChangeObverseService.addOnVolumeChangeListener(mOnVolumeChangeListener);
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

    @ViewBinding.Click(R.id.sw_root_container)
    private void toggleRecordedByRootSwitch() {
        mRecordedByRootSwitch.toggle();
    }

    @ViewBinding.Click(R.id.sw_record_toast_container)
    private void toggleRecordToastSwitch() {
        mRecordToastSwitch.toggle();
    }

    @ViewBinding.Click(R.id.start_or_pause)
    private void startOrPauseRecord() {
        if (mRecorder == null) {
            startRecord();
        } else if (mRecorder.getState() == Recorder.STATE_PAUSED) {
            resumeRecord();
        } else {
            pauseRecord();
        }
    }

    private void resumeRecord() {
        mRecorder.resume();
        setState(Recorder.STATE_RECORDING);
        EventBus.getDefault().post(new MessageEvent(HoverMenuService.MESSAGE_COLLAPSE_MENU));
    }

    private void pauseRecord() {
        mRecorder.pause();
        setState(Recorder.STATE_PAUSED);
    }

    private void startRecord() {
        mRecorder = mRecordedByRootSwitch.isChecked() ? new TouchRecorder() : AccessibilityActionRecorder.getInstance();
        mRecorder.setOnStateChangedListener(this);
        mRecorder.start();
        setState(Recorder.STATE_RECORDING);
        EventBus.getDefault().post(new MessageEvent(HoverMenuService.MESSAGE_COLLAPSE_MENU));
    }

    private void setState(int state) {
        mStopRecord.setVisibility(state == Recorder.STATE_STOPPED ? View.GONE : View.VISIBLE);
        mStartOrPauseRecordIcon.setImageResource(state == Recorder.STATE_RECORDING ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_48dp);
        //我知道这样写代码会被打 但我懒...
        mStartOrPauseRecordText.setText(state == Recorder.STATE_RECORDING ? R.string.text_pause_record :
                state == Recorder.STATE_PAUSED ? R.string.text_resume_record : R.string.text_start_record);

    }

    @ViewBinding.Click(R.id.stop_record)
    private void stopRecord() {
        mRecorder.stop();
        setState(Recorder.STATE_STOPPED);
        EventBus.getDefault().post(new MessageEvent(HoverMenuService.MESSAGE_COLLAPSE_MENU));
    }


    public View findViewById(int id) {
        return mView.findViewById(id);
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.message.equals(HoverMenuService.MESSAGE_MENU_EXPANDING)) {
            pauseRecord();
        } else if (event.message.equals(HoverMenuService.MESSAGE_MENU_EXIT)) {
            EventBus.getDefault().unregister(this);
            VolumeChangeObverseService.removeOnVolumeChangeListener(mOnVolumeChangeListener);
        }
    }

    @Subscribe
    public void onAccessibilityActionRecordEvent(AccessibilityActionRecorder.AccessibilityActionRecordEvent event) {
        if (mRecordToastSwitch.isChecked()) {
            Toast.makeText(mContext, AccessibilityEventHelper.getEventTypeNameResId(event.getAccessibilityEvent()), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onTouchRecorderStateChanged(InputEventConverter.RecordStateChangeEvent event) {
        if (!event.isRecording()) {
            stopRecord();
        }
    }

    @Override
    public void onStart() {
        Toast.makeText(mContext, R.string.text_start_record, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        MainActivity.onActionRecordStopped(mContext, mRecorder.getCode());
        mRecorder = null;
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {

    }
}
