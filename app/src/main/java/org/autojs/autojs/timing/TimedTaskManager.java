package org.autojs.autojs.timing;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.DirectModelNotifier;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.stardust.app.GlobalAppContext;
import org.autojs.autojs.App;
import org.autojs.autojs.storage.database.ModelChange;
import org.autojs.autojs.storage.database.TimedTaskDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/11/27.
 */
//TODO rx
public class TimedTaskManager {


    private static TimedTaskManager sInstance;
    private ModelAdapter<TimedTask> mTimedTaskModelAdapter;
    private Context mContext;
    private PublishSubject<ModelChange<TimedTask>> mTimedTaskChanges = PublishSubject.create();

    public static TimedTaskManager getInstance() {
        if (sInstance == null) {
            sInstance = new TimedTaskManager(GlobalAppContext.get());
        }
        return sInstance;
    }

    public TimedTaskManager(Context context) {
        mContext = context;
        mTimedTaskModelAdapter = FlowManager.getModelAdapter(TimedTask.class);
        DirectModelNotifier.get().registerForModelChanges(TimedTask.class, new DirectModelNotifier.ModelChangedListener<TimedTask>() {
            @Override
            public void onModelChanged(@NonNull TimedTask model, @NonNull BaseModel.Action action) {
                mTimedTaskChanges.onNext(new ModelChange<>(model, action));
                if (action == BaseModel.Action.DELETE && countTasks() == 0) {
                    TimedTaskScheduler.stopRtcRepeating(mContext);
                } else if (action == BaseModel.Action.INSERT) {
                    TimedTaskScheduler.startRtcRepeatingIfNeeded(mContext);
                }
            }

            @Override
            public void onTableChanged(@Nullable Class<?> tableChanged, @NonNull BaseModel.Action action) {

            }
        });
    }

    public void notifyTaskFinished(int id) {
        TimedTask task = getTimedTask(id);
        if (task == null)
            return;
        if (task.isDisposable()) {
            mTimedTaskModelAdapter.delete(task);
        } else {
            task.setScheduled(false);
            mTimedTaskModelAdapter.update(task);
        }
    }

    public void cancelTask(TimedTask timedTask) {
        TimedTaskScheduler.cancel(mContext, timedTask);
        mTimedTaskModelAdapter.delete(timedTask);
    }

    public void addTask(TimedTask timedTask) {
        mTimedTaskModelAdapter.insert(timedTask);
        TimedTaskScheduler.scheduleTaskIfNeeded(mContext, timedTask);
    }


    public Flowable<TimedTask> getAllTasks() {
        return RXSQLite.rx(SQLite.select().from(TimedTask.class))
                .queryStreamResults()
                .subscribeOn(Schedulers.io());

    }

    public Observable<ModelChange<TimedTask>> getTimeTaskChanges() {
        return mTimedTaskChanges;
    }

    public void notifyTaskScheduled(TimedTask timedTask) {
        timedTask.setScheduled(true);
        mTimedTaskModelAdapter.update(timedTask);

    }

    public List<TimedTask> getAllTasksAsList() {
        return SQLite.select().from(TimedTask.class)
                .queryList();
    }

    public TimedTask getTimedTask(int taskId) {
        return SQLite.select()
                .from(TimedTask.class)
                .where(TimedTask_Table.id.is(taskId))
                .querySingle();
    }

    public void updateTask(TimedTask task) {
        mTimedTaskModelAdapter.update(task);
        TimedTaskScheduler.cancel(mContext, task);
        TimedTaskScheduler.scheduleTaskIfNeeded(mContext, task);
    }

    public long countTasks() {
        return SQLite.select().from(TimedTask.class).count();
    }
}
