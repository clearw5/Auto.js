package com.stardust.autojs.core.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.ResultSet;

public class Transaction {

    private final SQLiteDatabase mDatabase;

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public Transaction(SQLiteDatabase database) {
        mDatabase = database;
    }

    public void executeSql(String sqlStatement, String[] arguments, StatementCallback callback, StatementErrorCallback errorCallback){
        Cursor cursor = mDatabase.rawQuery(sqlStatement,  arguments);
        callback.handleEvent(this, DatabaseResultSet.fromCursor(cursor));
    }

     void succeed() {
        mDatabase.setTransactionSuccessful();
    }

     void end() {
        mDatabase.endTransaction();
    }
}
