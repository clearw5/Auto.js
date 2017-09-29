package com.stardust.scriptdroid.ui.edit.completion;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.workground.WrapContentLinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stardust.pio.UncheckedIOException;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.tool.GsonUtils;
import com.stardust.util.ClipboardUtil;
import com.stardust.util.UnderuseExecutors;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/2/17.
 */

public class CodeCompletionBar extends RecyclerView {

    public interface OnHintClickListener {
        void onHintClick(CodeCompletions completions, int pos);
    }

    private int mTextColor;
    private CodeCompletions mCodeCompletions;
    private OnHintClickListener mOnHintClickListener;
    private final OnClickListener mOnCodeCompletionItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder(v).getAdapterPosition();
            if (position >= 0 && position < mCodeCompletions.getHints().size()) {
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
            if (position < 0 || position >= mCodeCompletions.getHints().size())
                return false;
            ClipboardUtil.setClip(getContext(), mCodeCompletions.getHints().get(position));
            Toast.makeText(getContext(), R.string.text_copied, Toast.LENGTH_SHORT).show();
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
            textView.setText(mCodeCompletions.getHints().get(position));
            if(mTextColor != 0){
                textView.setTextColor(mTextColor);
            }
        }

        @Override
        public int getItemCount() {
            return mCodeCompletions == null ? 0 : mCodeCompletions.getHints().size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mOnCodeCompletionItemClickListener);
            itemView.setOnLongClickListener(mOnCodeCompletionItemLongClickListener);
        }
    }

    private static void readCompletions(Context context, String path) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(new InputStreamReader(context.getAssets().open(path))).getAsJsonObject();
            //InputMethodEnhanceBar.global = readGlobal(object.remove("global").getAsJsonObject());
            readModules(object);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void readModules(JsonObject object) {
        for (Map.Entry<String, JsonElement> module : object.entrySet()) {
            //variables.put(module.getKey(), GsonUtils.toStringList(module.getValue()));
        }
    }

    private static List<String> readGlobal(JsonObject global) {
        List<String> globalFunctions = new ArrayList<>();
        for (Map.Entry<String, JsonElement> moduleGlobal : global.entrySet()) {
            globalFunctions.addAll(GsonUtils.toStringList(moduleGlobal.getValue()));
        }
        return globalFunctions;
    }

}
