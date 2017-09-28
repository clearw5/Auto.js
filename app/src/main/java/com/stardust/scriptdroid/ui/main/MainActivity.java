package com.stardust.scriptdroid.ui.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.qq.e.comm.DownloadService;
import com.stardust.app.FragmentPagerAdapterBuilder;
import com.stardust.app.NotAskAgainDialog;
import com.stardust.app.OnActivityResultDelegate;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.external.floatingwindow.HoverMenuManger;
import com.stardust.scriptdroid.script.StorageFileProvider;
import com.stardust.scriptdroid.ui.main.community.CommunityFragment_;
import com.stardust.scriptdroid.ui.main.doc.OnlineDocsFragment_;
import com.stardust.scriptdroid.ui.main.scripts.MyScriptListFragment_;
import com.stardust.scriptdroid.ui.main.task.TaskManagerFragment_;
import com.stardust.theme.ThemeColorManager;
import com.stardust.util.DeveloperUtils;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.settings.SettingsActivity_;
import com.stardust.scriptdroid.ui.update.VersionGuard;
import com.stardust.util.BackPressedHandler;
import com.stardust.view.DrawerAutoClose;
import com.stardust.widget.CommonMarkdownView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements OnActivityResultDelegate.DelegateHost, BackPressedHandler.HostActivity {

    private static final String LOG_TAG = "MainActivity";

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @ViewById(R.id.viewpager)
    ViewPager mViewPager;

    @ViewById(R.id.fab)
    FloatingActionButton mFab;

    private FragmentPagerAdapterBuilder.StoredFragmentPagerAdapter mPagerAdapter;
    private OnActivityResultDelegate.Mediator mActivityResultMediator = new OnActivityResultDelegate.Mediator();
    private VersionGuard mVersionGuard;
    private BackPressedHandler.Observer mBackPressObserver = new BackPressedHandler.Observer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        mVersionGuard = new VersionGuard(this);
        showAnnunciationIfNeeded();
    }

    @AfterViews
    void setUpViews() {
        setUpToolbar();
        setUpTabViewPager();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        registerBackPressHandlers();
        ThemeColorManager.addViewBackground(findViewById(R.id.app_bar));
    }

    private void showAnnunciationIfNeeded() {
        if (!Pref.shouldShowAnnunciation()) {
            return;
        }
        new CommonMarkdownView.DialogBuilder(this)
                .padding(36, 0, 36, 0)
                .markdown(PFile.read(getResources().openRawResource(R.raw.annunciation)))
                .title(R.string.text_annunciation)
                .positiveText(R.string.ok)
                .canceledOnTouchOutside(false)
                .show();
    }


    private void registerBackPressHandlers() {
        mBackPressObserver.registerHandler(new DrawerAutoClose(mDrawerLayout, Gravity.START));
        mBackPressObserver.registerHandler(new BackPressedHandler.DoublePressExit(this, R.string.text_press_again_to_exit));
    }

    private void checkPermissions() {
        checkPermission(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE);
        showAccessibilitySettingPromptIfDisabled();
    }

    private void showAccessibilitySettingPromptIfDisabled() {
        if (AccessibilityServiceTool.isAccessibilityServiceEnabled(this)) {
            return;
        }
        new NotAskAgainDialog.Builder(this, "Eating...love you...miss you...17.4.12")
                .title(R.string.text_need_to_enable_accessibility_service)
                .content(R.string.explain_accessibility_permission)
                .positiveText(R.string.text_go_to_setting)
                .negativeText(R.string.text_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        AccessibilityServiceTool.enableAccessibilityService();
                    }
                }).show();
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string._app_name);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.text_drawer_open,
                R.string.text_drawer_close);
        drawerToggle.syncState();
        mDrawerLayout.addDrawerListener(drawerToggle);
    }

    private void setUpTabViewPager() {
        TabLayout tabLayout = $(R.id.tab);
        mPagerAdapter = new FragmentPagerAdapterBuilder(this)
                .add(new MyScriptListFragment_(), R.string.text_script)
                .add(new OnlineDocsFragment_(), R.string.text_tutorial)
                .add(new CommunityFragment_(), R.string.text_community)
                .add(new TaskManagerFragment_(), R.string.text_manage)
                .build();
        mViewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        setUpViewPagerFragmentBehaviors();
    }

    private void setUpViewPagerFragmentBehaviors() {
        mPagerAdapter.setOnFragmentInstantiateListener(new FragmentPagerAdapterBuilder.OnFragmentInstantiateListener() {
            @Override
            public void OnInstantiate(int pos, Fragment fragment) {
                ((ViewPagerFragment) fragment).setFab(mFab);
                if (pos == mViewPager.getCurrentItem()) {
                    ((ViewPagerFragment) fragment).onPageShow();
                }
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            private ViewPagerFragment mPreviousFragment;

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = mPagerAdapter.getStoredFragment(position);
                if (fragment == null)
                    return;
                if (mPreviousFragment != null) {
                    mPreviousFragment.onPageHide();
                }
                mPreviousFragment = (ViewPagerFragment) fragment;
                mPreviousFragment.onPageShow();
            }
        });
    }


    @Click(R.id.setting)
    void startSettingActivity() {
        startActivity(new Intent(this, SettingsActivity_.class));
    }

    @Click(R.id.exit)
    public void exitCompletely() {
        HoverMenuManger.hideHoverMenu();
        stopService(new Intent(this, FloatyService.class));
        AutoJs.getInstance().getScriptEngineService().stopAll();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //not working
        stopService(new Intent(this, DownloadService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVersionGuard.checkForDeprecatesAndUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActivityResultMediator.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StorageFileProvider.getDefault().notifyStoragePermissionGranted();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG && !DeveloperUtils.checkSignature(this)) {
            finish();
        }
    }


    @NonNull
    @Override
    public OnActivityResultDelegate.Mediator getOnActivityResultDelegateMediator() {
        return mActivityResultMediator;
    }


    @Override
    public void onBackPressed() {
        if (!mBackPressObserver.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    public BackPressedHandler.Observer getBackPressedObserver() {
        return mBackPressObserver;
    }

}