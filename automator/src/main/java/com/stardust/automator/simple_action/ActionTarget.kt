package com.stardust.automator.simple_action

import android.graphics.Rect
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Created by Stardust on 2017/1/27.
 */

interface ActionTarget {

    fun createAction(action: Int, vararg params: Any): SimpleAction

    class TextActionTarget(private var mText: String, internal var mIndex: Int) : ActionTarget {

        override fun createAction(action: Int, vararg params: Any): SimpleAction {
            return ActionFactory.createActionWithTextFilter(action, mText, mIndex)
        }
    }

    class BoundsActionTarget(private var mBoundsInRect: Rect) : ActionTarget {

        override fun createAction(action: Int, vararg params: Any): SimpleAction {
            return ActionFactory.createActionWithBoundsFilter(action, mBoundsInRect)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class EditableActionTarget(private val mIndex: Int) : ActionTarget {

        override fun createAction(action: Int, vararg params: Any): SimpleAction {
            return ActionFactory.createActionWithEditableFilter(action, mIndex, params[0].toString())
        }
    }

    class IdActionTarget(private val mId: String) : ActionTarget {

        override fun createAction(action: Int, vararg params: Any): SimpleAction {
            return ActionFactory.createActionWithIdFilter(action, mId)
        }
    }
}
