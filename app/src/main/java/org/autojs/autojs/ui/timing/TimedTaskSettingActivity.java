package org.autojs.autojs.ui.timing;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.stardust.autojs.execution.ExecutionConfig;

import org.autojs.autojs.R;
import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.timing.TaskReceiver;
import org.autojs.autojs.timing.TimedTask;
import org.autojs.autojs.timing.TimedTaskManager;
import org.autojs.autojs.ui.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/11/28.
 */
@EActivity(R.layout.activity_timed_task_setting)
public class TimedTaskSettingActivity extends BaseActivity {


    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yy-MM-dd");

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int taskId = getIntent().getIntExtra(TaskReceiver.EXTRA_TASK_ID, -1);
        if (taskId != -1) {
            mTimedTask = TimedTaskManager.getInstance().getTimedTask(taskId);
            if (mTimedTask != null) {
                mScriptFile = new ScriptFile(mTimedTask.getScriptPath());
            }
        } else {
            String path = getIntent().getStringExtra(ScriptIntents.EXTRA_KEY_PATH);
            if (TextUtils.isEmpty(path)) {
                finish();
            }
            mScriptFile = new ScriptFile(path);
        }

    }

    @AfterViews
    void setupViews() {
        setToolbarAsBack(getString(R.string.text_timed_task));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setSubtitle(mScriptFile.getName());
        }
        findDayOfWeekCheckBoxes(mWeeklyTaskContainer);
        setUpTime();
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

    private void setUpTime() {
        mDisposableTaskDate.setText(DATE_FORMATTER.print(LocalDate.now()));
        mDisposableTaskTime.setText(TIME_FORMATTER.print(LocalTime.now()));
        if (mTimedTask == null) {
            mDailyTaskRadio.setChecked(true);
            return;
        }
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


    @CheckedChange({R.id.daily_task_radio, R.id.weekly_task_radio, R.id.disposable_task_radio})
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
                , date.getYear(), date.getMonthOfYear(), date.getDayOfMonth())
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            createOrUpdateTimedTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createOrUpdateTimedTask() {
        TimedTask task = createTimedTask();
        if (task == null)
            return;
        if (mTimedTask == null) {
            TimedTaskManager.getInstance().addTask(task);
            Toast.makeText(this, R.string.text_already_create, Toast.LENGTH_SHORT).show();
        } else {
            task.setId(mTimedTask.getId());
            TimedTaskManager.getInstance().updateTask(task);
        }
        finish();
    }
}
