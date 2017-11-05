package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

/**
 * Created by Stardust on 2017/11/5.
 */

public class IntFilter extends DfsFilter {

    public interface IntProperty {
        int get(UiObject object);
    }

    public static final IntProperty DEPTH = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.depth();
        }
    };

    public static final IntProperty ROW = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.row();
        }
    };

    public static final IntProperty ROW_COUNT = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.rowCount();
        }
    };

    public static final IntProperty ROW_SPAN = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.rowSpan();
        }
    };

    public static final IntProperty COLUMN = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.column();
        }
    };

    public static final IntProperty COLUMN_COUNT = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.columnCount();
        }
    };

    public static final IntProperty COLUMN_SPAN = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.columnSpan();
        }
    };

    private IntProperty mIntProperty;
    private int mValue;

    public IntFilter(IntProperty intProperty, int value) {
        mIntProperty = intProperty;
        mValue = value;
    }

    @Override
    protected boolean isIncluded(UiObject nodeInfo) {
        return mIntProperty.get(nodeInfo) == mValue;
    }
}
