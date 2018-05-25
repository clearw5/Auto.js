package org.autojs.autojs.timing;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.stardust.autojs.execution.ExecutionConfig;
import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autojs.storage.database.TimedTaskDatabase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

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
    private static final int REQUEST_CODE = 2000;

    @PrimaryKey(autoincrement = true, quickCheckAutoIncrement = true)
    @Column(name = "id")
    int mId = -1;

    @Column(name = "time")
    long mTimeFlag;

    @Column(name = "scheduled")
    boolean mScheduled;

    @Column(name = "delay")
    long mDelay = 0;

    @Column(name = "interval")
    long mInterval = 0;

    @Column(name = "loop_times")
    int mLoopTimes = 1;

    @Column(name = "millis")
    long mMillis;

    @Column(name = "script_path")
    String mScriptPath;

    public TimedTask() {

    }

    public TimedTask(long millis, long timeFlag, String scriptPath, ExecutionConfig config) {
        mMillis = millis;
        mTimeFlag = timeFlag;
        mScriptPath = scriptPath;
        mDelay = config.delay;
        mLoopTimes = config.loopTimes;
        mInterval = config.interval;
    }

    public boolean isDisposable() {
        return mTimeFlag == FLAG_DISPOSABLE;
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
        if (isDaily()) {
            LocalTime time = LocalTime.fromMillisOfDay(mMillis);
            long nextTimeMillis = time.toDateTimeToday().getMillis();
            if (System.currentTimeMillis() > nextTimeMillis) {
                return nextTimeMillis + TimeUnit.DAYS.toMillis(1);
            }
            return nextTimeMillis;
        }
        return getNextTimeOfWeeklyTask();

    }

    private long getNextTimeOfWeeklyTask() {
        int dayOfWeek = DateTime.now().getDayOfWeek();
        long nextTimeMillis = LocalTime.fromMillisOfDay(mMillis).toDateTimeToday().getMillis();
        for (int i = 0; i < 8; i++) {
            if ((getDayOfWeekTimeFlag(dayOfWeek) & mTimeFlag) != 0) {
                if (System.currentTimeMillis() <= nextTimeMillis) {
                    return nextTimeMillis;
                }
            }
            dayOfWeek++;
            nextTimeMillis += TimeUnit.DAYS.toMillis(1);
        }
        throw new IllegalStateException("Should not happen! timeFlag = " + mTimeFlag + ", dayOfWeek = " + DateTime.now().getDayOfWeek());
    }

    public static long getDayOfWeekTimeFlag(int dayOfWeek) {
        dayOfWeek = (dayOfWeek - 1) % 7 + 1;
        switch (dayOfWeek) {
            case DateTimeConstants.SUNDAY:
                return FLAG_SUNDAY;

            case DateTimeConstants.MONDAY:
                return FLAG_MONDAY;

            case DateTimeConstants.SATURDAY:
                return FLAG_SATURDAY;

            case DateTimeConstants.WEDNESDAY:
                return FLAG_WEDNESDAY;

            case DateTimeConstants.TUESDAY:
                return FLAG_TUESDAY;

            case DateTimeConstants.THURSDAY:
                return FLAG_THURSDAY;
            case DateTimeConstants.FRIDAY:
                return FLAG_FRIDAY;

        }
        throw new IllegalArgumentException("dayOfWeek = " + dayOfWeek);
    }

    public long getMillis() {
        return mMillis;
    }

    public int getId() {
        return mId;
    }

    public String getScriptPath() {
        return mScriptPath;
    }


    public void setId(int id) {
        mId = id;
    }

    public long getTimeFlag() {
        return mTimeFlag;
    }

    public void setTimeFlag(long time) {
        mTimeFlag = time;
    }

    public long getDelay() {
        return mDelay;
    }

    public void setDelay(long delay) {
        mDelay = delay;
    }

    public long getInterval() {
        return mInterval;
    }

    public void setInterval(long interval) {
        mInterval = interval;
    }

    public int getLoopTimes() {
        return mLoopTimes;
    }

    public void setLoopTimes(int loopTimes) {
        mLoopTimes = loopTimes;
    }

    public void setMillis(long millis) {
        mMillis = millis;
    }

    public void setScriptPath(String scriptPath) {
        mScriptPath = scriptPath;
    }

    public boolean isDaily() {
        return mTimeFlag == FLAG_EVERYDAY;
    }

    public Intent createIntent() {
        return new Intent(TaskReceiver.ACTION_TASK)
                .putExtra(TaskReceiver.EXTRA_TASK_ID, mId)
                .putExtra(ScriptIntents.EXTRA_KEY_PATH, mScriptPath)
                .putExtra(ScriptIntents.EXTRA_KEY_DELAY, mDelay)
                .putExtra(ScriptIntents.EXTRA_KEY_LOOP_TIMES, mLoopTimes)
                .putExtra(ScriptIntents.EXTRA_KEY_LOOP_INTERVAL, mInterval);
    }


    public PendingIntent createPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context, REQUEST_CODE + 1 + getId(),
                createIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public String toString() {
        return "TimedTask{" +
                "mId=" + mId +
                ", mTimeFlag=" + mTimeFlag +
                ", mScheduled=" + mScheduled +
                ", mDelay=" + mDelay +
                ", mInterval=" + mInterval +
                ", mLoopTimes=" + mLoopTimes +
                ", mMillis=" + mMillis +
                ", mScriptPath='" + mScriptPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimedTask timedTask = (TimedTask) o;

        return mId == timedTask.mId;
    }

    @Override
    public int hashCode() {
        return mId;
    }

    public static TimedTask dailyTask(LocalTime time, String scriptPath, ExecutionConfig config) {
        return new TimedTask(time.getMillisOfDay(), FLAG_EVERYDAY, scriptPath, config);
    }

    public static TimedTask disposableTask(LocalDateTime dateTime, String scriptPath, ExecutionConfig config) {
        return new TimedTask(dateTime.toDateTime().getMillis(), FLAG_DISPOSABLE, scriptPath, config);
    }

    public static TimedTask weeklyTask(LocalTime time, long timeFlag, String scriptPath, ExecutionConfig config) {
        return new TimedTask(time.getMillisOfDay(), timeFlag, scriptPath, config);
    }

    public boolean hasDayOfWeek(int dayOfWeek) {
        return (mTimeFlag & getDayOfWeekTimeFlag(dayOfWeek)) != 0;
    }
}
