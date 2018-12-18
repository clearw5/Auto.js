package org.autojs.autojs.ui.timing;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.stardust.autojs.execution.ExecutionConfig;
import com.stardust.util.BiMap;
import com.stardust.util.BiMaps;
import com.stardust.util.MapBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.R;
import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autojs.external.receiver.DynamicBroadcastReceivers;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.timing.IntentTask;
import org.autojs.autojs.timing.TaskReceiver;
import org.autojs.autojs.timing.TimedTask;
import org.autojs.autojs.timing.TimedTaskManager;
import org.autojs.autojs.ui.BaseActivity;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/11/28.
 */
@EActivity(R.layout.activity_timed_task_setting)
public class TimedTaskSettingActivity extends BaseActivity {

    public static final String EXTRA_INTENT_TASK_ID = "intent_task_id";
    public static final String EXTRA_TASK_ID = TaskReceiver.EXTRA_TASK_ID;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yy-MM-dd");
    private static final int REQUEST_CODE_IGNORE_BATTERY = 27101;

    private static final String LOG_TAG = "TimedTaskSettings";


    public static final Map<String, Integer> ACTION_DESC_MAP = new MapBuilder<String, Integer>()
            .put(DynamicBroadcastReceivers.ACTION_STARTUP, R.string.text_run_on_startup)
            .put(Intent.ACTION_BOOT_COMPLETED, R.string.text_run_on_boot)
            .put(Intent.ACTION_SCREEN_OFF, R.string.text_run_on_screen_off)
            .put(Intent.ACTION_SCREEN_ON, R.string.text_run_on_screen_on)
            .put(Intent.ACTION_USER_PRESENT, R.string.text_run_on_screen_unlock)
            .put(Intent.ACTION_BATTERY_CHANGED, R.string.text_run_on_battery_change)
            .put(Intent.ACTION_POWER_CONNECTED, R.string.text_run_on_power_connect)
            .put(Intent.ACTION_POWER_DISCONNECTED, R.string.text_run_on_power_disconnect)
            .put(ConnectivityManager.CONNECTIVITY_ACTION, R.string.text_run_on_conn_change)
            .put(Intent.ACTION_PACKAGE_ADDED, R.string.text_run_on_package_install)
            .put(Intent.ACTION_PACKAGE_REMOVED, R.string.text_run_on_package_uninstall)
            .put(Intent.ACTION_PACKAGE_REPLACED, R.string.text_run_on_package_update)
            .put(Intent.ACTION_HEADSET_PLUG, R.string.text_run_on_headset_plug)
            .put(Intent.ACTION_CONFIGURATION_CHANGED, R.string.text_run_on_config_change)
            .put(Intent.ACTION_TIME_TICK, R.string.text_run_on_time_tick)
            .build();

    private static final BiMap<Integer, String> ACTIONS = BiMaps.<Integer, String>newBuilder()
            .put(R.id.run_on_startup, DynamicBroadcastReceivers.ACTION_STARTUP)
            .put(R.id.run_on_boot, Intent.ACTION_BOOT_COMPLETED)
            .put(R.id.run_on_screen_off, Intent.ACTION_SCREEN_OFF)
            .put(R.id.run_on_screen_on, Intent.ACTION_SCREEN_ON)
            .put(R.id.run_on_screen_unlock, Intent.ACTION_USER_PRESENT)
            .put(R.id.run_on_battery_change, Intent.ACTION_BATTERY_CHANGED)
            .put(R.id.run_on_power_connect, Intent.ACTION_POWER_CONNECTED)
            .put(R.id.run_on_power_disconnect, Intent.ACTION_POWER_DISCONNECTED)
            .put(R.id.run_on_conn_change, ConnectivityManager.CONNECTIVITY_ACTION)
            .put(R.id.run_on_package_install, Intent.ACTION_PACKAGE_ADDED)
            .put(R.id.run_on_package_uninstall, Intent.ACTION_PACKAGE_REMOVED)
            .put(R.id.run_on_package_update, Intent.ACTION_PACKAGE_REPLACED)
            .put(R.id.run_on_headset_plug, Intent.ACTION_HEADSET_PLUG)
            .put(R.id.run_on_config_change, Intent.ACTION_CONFIGURATION_CHANGED)
            .put(R.id.run_on_time_tick, Intent.ACTION_TIME_TICK)
            .build();

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.timing_group)
    RadioGroup mTimingGroup;

    @ViewById(R.id.disposable_task_radio)
    RadioButton mDisposableTaskRadio;

    @ViewById(R.id.daily_task_radio)
    RadioButton mDailyTaskRadio;

    @ViewById(R.id.weekly_task_radio)
    RadioButton mWeeklyTaskRadio;

    @ViewById(R.id.run_on_broadcast)
    RadioButton mRunOnBroadcastRadio;

    @ViewById(R.id.run_on_other_broadcast)
    RadioButton mRunOnOtherBroadcast;

    @ViewById(R.id.action)
    EditText mOtherBroadcastAction;

    @ViewById(R.id.broadcast_group)
    RadioGroup mBroadcastGroup;

    @ViewById(R.id.disposable_task_time)
    TextView mDisposableTaskTime;

    @ViewById(R.id.disposable_task_date)
    TextView mDisposableTaskDate;

    @ViewById(R.id.daily_task_time_picker)
    TimePicker mDailyTaskTimePicker;

    @ViewById(R.id.weekly_task_time_picker)
    TimePicker mWeeklyTaskTimePicker;

    @ViewById(R.id.weekly_task_container)
    LinearLayout mWeeklyTaskContainer;

    private List<CheckBox> mDayOfWeekCheckBoxes = new ArrayList<>();

    private ScriptFile mScriptFile;
    private TimedTask mTimedTask;
    private IntentTask mIntentTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1);
        if (taskId != -1) {
            mTimedTask = TimedTaskManager.getInstance().getTimedTask(taskId);
            if (mTimedTask != null) {
                mScriptFile = new ScriptFile(mTimedTask.getScriptPath());
            }
        } else {
            long intentTaskId = getIntent().getLongExtra(EXTRA_INTENT_TASK_ID, -1);
            if (intentTaskId != -1) {
                mIntentTask = TimedTaskManager.getInstance().getIntentTask(intentTaskId);
                if (mIntentTask != null) {
                    mScriptFile = new ScriptFile(mIntentTask.getScriptPath());
                }
            } else {
                String path = getIntent().getStringExtra(ScriptIntents.EXTRA_KEY_PATH);
                if (TextUtils.isEmpty(path)) {
                    finish();
                }
                mScriptFile = new ScriptFile(path);
            }
        }

    }

    @AfterViews
    void setupViews() {
        setToolbarAsBack(getString(R.string.text_timed_task));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setSubtitle(mScriptFile.getName());
        }
        mDailyTaskTimePicker.setIs24HourView(true);
        mWeeklyTaskTimePicker.setIs24HourView(true);
        findDayOfWeekCheckBoxes(mWeeklyTaskContainer);
        setUpTaskSettings();
    }

    private void findDayOfWeekCheckBoxes(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof CheckBox) {
                mDayOfWeekCheckBoxes.add((CheckBox) child);
            } else if (child instanceof ViewGroup) {
                findDayOfWeekCheckBoxes((ViewGroup) child);
            }
            if (mDayOfWeekCheckBoxes.size() >= 7)
                break;
        }

    }

    private void setUpTaskSettings() {
        mDisposableTaskDate.setText(DATE_FORMATTER.print(LocalDate.now()));
        mDisposableTaskTime.setText(TIME_FORMATTER.print(LocalTime.now()));
        if (mTimedTask != null) {
            setupTime();
            return;
        }
        if (mIntentTask != null) {
            setupAction();
            return;
        }
        mDailyTaskRadio.setChecked(true);
    }

    private void setupAction() {
        mRunOnBroadcastRadio.setChecked(true);
        Integer buttonId = ACTIONS.getKey(mIntentTask.getAction());
        if (buttonId == null) {
            mRunOnOtherBroadcast.setChecked(true);
            mOtherBroadcastAction.setText(mIntentTask.getAction());
        } else {
            ((RadioButton) findViewById(buttonId)).setChecked(true);
        }
    }

    private void setupTime() {
        if (mTimedTask.isDisposable()) {
            mDisposableTaskRadio.setChecked(true);
            mDisposableTaskTime.setText(TIME_FORMATTER.print(mTimedTask.getMillis()));
            mDisposableTaskDate.setText(DATE_FORMATTER.print(mTimedTask.getMillis()));
            return;
        }
        LocalTime time = LocalTime.fromMillisOfDay(mTimedTask.getMillis());
        mDailyTaskTimePicker.setCurrentHour(time.getHourOfDay());
        mDailyTaskTimePicker.setCurrentMinute(time.getMinuteOfHour());
        mWeeklyTaskTimePicker.setCurrentHour(time.getHourOfDay());
        mWeeklyTaskTimePicker.setCurrentMinute(time.getMinuteOfHour());
        if (mTimedTask.isDaily()) {
            mDailyTaskRadio.setChecked(true);
        } else {
            mWeeklyTaskRadio.setChecked(true);
            for (int i = 0; i < mDayOfWeekCheckBoxes.size(); i++) {
                mDayOfWeekCheckBoxes.get(i).setChecked(mTimedTask.hasDayOfWeek(i + 1));
            }
        }
    }


    @CheckedChange({R.id.daily_task_radio, R.id.weekly_task_radio, R.id.disposable_task_radio, R.id.run_on_broadcast})
    void onCheckedChanged(CompoundButton button) {
        ExpandableRelativeLayout relativeLayout = findExpandableLayoutOf(button);
        if (button.isChecked()) {
            relativeLayout.post(relativeLayout::expand);
        } else {
            relativeLayout.collapse();
        }

    }

    private ExpandableRelativeLayout findExpandableLayoutOf(CompoundButton button) {
        ViewGroup parent = (ViewGroup) button.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i) == button) {
                return ((ExpandableRelativeLayout) parent.getChildAt(i + 1));
            }
        }
        throw new IllegalStateException("findExpandableLayout: button = " + button + ", parent = " + parent + ", childCount = " + parent.getChildCount());
    }

    @Click(R.id.disposable_task_time_container)
    void showDisposableTaskTimePicker() {
        LocalTime time = TIME_FORMATTER.parseLocalTime(mDisposableTaskTime.getText().toString());
        new TimePickerDialog(this, (view, hourOfDay, minute) -> mDisposableTaskTime.setText(TIME_FORMATTER.print(new LocalTime(hourOfDay, minute))), time.getHourOfDay(), time.getMinuteOfHour(), true)
                .show();

    }


    @Click(R.id.disposable_task_date_container)
    void showDisposableTaskDatePicker() {
        LocalDate date = DATE_FORMATTER.parseLocalDate(mDisposableTaskDate.getText().toString());
        new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                mDisposableTaskDate.setText(DATE_FORMATTER.print(new LocalDate(year, month, dayOfMonth)))
                , date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth())
                .show();
    }

    TimedTask createTimedTask() {
        if (mDisposableTaskRadio.isChecked()) {
            return createDisposableTask();
        } else if (mDailyTaskRadio.isChecked()) {
            return createDailyTask();
        } else {
            return createWeeklyTask();
        }
    }

    private TimedTask createWeeklyTask() {
        long timeFlag = 0;
        for (int i = 0; i < mDayOfWeekCheckBoxes.size(); i++) {
            if (mDayOfWeekCheckBoxes.get(i).isChecked()) {
                timeFlag |= TimedTask.getDayOfWeekTimeFlag(i + 1);
            }
        }
        if (timeFlag == 0) {
            Toast.makeText(this, R.string.text_weekly_task_should_check_day_of_week, Toast.LENGTH_SHORT).show();
            return null;
        }
        LocalTime time = new LocalTime(mWeeklyTaskTimePicker.getCurrentHour(), mWeeklyTaskTimePicker.getCurrentMinute());
        return TimedTask.weeklyTask(time, timeFlag, mScriptFile.getPath(), ExecutionConfig.getDefault());
    }

    private TimedTask createDailyTask() {
        LocalTime time = new LocalTime(mDailyTaskTimePicker.getCurrentHour(), mDailyTaskTimePicker.getCurrentMinute());
        return TimedTask.dailyTask(time, mScriptFile.getPath(), new ExecutionConfig());
    }

    private TimedTask createDisposableTask() {
        LocalTime time = TIME_FORMATTER.parseLocalTime(mDisposableTaskTime.getText().toString());
        LocalDate date = DATE_FORMATTER.parseLocalDate(mDisposableTaskDate.getText().toString());
        LocalDateTime dateTime = new LocalDateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(),
                time.getHourOfDay(), time.getMinuteOfHour());
        if (dateTime.isBefore(LocalDateTime.now())) {
            Toast.makeText(this, R.string.text_disposable_task_time_before_now, Toast.LENGTH_SHORT).show();
            return null;
        }
        return TimedTask.disposableTask(dateTime, mScriptFile.getPath(), ExecutionConfig.getDefault());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timed_task_setting, menu);
        return true;
    }

    @SuppressLint("BatteryLife")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !((PowerManager) getSystemService(POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName())) {
                try {
                    startActivityForResult(new Intent().setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                            .setData(Uri.parse("package:" + getPackageName())), REQUEST_CODE_IGNORE_BATTERY);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    createOrUpdateTask();
                }
            } else {
                createOrUpdateTask();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IGNORE_BATTERY) {
            Log.d(LOG_TAG, "result code = " + requestCode);
            createOrUpdateTask();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createOrUpdateTask() {
        if (mRunOnBroadcastRadio.isChecked()) {
            createOrUpdateIntentTask();
            return;
        }
        TimedTask task = createTimedTask();
        if (task == null)
            return;
        if (mTimedTask == null) {
            TimedTaskManager.getInstance().addTask(task);
            if (mIntentTask != null) {
                TimedTaskManager.getInstance().removeTask(mIntentTask);
            }
            Toast.makeText(this, R.string.text_already_create, Toast.LENGTH_SHORT).show();
        } else {
            task.setId(mTimedTask.getId());
            TimedTaskManager.getInstance().updateTask(task);
        }
        finish();
    }


    private void createOrUpdateIntentTask() {
        int buttonId = mBroadcastGroup.getCheckedRadioButtonId();
        if (buttonId == -1) {
            Toast.makeText(this, R.string.error_empty_selection, Toast.LENGTH_SHORT).show();
            return;
        }
        String action;
        if (buttonId == R.id.run_on_other_broadcast) {
            action = mOtherBroadcastAction.getText().toString();
            if (action.isEmpty()) {
                mOtherBroadcastAction.setError(getString(R.string.text_should_not_be_empty));
                return;
            }
        } else {
            action = ACTIONS.get(buttonId);
        }
        IntentTask task = new IntentTask();
        task.setAction(action);
        task.setScriptPath(mScriptFile.getPath());
        task.setLocal(action.equals(DynamicBroadcastReceivers.ACTION_STARTUP));
        if (mIntentTask != null) {
            task.setId(mIntentTask.getId());
            TimedTaskManager.getInstance().updateTask(task);
            Toast.makeText(this, R.string.text_already_create, Toast.LENGTH_SHORT).show();
        } else {
            TimedTaskManager.getInstance().addTask(task);
            if (mTimedTask != null) {
                TimedTaskManager.getInstance().removeTask(mTimedTask);
            }
        }

        finish();
    }
}
