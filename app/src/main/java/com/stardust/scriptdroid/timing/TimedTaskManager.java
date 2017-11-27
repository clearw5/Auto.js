package com.stardust.scriptdroid.timing;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/11/27.
 */

public class TimedTaskManager {

    private static TimedTaskManager sInstance;
    private ModelAdapter<TimedTask> mTimedTaskModelAdapter;

    public static TimedTaskManager getInstance() {
        if (sInstance == null) {
            sInstance = new TimedTaskManager();
        }
        return sInstance;
    }

    public TimedTaskManager() {
        mTimedTaskModelAdapter = FlowManager.getModelAdapter(TimedTask.class);
    }

    public void notifyTaskFinished(int id) {
        getTaskById(id).subscribe(timedTask -> {
            if (timedTask.isDisposable()) {
                removeTask(id).subscribe();
            } else {
                timedTask.setScheduled(false);
                mTimedTaskModelAdapter.update(timedTask);
            }
        });
    }

    private Flowable<TimedTask> getTaskById(int id) {
        return RXSQLite.rx(SQLite.select()
                .from(TimedTask.class)
                .where(TimedTask_Table.id.is(id)))
                .queryStreamResults()
                .subscribeOn(Schedulers.io());
    }

    private Completable removeTask(int id) {
        return RXSQLite.rx(SQLite.delete(TimedTaskDatabase.class)
                .where(TimedTask_Table.id.is(id)))
                .execute()
                .subscribeOn(Schedulers.io());

    }


    public Flowable<TimedTask> getAllTasks() {
        return RXSQLite.rx(SQLite.select().from(TimedTask.class))
                .queryStreamResults()
                .subscribeOn(Schedulers.io());

    }

    public void notifyTaskScheduled(TimedTask timedTask) {
        timedTask.setScheduled(true);
        mTimedTaskModelAdapter.update(timedTask);

    }

}
