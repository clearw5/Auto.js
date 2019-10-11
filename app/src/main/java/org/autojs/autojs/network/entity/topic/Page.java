
package org.autojs.autojs.network.entity.topic;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Page {

    @SerializedName("active")
    private boolean mActive;
    @SerializedName("page")
    private long mPage;

    public boolean getActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public long getPage() {
        return mPage;
    }

    public void setPage(long page) {
        mPage = page;
    }

}
