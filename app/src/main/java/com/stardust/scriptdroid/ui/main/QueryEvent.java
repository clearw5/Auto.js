package com.stardust.scriptdroid.ui.main;

/**
 * Created by Stardust on 2017/10/25.
 */

public class QueryEvent {

    public static final QueryEvent CLEAR = new QueryEvent(null);

    private final String mQuery;

    public QueryEvent(String query) {
        mQuery = query;
    }

    public String getQuery() {
        return mQuery;
    }
}
