package com.stardust.hover;

/**
 * Created by Stardust on 2017/3/11.
 */


import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.R;
import io.mattcarroll.hover.HoverMenuAdapter;
import io.mattcarroll.hover.defaulthovermenu.CollapsedMenuAnchor;
import io.mattcarroll.hover.defaulthovermenu.DefaultNavigator;
import io.mattcarroll.hover.defaulthovermenu.Dragger;
import io.mattcarroll.hover.defaulthovermenu.HoverMenuContentView;
import io.mattcarroll.hover.defaulthovermenu.MagnetPositioner;
import io.mattcarroll.hover.defaulthovermenu.Positionable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * {@code HoverMenuView} is a floating menu implementation. This implementation displays tabs along
 * the top of its display, from right to left. Below the tabs, filling the remainder of the display
 * is a content region that displays the content for a selected tab.  The content region includes
 * a visual indicator showing which tab is currently selected.  Each tab's content includes a title
 * and a visual area.  The visual area can display any {@code View}.
 * <p>
 * {@code HoverMenuView} cannot be used in XML because it requires additional parameters in its
 * constructor.
 */
@SuppressLint("ViewConstructor")
public class HoverMenuView extends RelativeLayout {

    private static final String TAG = "HoverMenuView";

    private static final float EXIT_RADIUS_IN_DP = 75;

    private static final int EXPANDED = 1;
    private static final int COLLAPSED = 2;
    private static final int TRANSITIONING = 3;

    //------ Views From Layout XML -------
    private View mTabAnchorView;
    private HoverMenuContentView mContentView; // Content view to display a menu
    private Navigator mNavigator;
    private View mShadeView; // Dark backdrop that fills screen behind menu
    private View mExitGradientBackground; // Dark gradient that appears behind exit view on lower part of screen
    private View mExitView; // An "x" that appears near bottom of screen to signify "exit" from floating menu

    private View mLastTabInStrip;
    private TabSelectionListener mTabSelectionListener;

    private List<Animator> mMotionAnimations = new ArrayList<>();
    private Point mDraggingPoint = new Point();
    private float mExitRadiusInPx; // Radius around mCenterOfExitView where the "primary" bubble can be dropped to signify an exit
    private boolean mIsExitRegionActivated = false;

    private HoverMenuAdapter mAdapter;
    private int mTabSizeInPx;
    private List<String> mTabIds = new ArrayList<>();
    private String mActiveTabId;
    private View mActiveTab; // The tab whose menu is currently displayed, and the tab that will represent the collapsed menu.

    private int mVisualState;
    private CollapsedMenuAnchor mMenuAnchor;
    private Dragger mWindowDragWatcher;
    private HoverMenuTransitionListener mTransitionListener;
    private HoverMenuExitRequestListener mExitRequestListener;

    private HoverMenuContentView.HoverMenuContentResizer mHoverMenuContentResizer = new HoverMenuContentView.HoverMenuContentResizer() {
        @Override
        public void makeHoverMenuContentFullscreen() {
            RelativeLayout.LayoutParams contentContainerLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            contentContainerLayoutParams.height = 0;
            contentContainerLayoutParams.addRule(RelativeLayout.BELOW, R.id.view_tab_strip_anchor);
            contentContainerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            contentContainerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            contentContainerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mContentView.setLayoutParams(contentContainerLayoutParams);
        }

        @Override
        public void makeHoverMenuContentAsTallAsItsContent() {
            RelativeLayout.LayoutParams contentContainerLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            contentContainerLayoutParams.addRule(RelativeLayout.BELOW, R.id.view_tab_strip_anchor);
            contentContainerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            contentContainerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mContentView.setLayoutParams(contentContainerLayoutParams);
        }
    };

    private OnTouchListener mTabOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                showTabAsPressed(view);
            } else if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                showTabAsNotPressed(view);
            }

            return false;
        }
    };

    private OnClickListener mTabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View tab) {
            String id = (String) tab.getTag(R.string.floatingmenuview_tabview_id);

            // If this is a selection of the tab that is already active, then collapse the menu.
            if (id.equals(mActiveTabId)) {
                collapse();
            }

            // Select the clicked tab.
            selectTab(id);
        }
    };

    private Positionable mSidePullerPositioner = new Positionable() {
        @Override
        public void setPosition(@NonNull Point position) {
            setCollapsedPosition(position.x, position.y);
        }
    };

    private CollapsedMenuViewController mCollapsedMenuViewController = new CollapsedMenuViewController() {

        @Override
        public void onPress() {
            onPressOfCollapsedMenu();
            startDragMode();
        }

        @Override
        public void onDragTo(@NonNull Point dragCenterPosition) {
            int left = dragCenterPosition.x - (getActiveTab().getWidth() / 2);
            int top = dragCenterPosition.y - (getActiveTab().getHeight() / 2);

            setCollapsedPosition(left, top);

            checkForExitRegionActivation();
        }

        @Override
        public void onRelease() {
            stopDragMode();
            onReleaseOfCollapsedMenu();
        }

        @Override
        public void pullToAnchor() {
            Log.d(TAG, "pullToAnchor()");
            MagnetPositioner magnetPositioner = new MagnetPositioner(getResources().getDisplayMetrics(), mSidePullerPositioner, new MagnetPositioner.OnCompletionListener() {
                @Override
                public void onPullToSideCompleted() {
                    // TODO: this collapsed logic is duplicated
                    mVisualState = COLLAPSED;
                    enableDragging();
                    mTransitionListener.onCollapsed();
                }
            });

            View activeTab = getActiveTab();
            Log.d(TAG, "Active tab location - left: " + activeTab.getX() + ", top: " + activeTab.getY());
            Rect tabViewBounds = new Rect(
                    (int) activeTab.getX(),
                    (int) activeTab.getY(),
                    (int) activeTab.getX() + activeTab.getWidth(),
                    (int) activeTab.getY() + activeTab.getHeight());

            // Update the anchor location.
            mMenuAnchor.setAnchorByInterpolation(tabViewBounds);
            Log.d(TAG, "New anchor normalized Y: " + mMenuAnchor.getAnchorNormalizedY());

            // Pull the tab bounds to the anchor position.
            magnetPositioner.pullToAnchor(mMenuAnchor, tabViewBounds, new BounceInterpolator());
        }
    };

    private Dragger.DragListener mDragListener = new Dragger.DragListener() {
        @Override
        public void onPress(float x, float y) {
            getCollapsedMenuViewController().onPress();
        }

        @Override
        public void onDragStart(float x, float y) {
        }

        @Override
        public void onDragTo(float x, float y) {
            getCollapsedMenuViewController().onDragTo(new Point(
                    (int) x + (getActiveTab().getWidth() / 2),
                    (int) y + (getActiveTab().getHeight() / 2))
            );
        }

        @Override
        public void onReleasedAt(float x, float y) {
            Log.d(TAG, "Menu released at - x: " + x + ", y: " + y);
            // Update the visual pressed state of the hover menu.
            getCollapsedMenuViewController().onRelease();

            if (isActiveTabInExitRegion()) {
                // Notify our listener that an exit has been requested.
                Log.d(TAG, "Hover menu dropped in exit region. Requesting exit.");
                mExitRequestListener.onExitRequested();
            } else {
                // The user did not choose to exit the menu so pull the menu to the side of the screen.
                getCollapsedMenuViewController().pullToAnchor();
            }
        }

        @Override
        public void onTap() {
            // Update the visual pressed state of the hover menu.
            getCollapsedMenuViewController().onRelease();

            expand();
        }
    };

    private final HoverMenuAdapter.ContentChangeListener mAdapterListener = new HoverMenuAdapter.ContentChangeListener() {
        @Override
        public void onContentChange(@NonNull HoverMenuAdapter adapter) {
            // Update all the tab views.
            removeAllTabs();
            addAllTabs();

            // TODO: need null check in case selected tab is gone.
            Log.d(TAG, "Content change. Active id: " + mActiveTabId);
            Log.d(TAG, "Active tab view: " + findViewById(mActiveTabId.hashCode()));
            mActiveTab = findViewById(mActiveTabId.hashCode());
            mContentView.setActiveTab(mActiveTab);
        }
    };

    public HoverMenuView(Context context, @Nullable Navigator navigator, @NonNull Dragger windowDragWatcher,
                         @NonNull PointF savedAnchor) {
        super(context);
        mWindowDragWatcher = windowDragWatcher;
        mMenuAnchor = new CollapsedMenuAnchor(getResources().getDisplayMetrics(), 10);
        mMenuAnchor.setAnchorAt((int) savedAnchor.x, savedAnchor.y); // TODO: savedAnchor isn't really a position, its a tuple of values (side, y-pos)
        mNavigator = null == navigator ? new DefaultNavigator(context) : navigator;
        init();
    }

    private void init() {
        Log.d(TAG, "init()");
        LayoutInflater.from(getContext()).inflate(R.layout.view_hover_menu, this, true);

        mTabSizeInPx = getResources().getDimensionPixelSize(R.dimen.floating_icon_size);

        mTabAnchorView = findViewById(R.id.view_tab_strip_anchor);
        mContentView = (HoverMenuContentView) findViewById(R.id.view_content);
        mShadeView = findViewById(R.id.view_shade);
        mExitGradientBackground = findViewById(R.id.view_exit_gradient);
        mExitView = findViewById(R.id.view_exit);
        mExitRadiusInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EXIT_RADIUS_IN_DP, getResources().getDisplayMetrics());

        mContentView.setContentResizer(mHoverMenuContentResizer);
        mContentView.setNavigator(mNavigator);

        initLayoutTransitionAnimations();

        // Initially we're not really in the EXPANDED or COLLAPSED states.
        mVisualState = TRANSITIONING;

        setTabSelectionListener(null);
        setHoverMenuTransitionListener(null);
        setHoverMenuExitRequestListener(null);
    }

    public void release() {
        mWindowDragWatcher.deactivate();
    }

    public void setContentBackgroundColor(@ColorInt int color) {
        mContentView.setBackgroundColor(color);
    }

    public CollapsedMenuViewController getCollapsedMenuViewController() {
        return mCollapsedMenuViewController;
    }

    public void collapse() {
        if (EXPANDED == mVisualState) {
            Log.d(TAG, "Collapsed Hover menu.");
            doCollapse(true);
        }
    }

    public void expand() {
        if (COLLAPSED == mVisualState) {
            Log.d(TAG, "Expanding Hover menu.");
            doExpansion(true);
        }
    }

    public void setHoverMenuTransitionListener(@Nullable HoverMenuTransitionListener transitionListener) {
        mTransitionListener = null == transitionListener ? new NoOpHoverMenuTransitionListener() : transitionListener;
    }

    public void setHoverMenuExitRequestListener(@Nullable HoverMenuExitRequestListener exitRequestListener) {
        mExitRequestListener = null == exitRequestListener ? new NoOpHoverMenuExitRequestListener() : exitRequestListener;
    }

    // TODO: create custom object to hold anchor state
    public PointF getAnchorState() {
        return new PointF(mMenuAnchor.getAnchorSide(), mMenuAnchor.getAnchorNormalizedY());
    }

    public void setAnchorState(@NonNull PointF anchorState) {
        mMenuAnchor.setAnchorAt((int) anchorState.x, anchorState.y);

        // If we're in the collapsed state, update the collapsed position to the new anchor position.
        if (COLLAPSED == mVisualState) {
            moveActiveTabToAnchor();
        }
    }

    private void doCollapse(boolean animate) {
        mVisualState = TRANSITIONING;
        mTransitionListener.onCollapsing();

        if (animate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                LayoutTransition transition = getLayoutTransition();
                transition.enableTransitionType(LayoutTransition.DISAPPEARING);
            }

            Iterator<View> tabIterator = getTailToHeadTabIterator();
            int timeUntilVisiblityChange = 0;
            while (tabIterator.hasNext()) {
                final View tabView = tabIterator.next();

                // We don't want to hide the active tab, so only hide the other tabs.
                if (getActiveTab() != tabView) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tabView.setVisibility(View.GONE);
                        }
                    }, timeUntilVisiblityChange);

                    timeUntilVisiblityChange += 50;
                }
            }

            // Disable transition animations after tabs are done animating in.
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        LayoutTransition transition = getLayoutTransition();
                        transition.disableTransitionType(LayoutTransition.DISAPPEARING);
                    }
                }
            }, timeUntilVisiblityChange);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    getShadeView().setVisibility(View.GONE);

                    // Move the active tab to the anchor position.
                    animateActiveTabToAnchorPosition();
                }
            }, timeUntilVisiblityChange);
        } else {
            // Change visibilities immediately.
            Iterator<View> tailToHeadTabIterator = getTailToHeadTabIterator();
            while (tailToHeadTabIterator.hasNext()) {
                View tabView = tailToHeadTabIterator.next();

                // We don't want to hide the very first tab, so only hide tabs that have yet another tab leading them.
                if (getActiveTab() != tabView) {
                    Log.d(TAG, "Hiding tab: " + tabView.getTag(R.string.floatingmenuview_tabview_id));
                    tabView.setVisibility(View.GONE);
                }
            }

            getShadeView().setVisibility(View.GONE);

            moveActiveTabToAnchor();
            mTransitionListener.onCollapsed();
        }

        // Close the content area.
        getContentView().setVisibility(View.GONE);
    }

    private void doExpansion(boolean animate) {
        mVisualState = TRANSITIONING;
        mTransitionListener.onExpanding();

        disableDragging();

        getShadeView().setVisibility(View.VISIBLE);

        if (animate) {
            animateActiveTabToExpandedPosition(new OvershootInterpolator());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                LayoutTransition transition = getLayoutTransition();
                transition.enableTransitionType(LayoutTransition.APPEARING);
            }

            Iterator<View> headToTailTabIterator = getHeadToTailTabIterator();
            int timeUntilVisiblityChange = 0;
            while (headToTailTabIterator.hasNext()) {
                View tabView = headToTailTabIterator.next();
                Log.d(TAG, "Showing tab: " + tabView.getTag(R.string.floatingmenuview_tabview_id));
                final View tabRef = tabView;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabRef.setVisibility(View.VISIBLE);
                    }
                }, timeUntilVisiblityChange);

                timeUntilVisiblityChange += 50;
            }

            // Disable transition animations after tabs are done animating in.
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        LayoutTransition transition = getLayoutTransition();
                        transition.disableTransitionType(LayoutTransition.APPEARING);
                    }

                    // Set state to expanded.
                    mVisualState = EXPANDED;
                    mTransitionListener.onExpanded();
                    mContentView.setActiveTab(mActiveTab);
                }
            }, timeUntilVisiblityChange);
        } else {
            // Change visibilities immediately.
            moveActiveTabTo(0, 0);

            Iterator<View> headToTailTabIterator = getHeadToTailTabIterator();
            while (headToTailTabIterator.hasNext()) {
                View tabView = headToTailTabIterator.next();
                Log.d(TAG, "Showing tab: " + tabView.getTag(R.string.floatingmenuview_tabview_id));
                tabView.setVisibility(View.VISIBLE);
            }

            // Set state to expanded.
            mVisualState = EXPANDED;
            mTransitionListener.onExpanded();
        }

        // Open the content.
        getContentView().setVisibility(View.VISIBLE);
    }

    private void enableDragging() {
        Point anchorPosition = mDraggingPoint;

        Log.d(TAG, "Enabling dragging - x: " + anchorPosition.x + ", y: " + anchorPosition.y);
        Log.d(TAG, "Drag width: " + mTabSizeInPx + ", height: " + mTabSizeInPx);

        // TODO: should create a method specifically for placing the drag bounds.
        mWindowDragWatcher.deactivate();
        mWindowDragWatcher.activate(mDragListener, new Rect(
                anchorPosition.x,
                anchorPosition.y,
                anchorPosition.x + getActiveTab().getWidth(),
                anchorPosition.y + getActiveTab().getHeight()
        ));
    }

    private void disableDragging() {
        mWindowDragWatcher.deactivate();
    }

    private void moveActiveTabToAnchor() {
        Rect anchoredBounds = mMenuAnchor.anchor(new Rect(
                (int) mActiveTab.getX(),
                (int) mActiveTab.getY(),
                (int) mActiveTab.getX() + mActiveTab.getWidth(),
                (int) mActiveTab.getY() + mActiveTab.getHeight()
        ));
        moveActiveTabTo(anchoredBounds.left, anchoredBounds.top);
    }

    private void moveActiveTabTo(int x, int y) {
        View activeTab = getActiveTab();
        if (null != activeTab) {
            activeTab.setTranslationX(x);
            activeTab.setTranslationY(y);
        }
    }

    public void setAdapter(@Nullable HoverMenuAdapter adapter) {
        Log.d(TAG, "setAdapter()");

        // Remove all existing tabs from any previous adapter.
        mActiveTabId = null;
        removeAllTabs();
        if (null != mAdapter) {
            mAdapter.removeContentChangeListener(mAdapterListener);
        }

        // Update our adapter reference.
        mAdapter = adapter;

        if (null != adapter) {
            // Create all the tabs that the new adapter wants.
            addAllTabs();

            // Start listening for adapter changes.
            mAdapter.addContentChangeListener(mAdapterListener);

            // Select the first tab.
            setActiveTab(mTabIds.get(0));

            // TODO: we might set an adapter while expanded - track down the need for this and change it.
            // We force a collapse because at this point we're in a weird initial state.
            doCollapse(true);
        }
    }

    @Nullable
    private View getActiveTab() {
        return mActiveTab;
    }

    @NonNull
    private View getShadeView() {
        return mShadeView;
    }

    @NonNull
    private HoverMenuContentView getContentView() {
        return mContentView;
    }

    @NonNull
    private Iterator<View> getHeadToTailTabIterator() {
        return new HeadToTailTabIterator(mTabAnchorView);
    }

    @NonNull
    private Iterator<View> getTailToHeadTabIterator() {
        return new TailToHeadTabIterator(mTabAnchorView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Log.d(TAG, "onLayoutChange()");
        Log.d(TAG, "New - left: " + left + ", top: " + top + ", right: " + right + ", bottom: " + bottom);
        Rect newBounds = new Rect(left, top, right, bottom);

        // Adjust anchor position.
        mMenuAnchor.setDisplayBounds(newBounds);

        if (null != mActiveTab) {
            Log.d(TAG, "Active tab - (" + mActiveTab.getX() + ", " + mActiveTab.getY() + "), width: " + mActiveTab.getWidth() + ", " + mActiveTab.getHeight());
            Log.d(TAG, "Active tab visibility: " + (mActiveTab.getVisibility() == VISIBLE ? "VISIBLE" : "NOT VISIBLE"));
        } else {
            Log.d(TAG, "Active tab is null.");
            return;
        }

        Rect anchoredBounds = mMenuAnchor.anchor(new Rect(0, 0, getActiveTab().getWidth(), getActiveTab().getHeight()));
        Log.d(TAG, "Adjusted anchor bounds at (" + anchoredBounds.left + ", " + anchoredBounds.top + ")");

        if (!changed) {
            return;
        }

        // TODO: how can we avoid this null check?
        if (null != mActiveTab) {
            Log.d(TAG, "Adjusting tab position due to layout change.");
            getCollapsedMenuViewController().onDragTo(new Point(
                    anchoredBounds.left + (mActiveTab.getWidth() / 2),
                    anchoredBounds.top + (mActiveTab.getHeight() / 2)
            ));
        } else {
            Log.d(TAG, "There is no active tab, no need to adjust positioning during layout change.");
        }

        if (mVisualState == COLLAPSED) {
            Log.d(TAG, "Restarting dragging so that the drag area is adjusted due to layout change.");
            enableDragging();
        }
    }

    public boolean isExpanded() {
        return mVisualState == EXPANDED;
    }

    private void addTab(@NonNull String id, @NonNull View tabView) {
        mTabIds.add(id);

        Log.d(TAG, "Setting tab ID to: " + id.hashCode());
        tabView.setId(id.hashCode());
        tabView.setTag(R.string.floatingmenuview_tabview_id, id);
        tabView.setOnTouchListener(mTabOnTouchListener);
        tabView.setOnClickListener(mTabOnClickListener);

        if (null == mLastTabInStrip) {
            Log.d(TAG, "Adding first tab: " + tabView.getTag(R.string.floatingmenuview_tabview_id));

            // This is the first tab in the strip.
            addTabAfter(tabView, mTabAnchorView);
        } else {
            Log.d(TAG, "Adding additional tab: " + tabView.getTag(R.string.floatingmenuview_tabview_id) + " after " + mLastTabInStrip.getTag(R.string.floatingmenuview_tabview_id));
            // There are already tabs in the strip, append this one to the end.
            addTabAfter(tabView, mLastTabInStrip);

            if (!isExpanded()) {
                tabView.setVisibility(GONE);
            }
        }

        mLastTabInStrip = tabView;
    }

    private void addTabAfter(@NonNull View tabView, @NonNull View leadingView) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mTabSizeInPx, mTabSizeInPx);
        layoutParams.addRule(LEFT_OF, leadingView.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, leadingView.getId());

        // Tell the leading view who is anchored to it.
        leadingView.setTag(tabView);

        addView(tabView, layoutParams);
    }

    private void removeTab(@NonNull String id) {
        View tabView = findViewById(id.hashCode());
        if (null != tabView) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tabView.getLayoutParams();
            int anchorViewId;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                anchorViewId = layoutParams.getRule(RelativeLayout.LEFT_OF);
            } else {
                anchorViewId = layoutParams.getRules()[RelativeLayout.LEFT_OF];
            }
            View leadingTabView = findViewById(anchorViewId);

            View trailingTabView = (View) tabView.getTag();
            if (null != trailingTabView) {
                addTabAfter(trailingTabView, leadingTabView);
            } else if (tabView == mLastTabInStrip) {
                mLastTabInStrip = leadingTabView;
            }

            removeView(tabView);
        }
    }

    private void addAllTabs() {
        for (int i = 0; i < mAdapter.getTabCount(); ++i) {
            addTab(i + "", mAdapter.getTabView(i));
            mTabIds.add(i + "");
        }
    }

    private void removeAllTabs() {
        for (String tabId : mTabIds) {
            removeView(findViewById(tabId.hashCode()));
        }

        mTabIds.clear();
        mLastTabInStrip = null;
    }

//    private void refreshTabs() {
//        int tabCount = mAdapter.getTabCount();
//        for (int i = 0; i < tabCount; ++i) {
//            replaceTabView()
//        }
//    }

    private void selectTab(@NonNull String id) {
        Log.d(TAG, "Selecting tab: " + id.hashCode());
        setActiveTab(id);
        mTabSelectionListener.onTabSelected(id);
    }

    private void setActiveTab(String id) {
        if (id.equals(mActiveTabId)) {
            // This tab is already selected.
            return;
        }

        mActiveTabId = id;
        mActiveTab = findViewById(id.hashCode());
        mContentView.setActiveTab(mActiveTab);

        // This is a top-level menu item so clear all content from the menu to start fresh.
//        mContentView.clearContent();
        mNavigator.clearContent();

        // Activate the chosen tab.
//        mAdapter.getNavigatorContent(Integer.parseInt(id)).execute(getContext(), mContentView);
//        mContentView.pushContent(mAdapter.getNavigatorContent(Integer.parseInt(id)));
        mNavigator.pushContent(mAdapter.getNavigatorContent(Integer.parseInt(id)));
    }

    public void setTabSelectionListener(@Nullable TabSelectionListener tabSelectionListener) {
        if (null != tabSelectionListener) {
            mTabSelectionListener = tabSelectionListener;
        } else {
            mTabSelectionListener = new NoOpTabSelectionListener();
        }
    }

    public boolean onBackPressed() {
        if (isExpanded() && !mContentView.onBackPressed()) {
            collapse();
            return true;
        } else {
            return false;
        }
    }

    private void onPressOfCollapsedMenu() {
        // Scale down the active tab to give "pressed" effect.
        if (null != mActiveTab) {
            showTabAsPressed(mActiveTab);
        }
    }

    private void onReleaseOfCollapsedMenu() {
        // Return scale of the active tab to normal state.
        if (null != mActiveTab) {
            showTabAsNotPressed(mActiveTab);
        }
    }

    private void onMenuCollapsed() {
        mVisualState = COLLAPSED;
        enableDragging();
        mTransitionListener.onCollapsed();
    }

    private void showTabAsPressed(@NonNull View tabView) {
        tabView.setScaleX(0.95f);
        tabView.setScaleY(0.95f);
    }

    private void showTabAsNotPressed(@NonNull View tabView) {
        tabView.setScaleX(1.0f);
        tabView.setScaleY(1.0f);
    }

    private void startDragMode() {
        Log.d(TAG, "startDragMode()");
        mExitGradientBackground.setAlpha(0.0f);
        ObjectAnimator.ofFloat(mExitGradientBackground, "alpha", 0.0f, 1.0f).setDuration(300).start();
        mExitGradientBackground.setVisibility(VISIBLE);

        LayoutTransition transition = getLayoutTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.enableTransitionType(LayoutTransition.APPEARING);
        }

        mExitView.setVisibility(VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.disableTransitionType(LayoutTransition.APPEARING);
        }
    }

    private void stopDragMode() {
        Log.d(TAG, "stopDragMode()");
        ObjectAnimator animator = ObjectAnimator.ofFloat(mExitGradientBackground, "alpha", 1.0f, 0.0f).setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mExitGradientBackground.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.start();

        LayoutTransition transition = getLayoutTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.enableTransitionType(LayoutTransition.DISAPPEARING);
        }

        mExitView.setVisibility(INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.disableTransitionType(LayoutTransition.DISAPPEARING);
        }
    }

    private void checkForExitRegionActivation() {
        if (!mIsExitRegionActivated && isActiveTabInExitRegion()) {
            activateExitRegion();
        } else if (mIsExitRegionActivated && !isActiveTabInExitRegion()) {
            deactivateExitRegion();
        }
    }

    private void activateExitRegion() {
        mIsExitRegionActivated = true;

        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);

        mExitView.setScaleX(1.25f);
        mExitView.setScaleY(1.25f);
    }

    private void deactivateExitRegion() {
        mIsExitRegionActivated = false;

        mExitView.setScaleX(1.0f);
        mExitView.setScaleY(1.0f);
    }

    private void setCollapsedPosition(int x, int y) {
        Log.d(TAG, "Setting collapsed position - x: " + x + ", y: " + y);
        mDraggingPoint.set(x, y);

        if (!isExpanded() && null != mActiveTab) {
//            Log.d(TAG, "Setting collapsed menu position (dragging) - x: " + mDraggingPoint.x + ", y: " + mDraggingPoint.y);
            mActiveTab.setX(mDraggingPoint.x);
            mActiveTab.setY(mDraggingPoint.y);
        }
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (KeyEvent.ACTION_UP == event.getAction() && KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            return onBackPressed();
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private void initLayoutTransitionAnimations() {
        setLayoutTransition(new LayoutTransition());
        final LayoutTransition transition = getLayoutTransition();

        transition.setAnimator(LayoutTransition.APPEARING, createEnterObjectAnimator());

        transition.setAnimator(LayoutTransition.DISAPPEARING, createExitObjectAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.disableTransitionType(LayoutTransition.APPEARING);
            transition.disableTransitionType(LayoutTransition.DISAPPEARING);
            transition.disableTransitionType(LayoutTransition.CHANGE_APPEARING);
            transition.disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
            transition.disableTransitionType(LayoutTransition.CHANGING);
        }
    }

    private ObjectAnimator createEnterObjectAnimator() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f);

        // Target object doesn't matter because it is overriden by layout system.
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(new Object(), scaleX, scaleY);
        animator.setDuration(500);
        animator.setInterpolator(new OvershootInterpolator());
        return animator;
    }

    private ObjectAnimator createExitObjectAnimator() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.1f);

        // Target object doesn't matter because it is overriden by layout system.
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(new Object(), scaleX, scaleY);
        animator.setDuration(500);
        animator.setInterpolator(new AnticipateInterpolator());
        return animator;
    }

    private void animateActiveTabToAnchorPosition() {
        Log.d(TAG, "animateActiveTabToAnchorPosition()");
        MagnetPositioner magnetPositioner = new MagnetPositioner(getResources().getDisplayMetrics(), mSidePullerPositioner, new MagnetPositioner.OnCompletionListener() {
            @Override
            public void onPullToSideCompleted() {
                onMenuCollapsed();
            }
        });

        View activeTab = getActiveTab();
        Log.d(TAG, "Active tab location - left: " + activeTab.getX() + ", top: " + activeTab.getY());
        Rect tabViewBounds = new Rect(
                (int) activeTab.getX(),
                (int) activeTab.getY(),
                (int) activeTab.getX() + activeTab.getWidth(),
                (int) activeTab.getY() + activeTab.getHeight());

        // Pull the tab bounds to the anchor position.
        magnetPositioner.pullToAnchor(mMenuAnchor, tabViewBounds, new OvershootInterpolator());
    }

    private void animateActiveTabToExpandedPosition(TimeInterpolator interpolator) {
        int x = 0;
        int y = 0;

        if (null == mActiveTab) {
            return;
        }

        for (Animator animator : mMotionAnimations) {
            if (animator.isRunning()) {
                animator.cancel();
            }
        }
        mMotionAnimations.clear();

        double distanceToAnimate = getDistanceBetweenTwoPoints(x, y, mActiveTab.getTranslationX(), mActiveTab.getTranslationY());
        int animationTimeInMillis = getTimeForAnimationDistance(distanceToAnimate);

        PropertyValuesHolder xChange = PropertyValuesHolder.ofFloat("translationX", mActiveTab.getTranslationX(), x);
        PropertyValuesHolder yChange = PropertyValuesHolder.ofFloat("translationY", mActiveTab.getTranslationY(), y);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mActiveTab, xChange, yChange);
        animator.setDuration(animationTimeInMillis);
        animator.setInterpolator(interpolator);
        animator.start();

        mMotionAnimations.add(animator);
    }

    public boolean isActiveTabInExitRegion() {
        PointF centerOfExitView = new PointF(mExitView.getX() + (mExitView.getWidth() / 2), mExitView.getY() + (mExitView.getHeight() / 2));
        PointF iconPosition = new PointF(mActiveTab.getX() + (mActiveTab.getWidth() / 2), mActiveTab.getY() + (mActiveTab.getHeight() / 2));
        double distance = getDistanceBetweenTwoPoints(iconPosition.x, iconPosition.y, centerOfExitView.x, centerOfExitView.y);
        return distance <= mExitRadiusInPx;
    }

    private double getDistanceBetweenTwoPoints(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private int getTimeForAnimationDistance(double distanceInPx) {
        double speedInDpPerSecond = 1000;
        double distanceInDp = distanceInPx / getResources().getDisplayMetrics().density;
        final int timingOffset = 200; // To give some time at front and back of animation regardless of how close we are to destination.

        return (int) ((distanceInDp / speedInDpPerSecond) * 1000) + timingOffset;
    }

    public interface TabSelectionListener {

        void onTabSelected(String id);

    }

    public static class NoOpTabSelectionListener implements TabSelectionListener {
        @Override
        public void onTabSelected(String id) {
            // no-op
        }
    }

    private static class HeadToTailTabIterator implements Iterator<View> {

        private View mCurrTabView;

        public HeadToTailTabIterator(@NonNull View anchorView) {
            mCurrTabView = anchorView;
        }

        @Override
        public boolean hasNext() {
            return null != mCurrTabView.getTag();
        }

        @Override
        public View next() {
            return mCurrTabView = (View) mCurrTabView.getTag();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("You cannot remove tabs through this iterator.");
        }

    }

    private static class TailToHeadTabIterator implements Iterator<View> {

        private Stack<View> mTabStack = new Stack<>();

        public TailToHeadTabIterator(@NonNull View anchorView) {
            // Tabs are connected as a singly-linked list so to reverse order we put them on a stack.
            View tabView = (View) anchorView.getTag();
            while (null != tabView) {
                mTabStack.push(tabView);
                tabView = (View) tabView.getTag();
            }
        }

        @Override
        public boolean hasNext() {
            return !mTabStack.isEmpty();
        }

        @Override
        public View next() {
            return mTabStack.pop();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("You cannot remove tabs through this iterator.");
        }
    }

    public interface CollapsedMenuViewController {
        void onPress();

        void onDragTo(@NonNull Point dragCenterPosition);

        void onRelease();

        void pullToAnchor();
    }

    public interface HoverMenuTransitionListener {
        void onCollapsing();

        void onCollapsed();

        void onExpanding();

        void onExpanded();
    }

    public static class NoOpHoverMenuTransitionListener implements HoverMenuTransitionListener {

        @Override
        public void onCollapsing() {
            // No-Op
        }

        @Override
        public void onCollapsed() {
            // No-Op
        }

        @Override
        public void onExpanding() {
            // No-Op
        }

        @Override
        public void onExpanded() {
            // No-Op
        }

    }

    public interface HoverMenuExitRequestListener {
        void onExitRequested();
    }

    public static class NoOpHoverMenuExitRequestListener implements HoverMenuExitRequestListener {

        @Override
        public void onExitRequested() {
            // No-Op
        }

    }
}
