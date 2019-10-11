package com.stardust.autojs.core.database;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseResultSet {


    public static class RowList {

        public final int length;
        private final ArrayList<Map<String, Object>> mData;


        public RowList(ArrayList<Map<String, Object>> data) {
            mData = data;
            length = mData.size();
        }

        public Object item(int i) {
            return mData.get(i);
        }
    }

    public final long insertId;
    public final long rowsAffected;
    public final RowList rows;

    public static DatabaseResultSet fromCursor(Cursor cursor) {
        ArrayList<Map<String, Object>> rows = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return null;
        }
        int columnCount = cursor.getColumnCount();
        long insertId = cursor.getLong(0);
        rows.add(readRowAsMap(cursor, columnCount));
        while (cursor.moveToNext()) {
            rows.add(readRowAsMap(cursor, columnCount));
        }
        cursor.close();
        return new DatabaseResultSet(insertId, new RowList(rows));
    }

    private static Map<String, Object> readRowAsMap(Cursor cursor, int columnCount) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < columnCount; i++) {
            map.put(cursor.getColumnName(i), CursorHelper.getValue(cursor, columnCount));
        }
        return map;
    }

    public DatabaseResultSet(long insertId, RowList rowList) {
        this.insertId = insertId;
        this.rowsAffected = rowList.length;
        this.rows = rowList;
    }

}
