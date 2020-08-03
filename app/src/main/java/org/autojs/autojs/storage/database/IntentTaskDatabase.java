package org.autojs.autojs.storage.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.autojs.autojs.timing.IntentTask;

public class IntentTaskDatabase extends Database<IntentTask> {

    private static final int VERSION = 2;
    private static final String NAME = "IntentTaskDatabase";

    public IntentTaskDatabase(Context context) {
        super(new SQLHelper(context), IntentTask.TABLE);
    }

    @Override
    protected ContentValues asContentValues(IntentTask model) {
        ContentValues values = new ContentValues();
        values.put("script_path", model.getScriptPath());
        values.put("action", model.getAction());
        values.put("category", model.getCategory());
        values.put("data_type", model.getDataType());
        values.put("local", model.isLocal() ? 1 : 0);
        return values;
    }

    @Override
    protected IntentTask createModelFromCursor(Cursor cursor) {
        IntentTask task = new IntentTask();
        task.setId(cursor.getInt(0));
        task.setScriptPath(cursor.getString(1));
        task.setAction(cursor.getString(2));
        task.setCategory(cursor.getString(3));
        task.setDataType(cursor.getString(4));
        task.setLocal(cursor.getInt(5) != 0);
        return task;
    }


    private static class SQLHelper extends SQLiteOpenHelper {

        public SQLHelper(Context context) {
            super(context, NAME + ".db", null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE `" + IntentTask.TABLE + "`(" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`script_path` TEXT NOT NULL ON CONFLICT FAIL, " +
                    "`action` TEXT, " +
                    "`category` TEXT, " +
                    "`data_type` TEXT, " +
                    "`local` INT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 1 && newVersion == 2) {
                db.execSQL("ALTER TABLE " + IntentTask.TABLE + "\n" +
                        "ADD local INT");
            }
        }
    }
}
