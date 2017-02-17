package com.stardust.scriptdroid.external.notification.record;

import android.widget.RemoteViews;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/2/14.
 */

public class ActionRecordSwitchView extends RemoteViews {

    private static ActionRecordSwitchView instance;

    public static final int STOPPED = 0;
    public static final int PAUSED = 1;
    public static final int RECORDING = 2;

    public static ActionRecordSwitchView getInstance() {
        if (instance == null) {
            instance = new ActionRecordSwitchView();
        }
        return instance;
    }

    private ActionRecordSwitchView() {
        super(App.getApp().getPackageName(), R.layout.remote_views_record_switch);
        setUpOnClick();
    }

    public void setState(int state) {
        switch (state) {
            case STOPPED:
                setImageViewResource(R.id.img_start_or_pause, R.drawable.ic_play_arrow_grey600_48dp);
                setTextViewText(R.id.text_start_or_pause, App.getApp().getString(R.string.text_start_record));
                break;
            case RECORDING:
                setImageViewResource(R.id.img_start_or_pause, R.drawable.ic_pause_grey600_48dp);
                setTextViewText(R.id.text_start_or_pause, App.getApp().getString(R.string.text_pause_record));
                break;
            case PAUSED:
                setImageViewResource(R.id.img_start_or_pause, R.drawable.ic_play_arrow_grey600_48dp);
                setTextViewText(R.id.text_start_or_pause, App.getApp().getString(R.string.text_resume_record));
                break;
        }
        ActionRecordSwitchNotification.showOrUpdateNotification();
    }

    private void setUpOnClick() {
        setOnClickPendingIntent(R.id.stop, ActionRecordSwitchHandleService.getStopIntent());
        setOnClickPendingIntent(R.id.start_or_pause, ActionRecordSwitchHandleService.getStartOrPauseIntent());
        setOnClickPendingIntent(R.id.close, ActionRecordSwitchHandleService.getDeleteIntent());
        setOnClickPendingIntent(R.id.redo, ActionRecordSwitchHandleService.getRedoIntent());
    }
}
