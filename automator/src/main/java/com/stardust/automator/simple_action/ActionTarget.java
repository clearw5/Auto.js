package com.stardust.automator.simple_action;

import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by Stardust on 2017/1/27.
 */

public interface ActionTarget {

    SimpleAction createAction(int action, Object... params);

    class TextActionTarget implements ActionTarget {

        String mText;
        int mIndex;

        public TextActionTarget(String text, int i) {
            mText = text;
            mIndex = i;
        }

        @Override
        public SimpleAction createAction(int action, Object... params) {
            return ActionFactory.createActionWithTextFilter(action, mText, mIndex);
        }
    }

    class BoundsActionTarget implements ActionTarget {

        Rect mBoundsInRect;

        public BoundsActionTarget(Rect rect) {
            mBoundsInRect = rect;
        }

        @Override
        public SimpleAction createAction(int action, Object... params) {
            return ActionFactory.createActionWithBoundsFilter(action, mBoundsInRect);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class EditableActionTarget implements ActionTarget {
        private int mIndex;

        public EditableActionTarget(int index) {
            mIndex = index;
        }

        @Override
        public SimpleAction createAction(int action, Object... params) {
            return ActionFactory.createActionWithEditableFilter(action, mIndex, params[0].toString());
        }
    }

    class IdActionTarget implements ActionTarget {
        private String mId;

        public IdActionTarget(String id) {
            mId = id;
        }

        @Override
        public SimpleAction createAction(int action, Object... params) {
            return ActionFactory.createActionWithIdFilter(action, mId);
        }
    }
}
