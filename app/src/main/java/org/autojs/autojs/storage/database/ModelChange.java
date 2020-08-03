package org.autojs.autojs.storage.database;


/**
 * Created by Stardust on 2017/11/28.
 */

public class ModelChange<M> {

    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;


    private final M mData;
    private final int mAction;

    public ModelChange(M data, int action) {
        mData = data;
        mAction = action;
    }

    public M getData() {
        return mData;
    }

    public int getAction() {
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
