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

        @Override
        public String toString() {
            return "depth";
        }
    };

    public static final IntProperty ROW = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.row();
        }

        @Override
        public String toString() {
            return "row";
        }
    };

    public static final IntProperty ROW_COUNT = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.rowCount();
        }

        @Override
        public String toString() {
            return "rowCount";
        }
    };

    public static final IntProperty ROW_SPAN = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.rowSpan();
        }

        @Override
        public String toString() {
            return "rowSpan";
        }
    };

    public static final IntProperty COLUMN = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.column();
        }

        @Override
        public String toString() {
            return "column";
        }
    };

    public static final IntProperty COLUMN_COUNT = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.columnCount();
        }

        @Override
        public String toString() {
            return "columnCount";
        }
    };

    public static final IntProperty COLUMN_SPAN = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.columnSpan();
        }

        @Override
        public String toString() {
            return "columnSpan";
        }
    };

    public static final IntProperty INDEX_IN_PARENT = new IntProperty() {
        @Override
        public int get(UiObject object) {
            return object.indexInParent();
        }

        @Override
        public String toString() {
            return "indexInParent";
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

    @Override
    public String toString() {
        return mIntProperty.toString() + "(" + mValue + ")";
    }
}
