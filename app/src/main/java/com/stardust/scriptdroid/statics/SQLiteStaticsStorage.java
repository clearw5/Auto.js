package com.stardust.scriptdroid.statics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.stardust.autojs.script.ScriptSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/5.
 */

public class SQLiteStaticsStorage implements ScriptStaticsStorage {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "Ever.db";
    static final String TABLE_NAME = "FinalEating";

    private StorIOSQLite mStorIOSQLite;

    public SQLiteStaticsStorage(Context context) {
        mStorIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new SQLiteOpenHelper(context))
                .addTypeMapping(ScriptStaticsRecord.class, new ScriptStaticsRecordSQLiteTypeMapping())
                .build();
    }

    @Override
    public void record(ScriptSource source) {
        int times = getTimes(source) + 1;
        mStorIOSQLite.put()
                .object(new ScriptStaticsRecord(source.toString(), times))
                .prepare()
                .executeAsBlocking();

    }

    public int getTimes(ScriptSource source) {
        ScriptStaticsRecord record = mStorIOSQLite.get()
                .object(ScriptStaticsRecord.class)
                .withQuery(Query.builder()
                        .table(TABLE_NAME)
                        .where("name = ?")
                        .whereArgs(source.toString())
                        .build())
                .prepare()
                .executeAsBlocking();
        if (record != null) {
            return record.times;
        } else {
            return 0;
        }
    }

    @Override
    public Map<String, String> getAll() {
        List<ScriptStaticsRecord> records = mStorIOSQLite.get()
                .listOfObjects(ScriptStaticsRecord.class)
                .withQuery(Query.builder()
                        .table(TABLE_NAME)
                        .orderBy("times")
                        .build())
                .prepare()
                .executeAsBlocking();
        return toMap(records);
    }

    private Map<String, String> toMap(List<ScriptStaticsRecord> records) {
        Map<String, String> map = new HashMap<>();
        for (ScriptStaticsRecord record : records) {
            map.put(record.name, String.valueOf(record.times));
        }
        return map;
    }

    @Override
    public Map<String, String> getMax(int size) {
        List<ScriptStaticsRecord> records = mStorIOSQLite.get()
                .listOfObjects(ScriptStaticsRecord.class)
                .withQuery(Query.builder()
                        .table(TABLE_NAME)
                        .orderBy("times DESC")
                        .limit(size)
                        .build())
                .prepare()
                .executeAsBlocking();
        return toMap(records);
    }

    @Override
    public void clear() {
        mStorIOSQLite.delete()
                .byQuery(DeleteQuery.builder()
                        .table(TABLE_NAME)
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Override
    public void close() {
        try {
            mStorIOSQLite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

        SQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(\n"
                    + "name TEXT NOT NULL PRIMARY KEY, "
                    + "times INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}
