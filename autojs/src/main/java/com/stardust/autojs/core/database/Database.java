package com.stardust.autojs.core.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteTransactionListener;

public class Database {


    private DatabasesOpenHelper mHelper;
    private SQLiteDatabase mWritableDatabase;
    private SQLiteDatabase mReadableDatabase;

    public void executeSql(String sql){
        mWritableDatabase.execSQL(sql);
    }

    public void transaction(TransactionCallback callback, TransactionErrorCallback errorCallback, DatabaseVoidCallback successCallback){
        transactionInternal(mWritableDatabase, callback, errorCallback, successCallback);
    }

    public void readTransaction(TransactionCallback callback, TransactionErrorCallback errorCallback, DatabaseVoidCallback successCallback){
        transactionInternal(mReadableDatabase, callback, errorCallback, successCallback);
    }

    public void changeVersion(int oldVersion, int newVersion, TransactionCallback callback, TransactionErrorCallback errorCallback, DatabaseVoidCallback successCallback){
        transactionInternal(mWritableDatabase, new TransactionCallback() {
            @Override
            public void handleEvent(Transaction transaction) {
                //TODO
                if(transaction.getDatabase().getVersion() == oldVersion){
                    transaction.getDatabase().setVersion(newVersion);
                    callback.handleEvent(transaction);
                }
            }
        }, errorCallback, successCallback);
    }

    private void transactionInternal(SQLiteDatabase database, TransactionCallback callback, TransactionErrorCallback errorCallback, DatabaseVoidCallback successCallback){
        database.beginTransactionWithListener(new SQLiteTransactionListener() {
            @Override
            public void onBegin() {
                Transaction transaction = new Transaction(database);
                try {
                    callback.handleEvent(transaction);
                    transaction.succeed();
                }catch (SQLException e){
                    errorCallback.handleEvent(e);
                }finally {
                    transaction.end();
                }
            }

            @Override
            public void onCommit() {
                successCallback.handleEvent();
            }

            @Override
            public void onRollback() {
            }
        });
    }


    private static class DatabasesOpenHelper extends SQLiteOpenHelper {

        public DatabasesOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DatabasesOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
