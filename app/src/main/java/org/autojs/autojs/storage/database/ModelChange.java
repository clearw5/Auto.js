package org.autojs.autojs.storage.database;

import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Stardust on 2017/11/28.
 */

public class ModelChange<M> {

    private final M mData;
    private final BaseModel.Action mAction;

    public ModelChange(M data, BaseModel.Action action) {
        mData = data;
        mAction = action;
    }

    public M getData() {
        return mData;
    }

    public BaseModel.Action getAction() {
        return mAction;
    }

    @Override
    public String toString() {
        return "ModelChange{" +
                "mData=" + mData +
                ", mAction=" + mAction +
                '}';
    }
}
