package com.stardust.scriptdroid.timing;

import android.content.Intent;
import android.text.format.Time;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.scriptdroid.external.ScriptIntents;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


/**
 * Created by Stardust on 2017/11/27.
 */
@Table(database = TimedTaskDatabase.class)
public class TimedTask {

    private static final int FLAG_DISPOSABLE = 0;
    public final static int FLAG_SUNDAY = 0x1;
    public final static int FLAG_MONDAY = 0x2;
    public final static int FLAG_TUESDAY = 0x4;
    public final static int FLAG_WEDNESDAY = 0x8;
    public final static int FLAG_THURSDAY = 0x10;
    public final static int FLAG_FRIDAY = 0x20;
    public final static int FLAG_SATURDAY = 0x40;
    private static final int FLAG_EVERYDAY = 0x7F;

    @PrimaryKey(autoincrement = true)
    @Column(name = "id")
    int mId;

    @Column(name = "days_of_week")
    int mDaysOfWeek;

    @Column(name = "scheduled")
    boolean mScheduled;

    @Column(name = "delay")
    private long mDelay = 0;

    @Column(name = "interval")
    private long mInterval = 0;

    @Column(name = "loop_times")
    private int mLoopTimes = 1;

    @Column(name = "millis")
    private long mMillis;

    @Column(name = "script_path")
    private String mScriptPath;

    public TimedTask() {

    }

    public TimedTask(long millis, int daysOfWeek, String scriptPath, ExecutionConfig config) {
        mMillis = millis;
        mDaysOfWeek = daysOfWeek;
        mScriptPath = scriptPath;
        mDelay = config.delay;
        mLoopTimes = config.loopTimes;
        mInterval = config.interval;
    }

    public boolean isDisposable() {
        return mDaysOfWeek == FLAG_DISPOSABLE;
    }

    public boolean isScheduled() {
        return mScheduled;
    }

    public void setScheduled(boolean scheduled) {
        mScheduled = scheduled;
    }

    public long getNextTime() {
        if (isDisposable()) {
            return mMillis;
        }
        // TODO: 2017/11/28 day of week
        LocalTime time = LocalTime.fromMillisOfDay(mMillis);
        long nextTimeMillis = time.toDateTimeToday().getMillis();
        if (System.currentTimeMillis() > nextTimeMillis) {
            return nextTimeMillis + TimeUnit.DAYS.toMillis(1);
        }
        return nextTimeMillis;
    }

    public int getId() {
        return mId;
    }

    public Intent createIntent() {
        return new Intent(TaskReceiver.ACTION_TASK)
                .putExtra(TaskReceiver.EXTRA_TASK_ID, mId)
                .putExtra(ScriptIntents.EXTRA_KEY_PATH, mScriptPath)
                .putExtra(ScriptIntents.EXTRA_KEY_DELAY, mDelay)
                .putExtra(ScriptIntents.EXTRA_KEY_LOOP_TIMES, mLoopTimes)
                .putExtra(ScriptIntents.EXTRA_KEY_LOOP_INTERVAL, mInterval);
    }

    public static TimedTask dailyTask(LocalTime time, String scriptPath, ExecutionConfig config) {
        return new TimedTask(time.getMillisOfDay(), FLAG_EVERYDAY, scriptPath, config);
    }

    public static TimedTask disposableTask(long millis, String scriptPath, ExecutionConfig config) {
        return new TimedTask(millis, FLAG_EVERYDAY, scriptPath, config);
    }

}
