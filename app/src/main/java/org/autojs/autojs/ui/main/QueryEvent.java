package org.autojs.autojs.ui.main;

/**
 * Created by Stardust on 2017/10/25.
 */

public class QueryEvent {

    public static final QueryEvent CLEAR = new QueryEvent(null);
    public static final QueryEvent FIND_FORWARD = new QueryEvent("", true);

    private boolean mShouldCollapseSearchView = false;
    private final String mQuery;
    private final boolean mFindForward;

    public QueryEvent(String query, boolean b) {
        mQuery = query;
        mFindForward = b;
    }

    public QueryEvent(String query) {
        this(query, false);
    }

    public String getQuery() {
        return mQuery;
    }

    public void collapseSearchView() {
        mShouldCollapseSearchView = true;
    }

    public boolean shouldCollapseSearchView() {
        return mShouldCollapseSearchView;
    }

    public boolean isFindForward() {
        return mFindForward;
    }
}

