package com.stardust.scriptdroid.timing;


import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio2.sqlite.queries.Query;

import io.reactivex.Observable;

/**
 * Created by Stardust on 2017/11/27.
 */

public class TimedTaskManager {

    static final String TABLE_NAME = "timed_tasks";

    private static TimedTaskManager sInstance = new TimedTaskManager();
    private StorIOSQLite mStorIOSQLite;

    public static TimedTaskManager getInstance() {
        return sInstance;
    }

    public TimedTaskManager() {
      /*  mStorIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper()
                .build();*/
    }

    public void notifyTaskFinished(int id) {
        getTaskById(id).subscribe(timedTask -> {
            if (timedTask.isDisposable()) {
                removeTask(id);
            } else {

            }
        });
    }

    private Observable<TimedTask> getTaskById(int id) {
        return null;
    }

    private void removeTask(int id) {
    }


    public Observable<TimedTask> getAllTasks() {
        return null;
    }

    public Observable<TimedTask> getFutureTasksInMillis(long millis) {
        long current = System.currentTimeMillis();
        return getAllTasks().filter(timedTask ->
                timedTask.getNextTime() - current <= millis);
    }
}
