package org.autojs.autojs.storage.database;


public abstract class BaseModel {
    private long mId;

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel baseModel = (BaseModel) o;
        return mId == baseModel.mId;
    }

    @Override
    public int hashCode() {
        return (int)(mId ^ (mId >>> 32));
    }
}
