package org.autojs.autojs.ui.edit.completion;

import android.content.Context;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.stardust.autojs.workground.WrapContentLinearLayoutManager;

import org.autojs.autojs.R;
import org.autojs.autojs.model.autocomplete.CodeCompletions;

/**
 * Created by Stardust on 2017/2/17.
 */

public class CodeCompletionBar extends RecyclerView {

    public interface OnHintClickListener {
        void onHintClick(CodeCompletions completions, int pos);

        void onHintLongClick(CodeCompletions completions, int pos);
    }

    private int mTextColor;
    private CodeCompletions mCodeCompletions;
    private OnHintClickListener mOnHintClickListener;
    private final OnClickListener mOnCodeCompletionItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder(v).getAdapterPosition();
            if (position >= 0 && position < mCodeCompletions.size()) {
                if (mOnHintClickListener != null) {
                    mOnHintClickListener.onHintClick(mCodeCompletions, position);
                }
            }

        }
    };

    private final OnLongClickListener mOnCodeCompletionItemLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int position = getChildViewHolder(v).getAdapterPosition();
            if (position < 0 || position >= mCodeCompletions.size())
                return false;
            if (mOnHintClickListener != null) {
                mOnHintClickListener.onHintLongClick(mCodeCompletions, position);
            }
            return true;
        }
    };

    public CodeCompletionBar(Context context) {
        super(context);
        init();
    }

    public CodeCompletionBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CodeCompletionBar(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnHintClickListener(OnHintClickListener onHintClickListener) {
        mOnHintClickListener = onHintClickListener;
    }

    public void setCodeCompletions(CodeCompletions codeCompletions) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            post(() -> setCodeCompletions(codeCompletions));
            return;
        }
        mCodeCompletions = codeCompletions;
        getAdapter().notifyDataSetChanged();
    }

    public CodeCompletions getCodeCompletions() {
        return mCodeCompletions;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        getAdapter().notifyDataSetChanged();
    }


    private void init() {
        setAdapter(new CodeCompletionAdapter());
        setLayoutManager(new WrapContentLinearLayoutManager(getContext(), HORIZONTAL, false));
    }

    private class CodeCompletionAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.input_method_enhance_bar_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView textView = ((TextView) holder.itemView);
            textView.setText(mCodeCompletions.getHint(position));
            if (mTextColor != 0) {
                textView.setTextColor(mTextColor);
            }
        }

        @Override
        public int getItemCount() {
            return mCodeCompletions == null ? 0 : mCodeCompletions.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mOnCodeCompletionItemClickListener);
            itemView.setOnLongClickListener(mOnCodeCompletionItemLongClickListener);
        }
    }


}
