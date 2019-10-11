package org.autojs.autojs.ui.edit.debug;

public class WatchingVariable {

    private String mDisplayName;
    private String mName;
    private final boolean mPinned;
    private String mValue;
    private String mSingleLineValue;

    public WatchingVariable(String displayName, String name, boolean pinned) {
        mDisplayName = displayName;
        mName = name;
        mPinned = pinned;
    }

    public WatchingVariable(String name) {
        this(name, name, false);
    }

    public boolean isPinned() {
        return mPinned;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
        mSingleLineValue = value == null ? null : value.replaceAll("\n", " ");
    }

    public String getSingleLineValue() {
        return mSingleLineValue;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getName() {
        return mName;
    }
}
