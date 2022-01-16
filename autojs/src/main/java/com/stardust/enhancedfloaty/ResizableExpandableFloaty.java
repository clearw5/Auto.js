package com.stardust.enhancedfloaty;

import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by Stardust on 2017/4/19.
 */

public interface ResizableExpandableFloaty {


    View inflateCollapsedView(FloatyService service, ResizableExpandableFloatyWindow window);

    View inflateExpandedView(FloatyService service, ResizableExpandableFloatyWindow window);

    @Nullable
    View getResizerView(View expandedView);

    @Nullable
    View getMoveCursorView(View expandedView);

    float getCollapsedHiddenWidthRadio();

    float getCollapsedViewUnpressedAlpha();

    float getCollapsedViewPressedAlpha();


    boolean shouldRequestFocusWhenExpand();


    int getInitialX();


    int getInitialY();

    int getInitialHeight();

    int getInitialWidth();

    boolean isInitialExpanded();

    abstract class AbstractResizableExpandableFloaty implements ResizableExpandableFloaty {
        private float mCollapsedHiddenWidthRadio = 0f;
        private float mCollapsedViewUnpressedAlpha = 0.7f;
        private float mCollapsedViewPressedAlpha = 1.0f;
        private boolean mShouldRequestFocusWhenExpand = true;
        private int mInitialX;
        private int mInitialY;
        private boolean mInitialExpanded = false;
        private int mInitialHeight;
        private int mInitialWidth;

        @Nullable
        public View getResizerView(View expandedView) {
            return null;
        }

        @Nullable
        public View getMoveCursorView(View expandedView) {
            return null;
        }

        public float getCollapsedHiddenWidthRadio() {
            return mCollapsedHiddenWidthRadio;
        }

        public void setCollapsedHiddenWidthRadio(float collapsedHiddenWidthRadio) {
            this.mCollapsedHiddenWidthRadio = collapsedHiddenWidthRadio;
        }

        public float getCollapsedViewUnpressedAlpha() {
            return mCollapsedViewUnpressedAlpha;
        }

        public void setCollapsedViewUnpressedAlpha(float collapsedViewUnpressedAlpha) {
            mCollapsedViewUnpressedAlpha = collapsedViewUnpressedAlpha;
        }

        public float getCollapsedViewPressedAlpha() {
            return mCollapsedViewPressedAlpha;
        }

        public void setCollapsedViewPressedAlpha(float collapsedViewPressedAlpha) {
            mCollapsedViewPressedAlpha = collapsedViewPressedAlpha;
        }

        public boolean shouldRequestFocusWhenExpand() {
            return mShouldRequestFocusWhenExpand;
        }

        public void setShouldRequestFocusWhenExpand(boolean requestFocusWhenExpand) {
            mShouldRequestFocusWhenExpand = requestFocusWhenExpand;
        }


        public int getInitialX() {
            return mInitialX;
        }

        public void setInitialX(int initialX) {
            mInitialX = initialX;
        }

        public int getInitialY() {
            return mInitialY;
        }

        public void setInitialY(int initialY) {
            mInitialY = initialY;
        }

        public boolean isInitialExpanded() {
            return mInitialExpanded;
        }

        public void setInitialExpanded(boolean initialExpanded) {
            mInitialExpanded = initialExpanded;
        }

        @Override
        public int getInitialHeight() {
            return mInitialHeight;
        }

        public void setInitialHeight(int initialHeight) {
            mInitialHeight = initialHeight;
        }

        @Override
        public int getInitialWidth() {
            return mInitialWidth;
        }

        public void setInitialWidth(int initialWidth) {
            mInitialWidth = initialWidth;
        }
    }
}
