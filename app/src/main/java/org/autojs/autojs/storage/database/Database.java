package org.autojs.autojs.storage.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.autojs.autojs.timing.IntentTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public abstract class Database<M extends BaseModel> {

    private final SQLiteDatabase mWritableSQLiteDatabase;
    private final SQLiteDatabase mReadableSQLiteDatabase;
    private final String mTable;
    private final PublishSubject<ModelChange<M>> mModelChange = PublishSubject.create();

    public Database(SQLiteOpenHelper sqLiteOpenHelper, String table) {
        mWritableSQLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        mReadableSQLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        mTable = table;
    }

    public <T> Observable<T> exec(Callable<T> callable) {
        return Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io());
    }

    public <T> Flowable<T> execFlowable(Callable<T> callable) {
        return Flowable.fromCallable(callable)
                .subscribeOn(Schedulers.io());
    }

    public PublishSubject<ModelChange<M>> getModelChange() {
        return mModelChange;
    }

    public Observable<Integer> delete(M model) {
        return exec(() -> {
            int delete = mWritableSQLiteDatabase.delete(mTable, "id = ?",
                    new String[]{String.valueOf(model.getId())});
            if (delete >= 1) {
                mModelChange.onNext(new ModelChange<>(model, ModelChange.DELETE));
            }
            return delete;
        });
    }

    public Observable<Integer> update(M model) {
        return exec(() -> {
            ContentValues values = asContentValues(model);
            values.put("id", model.getId());
            int update = mWritableSQLiteDatabase.update(mTable, values, "id = ?", arg(model.getId()));
            if (update >= 1) {
                mModelChange.onNext(new ModelChange<>(model, ModelChange.UPDATE));
            }
            return update;
        });
    }


    public Observable<Long> insert(M model) {
        return exec(() -> {
            ContentValues values = asContentValues(model);
            long id = mWritableSQLiteDatabase.insertOrThrow(mTable, null, values);
            if (id >= 0) {
                model.setId(id);
                mModelChange.onNext(new ModelChange<>(model, ModelChange.INSERT));
            }
            return id;
        });
    }


    protected abstract M createModelFromCursor(Cursor cursor);

    protected abstract ContentValues asContentValues(M model);

    public M queryById(long id) {
        Cursor cursor = mReadableSQLiteDatabase.rawQuery("SELECT * FROM " + mTable + " WHERE id = ?", arg(id));
        if (!cursor.moveToFirst()) {
            return null;
        }
        M model = createModelFromCursor(cursor);
        cursor.close();
        return model;
    }


    public Flowable<M> queryAllAsFlowable() {
        return execFlowable(() ->
                mReadableSQLiteDatabase.rawQuery("SELECT * FROM " + mTable, null)
        )
                .flatMap(cursor -> Flowable.fromIterable(() -> new CursorIterator(cursor)))
                .map(this::createModelFromCursor);
    }

    public List<M> queryAll() {
        ArrayList<M> list = new ArrayList<>();
        Cursor cursor = mReadableSQLiteDatabase.rawQuery("SELECT * FROM " + mTable, null);
        while (cursor.moveToNext()) {
            list.add(createModelFromCursor(cursor));
        }
        cursor.close();
        return list;
    }

    public long count() {
        Cursor cursor = mReadableSQLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + mTable, null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        cursor.close();
        return 0;
    }


    public Flowable<M> query(String sql, Object... args) {
        String[] strArgs = args(args);
        return execFlowable(() ->
                mReadableSQLiteDatabase.query(mTable, null, sql, strArgs, null, null, null)
        )
                .flatMap(cursor -> Flowable.fromIterable(() -> new CursorIterator(cursor)))
                .map(this::createModelFromCursor);
    }

    private String[] args(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        String[] a = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            a[i] = String.valueOf(args[i]);
        }
        return a;
    }

    private String[] arg(Object value) {
        return new String[]{String.valueOf(value)};
    }


    private static class CursorIterator implements Iterator<Cursor> {

        private final Cursor mCursor;

        private CursorIterator(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public boolean hasNext() {
            boolean next = mCursor.moveToNext();
            if (!next) {
                mCursor.close();
            }
            return next;
        }

        @Override
        public Cursor next() {
            return mCursor;
        }
    }
}
