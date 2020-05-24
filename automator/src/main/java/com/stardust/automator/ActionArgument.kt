package com.stardust.automator

import android.os.Bundle

/**
 * Created by Stardust on 2017/3/9.
 */

abstract class ActionArgument private constructor(protected val mKey: String) {

    abstract fun putIn(bundle: Bundle)

    class IntActionArgument(name: String, private val mInt: Int) : ActionArgument(name) {

        override fun putIn(bundle: Bundle) {
            bundle.putInt(mKey, mInt)
        }
    }

    class CharSequenceActionArgument(name: String, private val mCharSequence: CharSequence) : ActionArgument(name) {

        override fun putIn(bundle: Bundle) {
            bundle.putCharSequence(mKey, mCharSequence)
        }
    }

    class FloatActionArgument(name: String, private val mFloat: Float) : ActionArgument(name) {

        override fun putIn(bundle: Bundle) {
            bundle.putFloat(mKey, mFloat)
        }
    }

}
