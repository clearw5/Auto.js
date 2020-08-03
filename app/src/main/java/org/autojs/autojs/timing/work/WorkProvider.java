package org.autojs.autojs.timing.work;

import android.content.Context;

import org.autojs.autojs.timing.TimedTask;

public interface WorkProvider {

    /**
     * 创建定时执行的任务
     *
     * @param timedTask 任务信息
     * @param timeWindow 延迟时间
     */
    void enqueueWork(TimedTask timedTask, long timeWindow);


    /**
     * 创建定期执行的任务
     *
     * @param delay 延迟启动时间
     */
    void enqueuePeriodicWork(int delay);

    /**
     * 取消定时任务
     *
     * @param timedTask
     */
    void cancel(TimedTask timedTask);

    void cancelAllWorks();

    boolean isCheckWorkFine();

    void checkTasks(Context context, boolean force);

    void scheduleTaskIfNeeded(Context context, TimedTask timedTask, boolean force);

    void scheduleTask(Context context, TimedTask timedTask, long millis, boolean force);
}
