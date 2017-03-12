package com.stardust.automator;

import android.os.Bundle;

/**
 * Created by Stardust on 2017/3/9.
 */

public abstract class ActionArgument {

    protected final String mKey;

    private ActionArgument(String key) {
        mKey = key;
    }

    public abstract void putIn(Bundle bundle);

    public static class IntActionArgument extends ActionArgument {

        private final int mInt;

        public IntActionArgument(String name, int i) {
            super(name);
            mInt = i;
        }

        @Override
        public void putIn(Bundle bundle) {
            bundle.putInt(mKey, mInt);
        }
    }

    public static class CharSequenceActionArgument extends ActionArgument {

        private final CharSequence mCharSequence;

        public CharSequenceActionArgument(String name, CharSequence charSequence) {
            super(name);
            mCharSequence = charSequence;
        }

        @Override
        public void putIn(Bundle bundle) {
            bundle.putCharSequence(mKey, mCharSequence);
        }
    }

    public static class FloatActionArgument extends ActionArgument {
        private final float mFloat;

        public FloatActionArgument(String name, float value) {
            super(name);
            mFloat = value;
        }

        @Override
        public void putIn(Bundle bundle) {
            bundle.putFloat(mKey, mFloat);
        }
    }

}
