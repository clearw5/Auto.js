package com.stardust.scriptdroid.external.notification.record;

import android.widget.RemoteViews;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.record.Recorder;

/**
 * Created by Stardust on 2017/2/14.
 */

public class AccessibilityActionRecordSwitchView extends RemoteViews {

    private static AccessibilityActionRecordSwitchView instance;

    public static AccessibilityActionRecordSwitchView getInstance() {
        if (instance == null) {
            instance = new AccessibilityActionRecordSwitchView();
        }
        return instance;
    }

    private AccessibilityActionRecordSwitchView() {
        super(App.getApp().getPackageName(), R.layout.remote_views_record_switch);
        setUpOnClick();
    }

    public void setState(int state) {
        switch (state) {
            case Recorder.STATE_STOPPED:
                setImageViewResource(R.id.img_start_or_pause, R.drawable.ic_play_arrow_grey600_48dp);
                setTextViewText(R.id.text_start_or_pause, App.getApp().getString(R.string.text_start_record));
                break;
            case Recorder.STATE_RECORDING:
                setImageViewResource(R.id.img_start_or_pause, R.drawable.ic_pause_grey600_48dp);
                setTextViewText(R.id.text_start_or_pause, App.getApp().getString(R.string.text_pause_record));
                break;
            case Recorder.STATE_PAUSED:
                setImageViewResource(R.id.img_start_or_pause, R.drawable.ic_play_arrow_grey600_48dp);
                setTextViewText(R.id.text_start_or_pause, App.getApp().getString(R.string.text_resume_record));
                break;
        }
        AccessibilityActionRecordNotification.showOrUpdateNotification();
    }

    private void setUpOnClick() {
        setOnClickPendingIntent(R.id.stop, AccessibilityActionRecordNotification.getStopIntent());
        setOnClickPendingIntent(R.id.start_or_pause, AccessibilityActionRecordNotification.getStartOrPauseIntent());
        setOnClickPendingIntent(R.id.close, AccessibilityActionRecordNotification.getDeleteIntent());
        setOnClickPendingIntent(R.id.redo, AccessibilityActionRecordNotification.getRedoIntent());
    }
}
