package com.stardust.scriptdroid.statics;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteCreator;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

/**
 * Created by Stardust on 2017/5/5.
 */

@StorIOSQLiteType(table = SQLiteStaticsStorage.TABLE_NAME)

public class ScriptStaticsRecord {

    @StorIOSQLiteColumn(name = "name", key = true)
    public String name;

    @StorIOSQLiteColumn(name = "times")
    public int times;

    public ScriptStaticsRecord(String name, int times) {
        this.name = name;
        this.times = times;
    }

    public ScriptStaticsRecord() {
    }
}
