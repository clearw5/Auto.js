package com.stardust.scriptdroid.ui.edit.completion;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stardust.scriptdroid.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Stardust on 2017/2/17.
 */

public class InputMethodEnhanceBar extends RecyclerView implements CodeCompletion.OnCodeCompletionChangeListener {


    public InputMethodEnhanceBar(Context context) {
        super(context);
        init();
    }

    public InputMethodEnhanceBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputMethodEnhanceBar(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public interface EditTextBridge {
        void appendText(CharSequence text);

        void backspace(int count);

        com.jecelyin.editor.v2.core.widget.TextView getEditText();
    }

    EditTextBridge mEditTextBridge;
    private CodeCompletion mCodeCompletion = new CodeCompletion(this);
    private List<CodeCompletion.CodeCompletionItem> mCodeCompletionList = new ArrayList<>();
    private OnClickListener mOnCodeCompletionItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder(v).getAdapterPosition();
            mEditTextBridge.appendText(mCodeCompletionList.get(position).getAppendText());
        }
    };

    private void init() {
        setAdapter(new CodeCompletionAdapter());
        setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
    }

    public void setEditTextBridge(EditTextBridge editTextBridge) {
        mEditTextBridge = editTextBridge;
        mCodeCompletion.setEditText(mEditTextBridge.getEditText());
    }

    @Override
    public void OnCodeCompletionChange(Collection<CodeCompletion.CodeCompletionItem> list) {
        mCodeCompletionList.clear();
        mCodeCompletionList.addAll(list);
        getAdapter().notifyDataSetChanged();
    }


    private class CodeCompletionAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.input_method_enhance_bar_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(mCodeCompletionList.get(position).getDisplayText());
        }

        @Override
        public int getItemCount() {
            return mCodeCompletionList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mOnCodeCompletionItemClickListener);
        }
    }
}
