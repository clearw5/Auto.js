package org.autojs.autojs.ui.edit.debug;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.stardust.util.ClipboardUtil;

import org.autojs.autojs.R;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.ui.widget.AutoAdapter;
import org.autojs.autojs.ui.widget.BindableViewHolder;

import java.util.List;


public class DebugBar extends FrameLayout {

    private AutoAdapter<WatchingVariable> mVariablesAdapter;
    private final WatchingVariable mCurrentVariable = new WatchingVariable(null, null, true);
    private TextView mTitle;
    private CodeEvaluator mCodeEvaluator;

    public DebugBar(@NonNull Context context) {
        super(context);
        init();
    }

    public DebugBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DebugBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCodeEvaluator(CodeEvaluator codeEvaluator) {
        mCodeEvaluator = codeEvaluator;
    }

    public List<WatchingVariable> getWatchingVariables() {
        return mVariablesAdapter.getData();
    }

    private void init() {
        inflate(getContext(), R.layout.debug_bar, this);
        mVariablesAdapter = new AutoAdapter<>(VariableViewHolder::new, R.layout.item_debug_variable_recycler_view);
        RecyclerView variablesView = findViewById(R.id.variables);
        variablesView.setAdapter(mVariablesAdapter);
        variablesView.setLayoutManager(new LinearLayoutManager(getContext()));
        mVariablesAdapter.add(mCurrentVariable);
        findViewById(R.id.add).setOnClickListener(view -> showNewWatchingVariableDialog());
        findViewById(R.id.execute).setOnClickListener(view -> showExecuteCodeDialog());
        mTitle = findViewById(R.id.title);
    }

    private void showExecuteCodeDialog() {
        if (mCodeEvaluator == null) {
            return;
        }
        new CodeEvaluateDialogBuilder(getContext())
                .codeEvaluator(mCodeEvaluator)
                .title(R.string.text_execute_code)
                .show();
    }


    public void setTitle(String title) {
        if (title == null) {
            mTitle.setText(R.string.text_debug);
        } else {
            mTitle.setText(getResources().getString(R.string.format_debug_bar_title, title));
        }
    }

    private void showNewWatchingVariableDialog() {
        new ThemeColorMaterialDialogBuilder(getContext())
                .title(R.string.text_new_watching_variable)
                .input(getResources().getString(R.string.text_variable_or_expr), "", (dialog, input) -> {
                    if (TextUtils.isEmpty(input)) {
                        return;
                    }
                    mVariablesAdapter.add(new WatchingVariable(input.toString()));
                })
                .show();
    }

    public void updateCurrentVariable(String name, String value) {
        mCurrentVariable.setDisplayName(name);
        mCurrentVariable.setName(name);
        mCurrentVariable.setValue(value);
        mVariablesAdapter.notifyItemChanged(0);
    }

    public void refresh(int start, int count) {
        mVariablesAdapter.notifyItemRangeChanged(start, count);
    }

    public void registerVariableChangeObserver(RecyclerView.AdapterDataObserver observer) {
        mVariablesAdapter.registerAdapterDataObserver(observer);
    }

    public void unregisterVariableChangeObserver(RecyclerView.AdapterDataObserver observer) {
        mVariablesAdapter.unregisterAdapterDataObserver(observer);
    }


    private void showVariable(WatchingVariable variable) {
        new ThemeColorMaterialDialogBuilder(getContext())
                .title(variable.getDisplayName())
                .content(variable.getValue())
                .positiveText(R.string.ok)
                .negativeText(R.string.text_copy_value)
                .autoDismiss(true)
                .onNegative((dialog, which) -> ClipboardUtil.setClip(getContext(), variable.getValue()))
                .show();
    }


    class VariableViewHolder extends BindableViewHolder<WatchingVariable> {

        private final TextView mVariable;
        private final ImageView mIcon;

        VariableViewHolder(View itemView) {
            super(itemView);
            mVariable = itemView.findViewById(R.id.variable);
            mIcon = itemView.findViewById(R.id.icon);
            mIcon.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                WatchingVariable variable = mVariablesAdapter.get(pos);
                if (!variable.isPinned()) {
                    mVariablesAdapter.remove(pos);
                }
            });
            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                WatchingVariable variable = mVariablesAdapter.get(pos);
                showVariable(variable);
            });
        }

        @Override
        public void bind(WatchingVariable data, int position) {
            if (TextUtils.isEmpty(data.getDisplayName())) {
                mVariable.setText("");
            } else {
                mVariable.setText(String.format("%s = %s", data.getDisplayName(), data.getSingleLineValue()));
            }
            mIcon.setVisibility(data.isPinned() ? View.INVISIBLE : View.VISIBLE);
        }
    }


}
