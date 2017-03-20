package com.stardust.scriptdroid.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.stardust.app.NotRemindAgainDialog;
import com.stardust.app.OnActivityResultDelegate;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.scriptdroid.external.notification.record.AccessibilityActionRecordNotification;
import com.stardust.scriptdroid.record.inputevent.InputEventRecorder;
import com.stardust.scriptdroid.record.inputevent.InputEventToJsRecorder;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.tool.ImageSelector;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.main.my_script_list.MyScriptListFragment;
import com.stardust.scriptdroid.ui.main.sample_list.SampleScriptListFragment;
import com.stardust.scriptdroid.ui.settings.SettingsActivity;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.MessageEvent;
import com.stardust.view.ViewBinding;
import com.stardust.widget.SlidingUpPanel;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.tool.FileUtils;
import com.stardust.scriptdroid.tool.BackPressedHandler;
import com.stardust.scriptdroid.ui.main.operation.ScriptFileOperation;
import com.stardust.view.ViewBinder;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;


public class MainActivity extends BaseActivity implements FileChooserDialog.FileCallback {

    private static final String EXTRA_ACTION = "EXTRA_ACTION";

    private static final String ACTION_ON_ACTION_RECORD_STOPPED = "ACTION_ON_ACTION_RECORD_STOPPED";
    private static final String ARGUMENT_SCRIPT = "ARGUMENT_SCRIPT";
    private static final String ACTION_ON_ROOT_RECORD_STOPPED = "ACTION_ON_ROOT_RECORD_STOPPED";

    private ScriptFileList mScriptFileList;
    private DrawerLayout mDrawerLayout;
    @ViewBinding.Id(R.id.bottom_menu)
    private SlidingUpPanel mAddBottomMenuPanel;

    private MyScriptListFragment mMyScriptListFragment;
    private SampleScriptListFragment mSampleScriptListFragment;
    private OnActivityResultDelegate.Intermediary mActivityResultIntermediary = new OnActivityResultDelegate.Intermediary();
    private BackPressedHandler.Observer mBackPressObserver = new BackPressedHandler.Observer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUI();
        checkPermissions();
        registerBackPressHandler();
        handleIntent(getIntent());
    }

    private void registerBackPressHandler() {
        mBackPressObserver.registerHandler(new BackPressedHandler.DoublePressExit(this));
    }

    private void checkPermissions() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        goToAccessibilityPermissionSettingIfDisabled();
    }

    private void goToAccessibilityPermissionSettingIfDisabled() {
        if (!AccessibilityServiceUtils.isAccessibilityServiceEnabled(this, AccessibilityWatchDogService.class)) {
            new NotRemindAgainDialog.Builder(this, "goToAccessibilityPermissionSettingIfDisabled")
                    .title(R.string.text_alert)
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
    }

    private void addScriptFile(final String path) {
        new ThemeColorMaterialDialogBuilder(this).title(R.string.text_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.text_please_input_name), FileUtils.getNameWithoutExtension(path), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        MainActivity.this.addScriptFile(input.toString(), path);
                    }
                }).show();
    }

    private void addScriptFile(String name, String path) {
        mScriptFileList.add(new ScriptFile(name, path));
        EventBus.getDefault().post(new MessageEvent(MyScriptListFragment.MESSAGE_SCRIPT_FILE_ADDED));
    }

    private void setUpUI() {
        mDrawerLayout = (DrawerLayout) View.inflate(this, R.layout.activity_main, null);
        setContentView(mDrawerLayout);
        setUpFragment();
        setUpToolbar();
        setUpTabLayout();
        setUpDrawerHeader();
        ViewBinder.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void setUpFragment() {
        SlideMenuFragment.setFragment(this, R.id.fragment_slide_menu);
        mMyScriptListFragment = new MyScriptListFragment();
        mScriptFileList = ScriptFileList.getImpl();
        mSampleScriptListFragment = new SampleScriptListFragment();
    }

    private void setUpTabLayout() {
        TabLayout tabLayout = $(R.id.tab);
        ViewPager viewPager = $(R.id.viewpager);
        viewPager.setAdapter(new FragmentPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);
    }

    @SuppressLint("SetTextI18n")
    private void setUpDrawerHeader() {
        TextView version = $(R.id.version);
        version.setText("Version " + BuildConfig.VERSION_NAME);
        String path = Pref.def().getString(Pref.KEY_DRAWER_HEADER_IMAGE_PATH, null);
        if (path != null) {
            setDrawerHeaderImage(path);
        }
        path = Pref.def().getString(Pref.KEY_APP_BAR_IMAGE_PATH, null);
        if (path != null) {
            setAppBarImage(path);
        }
        $(R.id.drawer).setFitsSystemWindows(false);
    }

    private void setDrawerHeaderImage(String path) {
        Drawable d = BitmapDrawable.createFromPath(path);
        if (d != null) {
            ((ImageView) $(R.id.drawer_header_img)).setImageDrawable(d);
        }
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


    @ViewBinding.Click(R.id.add)
    private void showAddFilePanel() {
        mAddBottomMenuPanel.show();
    }

    @ViewBinding.Click(R.id.create_new_file)
    private void createScriptFile() {
        createScriptFileForScript(null);
    }


    private void createScriptFileForScript(final String script) {
        new ThemeColorMaterialDialogBuilder(this).title(R.string.text_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.text_please_input_name), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String path = FileUtils.generateNotExistingPath(ScriptFile.DEFAULT_FOLDER + input, ".js");
                        MainActivity.this.createScriptFile(input.toString(), path, script);
                    }
                })
                .show();
    }

    private void createScriptFile(String name, String path, String script) {
        if (FileUtils.createFileIfNotExists(path)) {
            if (script != null) {
                if (!FileUtils.writeString(path, script)) {
                    Snackbar.make(mDrawerLayout, R.string.text_file_write_fail, Snackbar.LENGTH_LONG).show();
                }
            }
            addScriptFile(name, path);
            new ScriptFileOperation.Edit().operate(mMyScriptListFragment.getScriptListRecyclerView(), mScriptFileList, mScriptFileList.size() - 1);
        } else {
            Snackbar.make(mDrawerLayout, R.string.text_file_create_fail, Snackbar.LENGTH_LONG).show();
        }
    }

    @ViewBinding.Click(R.id.import_from_file)
    private void showFileChooser() {
        new FileChooserDialog.Builder(this)
                .initialPath(ScriptFile.DEFAULT_FOLDER)
                .extensionsFilter(".js", ".txt")
                .show();
    }

    @ViewBinding.Click(R.id.record)
    private void startScriptRecord() {
        if (AccessibilityWatchDogService.getInstance() == null) {
            Snackbar.make(mDrawerLayout, R.string.text_need_enable_accessibility_service, Snackbar.LENGTH_SHORT).show();
            return;
        }
        AccessibilityActionRecordNotification.showOrUpdateNotification();
        Snackbar.make(mDrawerLayout, R.string.hint_start_record, Snackbar.LENGTH_SHORT).show();
    }

    private InputEventRecorder mInputEventRecorder;

    @ViewBinding.Click(R.id.root_record)
    private void startRootRecord() {
        if (mInputEventRecorder == null) {
            mInputEventRecorder = new InputEventToJsRecorder();
            mInputEventRecorder.listen();
        }
        Snackbar.make(mDrawerLayout, R.string.hint_start_root_record, Snackbar.LENGTH_SHORT).show();
    }

    @ViewBinding.Click(R.id.setting)
    private void startSettingActivity() {
        startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ViewBinding.Click(R.id.exit)
    public void finish() {
        super.finish();
    }

    @ViewBinding.Click(R.id.drawer_header_img)
    public void selectHeaderImage() {
        new ImageSelector(this, mActivityResultIntermediary, new ImageSelector.ImageSelectorCallback() {
            @Override
            public void onImageSelected(ImageSelector selector, String path) {
                setDrawerHeaderImage(path);
                Pref.def().edit().putString(Pref.KEY_DRAWER_HEADER_IMAGE_PATH, path).apply();
                mActivityResultIntermediary.removeDelegate(selector);
            }
        }).select();
    }

    @Override
    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {
        addScriptFile(file.getPath());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getStringExtra(EXTRA_ACTION);
        if (action == null)
            return;
        switch (action) {
            case ACTION_ON_ACTION_RECORD_STOPPED:
                handleRecordedScript(intent.getStringExtra(ARGUMENT_SCRIPT));
                break;
            case ACTION_ON_ROOT_RECORD_STOPPED:
                if (mInputEventRecorder != null) {
                    mInputEventRecorder.stop();
                    handleRecordedScript(mInputEventRecorder.getCode());
                    mInputEventRecorder = null;
                }
                break;
        }
    }

    private void handleRecordedScript(final String script) {
        new ThemeColorMaterialDialogBuilder(this)
                .title(R.string.text_recorded)
                .items(getString(R.string.text_new_file), getString(R.string.text_copy_to_clip))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        if (position == 0) {
                            createScriptFileForScript(script);
                        } else {
                            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE))
                                    .setPrimaryClip(ClipData.newPlainText("script", script));
                            Toast.makeText(MainActivity.this, R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .negativeText(R.string.text_cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .canceledOnTouchOutside(false)
                .show();

    }


    @Override
    public void onBackPressed() {
        if (mAddBottomMenuPanel.isShowing()) {
            mAddBottomMenuPanel.dismiss();
        } else if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else {
            mBackPressObserver.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActivityResultIntermediary.onActivityResult(requestCode, resultCode, data);
    }

    public static void onActionRecordStopped(Context context, String script) {
        Intent intent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(EXTRA_ACTION, ACTION_ON_ACTION_RECORD_STOPPED)
                .putExtra(ARGUMENT_SCRIPT, script);
        context.startActivity(intent);
    }


    public static void onRootRecordStopped(Context context) {
        Intent intent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(EXTRA_ACTION, ACTION_ON_ROOT_RECORD_STOPPED);
        context.startActivity(intent);
    }

    @ViewBinding.Click(R.id.toolbar)
    public void OnToolbarClick() {
        new ImageSelector(this, mActivityResultIntermediary, new ImageSelector.ImageSelectorCallback() {
            @Override
            public void onImageSelected(ImageSelector selector, String path) {
                Pref.def().edit().putString(Pref.KEY_APP_BAR_IMAGE_PATH, path).apply();
                setAppBarImage(path);
                mActivityResultIntermediary.removeDelegate(selector);
            }
        }).select();
    }

    private void setAppBarImage(String path) {
        Drawable d = BitmapDrawable.createFromPath(path);
        if (d != null) {
            $(R.id.app_bar).setBackground(d);
        }
    }

    private class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        FragmentPagerAdapter() {
            super(MainActivity.this.getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            return position == 0 ? mMyScriptListFragment : mSampleScriptListFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(position == 0 ? R.string.text_my_script : R.string.text_sample_script);
        }
    }

}