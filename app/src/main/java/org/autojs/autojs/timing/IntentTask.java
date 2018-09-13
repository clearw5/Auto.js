package org.autojs.autojs.timing;

import android.content.IntentFilter;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.autojs.autojs.storage.database.IntentTaskDatabase;
import org.autojs.autojs.storage.database.TimedTaskDatabase;

@Table(database = IntentTaskDatabase.class)
public class IntentTask {

    @PrimaryKey(autoincrement = true, quickCheckAutoIncrement = true)
    @Column(name = "id")
    int mId = -1;

    @NotNull
    @Column(name = "script_path")
    String mScriptPath;

    @Column(name = "action")
    String mAction;

    @Column(name = "category")
    String mCategory;

    @Column(name = "data_type")
    String mDataType;

    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        if (mAction != null) {
            filter.addAction(mAction);
        }
        if (mCategory != null) {
            filter.addCategory(mCategory);
        }
        if (mDataType != null) {
            try {
                filter.addDataType(mDataType);
            } catch (IntentFilter.MalformedMimeTypeException e) {
                e.printStackTrace();
            }
        }
        return filter;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getScriptPath() {
        return mScriptPath;
    }

    public void setScriptPath(String scriptPath) {
        mScriptPath = scriptPath;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        mAction = action;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getDataType() {
        return mDataType;
    }

    public void setDataType(String dataType) {
        mDataType = dataType;
    }
}
