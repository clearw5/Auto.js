package com.stardust.scriptdroid.droid.runtime.action;

import android.graphics.Rect;

/**
 * Created by Stardust on 2017/1/27.
 */

public interface ActionTarget {

    Action createAction(int action, Object... params);

    class TextActionTarget implements ActionTarget {

        String mText;

        public TextActionTarget(String text) {
            mText = text;
        }

        @Override
        public Action createAction(int action, Object... params) {
            return ActionFactory.createActionWithTextFilter(action, mText);
        }
    }

    class BoundsActionTarget implements ActionTarget {

        Rect mBoundsInRect;

        public BoundsActionTarget(Rect rect) {
            mBoundsInRect = rect;
        }

        @Override
        public Action createAction(int action, Object... params) {
            return ActionFactory.createActionWithBoundsFilter(action, mBoundsInRect);
        }
    }

    class EditableActionTarget implements ActionTarget {
        private int mIndex;

        public EditableActionTarget(int index) {
            mIndex = index;
        }

        @Override
        public Action createAction(int action, Object... params) {
            return ActionFactory.createActionWithEditableFilter(action, mIndex, params[0].toString());
        }
    }
}
