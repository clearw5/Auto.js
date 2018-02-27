package com.stardust.autojs.core.console;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stardust.autojs.R;
import com.stardust.concurrent.ConcurrentArrayList;
import com.stardust.enhancedfloaty.ResizableExpandableFloatyWindow;
import com.stardust.util.SparseArrayEntries;

import java.util.ArrayList;

/**
 * Created by Stardust on 2017/5/2.
 * <p>
 * TODO: 优化为无锁形式
 */
public class ConsoleView extends FrameLayout implements StardustConsole.LogListener {

    static final SparseArray<Integer> COLORS = new SparseArrayEntries<Integer>()
            .entry(Log.VERBOSE, 0xdfc0c0c0)
            .entry(Log.DEBUG, 0xdfffffff)
            .entry(Log.INFO, 0xff64dd17)
            .entry(Log.WARN, 0xff2962ff)
            .entry(Log.ERROR, 0xffd50000)
            .entry(Log.ASSERT, 0xffff534e)
            .sparseArray();

    private static final int REFRESH_INTERVAL = 100;
    private SparseArray<Integer> mColors = COLORS;
    private StardustConsole mConsole;
    private RecyclerView mLogListRecyclerView;
    private EditText mEditText;
    private ResizableExpandableFloatyWindow mWindow;
    private LinearLayout mInputContainer;
    private boolean mShouldStopRefresh = false;
    private ArrayList<StardustConsole.Log> mLogs = new ArrayList<>();

    public ConsoleView(Context context) {
        super(context);
        init();
    }

    public ConsoleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConsoleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setColors(SparseArray<Integer> colors) {
        mColors = colors;
    }

    private void init() {
        inflate(getContext(), R.layout.console_view, this);
        mLogListRecyclerView = (RecyclerView) findViewById(R.id.log_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mLogListRecyclerView.setLayoutManager(manager);
        mLogListRecyclerView.setAdapter(new Adapter());
        initEditText();
        initSubmitButton();
    }

    private void initSubmitButton() {
        final Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            CharSequence input = mEditText.getText();
            submitInput(input);
        });
    }

    private void submitInput(CharSequence input) {
        if (android.text.TextUtils.isEmpty(input)) {
            return;
        }
        if (mConsole.submitInput(input)) {
            mEditText.setText("");
        }
    }

    private void initEditText() {
        mEditText = (EditText) findViewById(R.id.input);
        mEditText.setFocusableInTouchMode(true);
        mInputContainer = (LinearLayout) findViewById(R.id.input_container);
        OnClickListener listener = v -> {
            if (mWindow != null) {
                mWindow.requestWindowFocus();
                mEditText.requestFocus();
            }
        };
        mEditText.setOnClickListener(listener);
        mInputContainer.setOnClickListener(listener);
    }

    public void setConsole(StardustConsole console) {
        mConsole = console;
        mConsole.setConsoleView(this);
    }

    @Override
    public void onNewLog(StardustConsole.Log log) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mShouldStopRefresh = false;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLog();
                if (!mShouldStopRefresh) {
                    postDelayed(this, REFRESH_INTERVAL);
                }
            }
        }, REFRESH_INTERVAL);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mShouldStopRefresh = true;
    }


    @Override
    public void onLogClear() {
        post(() -> {
            mLogs.clear();
            mLogListRecyclerView.getAdapter().notifyDataSetChanged();
        });
    }

    private void refreshLog() {
        if (mConsole == null)
            return;
        int oldSize = mLogs.size();
        ArrayList<StardustConsole.Log> logs = mConsole.getAllLogs();
        synchronized (mConsole.getAllLogs()) {
            final int size = logs.size();
            if (size == 0) {
                return;
            }
            if (oldSize >= size) {
                return;
            }
            if (oldSize == 0) {
                mLogs.addAll(logs);
            } else {
                for (int i = oldSize; i < size; i++) {
                    mLogs.add(logs.get(i));
                }
            }
            mLogListRecyclerView.getAdapter().notifyItemRangeInserted(oldSize, size - 1);
            mLogListRecyclerView.scrollToPosition(size - 1);
        }
    }

    public void setWindow(ResizableExpandableFloatyWindow window) {
        mWindow = window;
    }

    public void showEditText() {
        post(() -> {
            mWindow.requestWindowFocus();
            //mInputContainer.setVisibility(VISIBLE);
            mEditText.requestFocus();
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.console_view_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            StardustConsole.Log log = mLogs.get(position);
            holder.textView.setText(log.content);
            holder.textView.setTextColor(mColors.get(log.level));
        }

        @Override
        public int getItemCount() {
            return mLogs.size();
        }
    }
}
