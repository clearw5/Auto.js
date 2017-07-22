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

// TODO: 2017/7/21 refactor
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

    @Override
    public void OnCodeCompletionChange(@NonNull Collection<CodeCompletion.CodeCompletionItem> list1, @NonNull Collection<CodeCompletion.CodeCompletionItem> list2) {
        mCodeCompletionList.clear();
        mCodeCompletionList.addAll(list1);
        mCodeCompletionList.addAll(list2);
        getAdapter().notifyDataSetChanged();
    }

    public interface EditTextBridge {
        void appendText(CharSequence text);

        void backspace(int count);

        com.jecelyin.editor.v2.core.widget.TextView getEditText();
    }

    private static List<String> global;
    private static Map<String, List<String>> variables = new HashMap<>();

    EditTextBridge mEditTextBridge;
    private CodeCompletion mCodeCompletion = new CodeCompletion(this);
    private List<CodeCompletion.CodeCompletionItem> mCodeCompletionList = new ArrayList<>();
    private final OnClickListener mOnCodeCompletionItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildViewHolder(v).getAdapterPosition();
            if (position >= 0 && position < mCodeCompletionList.size())
                mEditTextBridge.appendText(mCodeCompletionList.get(position).getAppendText());
        }
    };

    private final OnLongClickListener mOnCodeCompletionItemLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int position = getChildViewHolder(v).getAdapterPosition();
            if (position < 0 || position >= mCodeCompletionList.size())
                return false;
            ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("", mCodeCompletionList.get(position).getDisplayText()));
            Toast.makeText(getContext(), R.string.text_copied, Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    private void init() {
        setAdapter(new CodeCompletionAdapter());
        setLayoutManager(new WrapContentLinearLayoutManager(getContext(), HORIZONTAL, false));
        if (global == null) {
            UnderuseExecutors.execute(new Runnable() {
                @Override
                public void run() {
                    readCompletions(getContext(), "js/functions.json");
                    mCodeCompletion.setGlobal(global);
                    mCodeCompletion.setVariableProperties(variables);
                }
            });
        }else {
            mCodeCompletion.setGlobal(global);
            mCodeCompletion.setVariableProperties(variables);
        }

    }

    public void setEditTextBridge(EditTextBridge editTextBridge) {
        mEditTextBridge = editTextBridge;
        mCodeCompletion.setEditText(mEditTextBridge.getEditText());
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
            itemView.setOnLongClickListener(mOnCodeCompletionItemLongClickListener);
        }
    }

    private static void readCompletions(Context context, String path) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(new InputStreamReader(context.getAssets().open(path))).getAsJsonObject();
            InputMethodEnhanceBar.global = readGlobal(object.remove("global").getAsJsonObject());
            readModules(object);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void readModules(JsonObject object) {
        for (Map.Entry<String, JsonElement> module : object.entrySet()) {
            variables.put(module.getKey(), GsonUtils.toStringList(module.getValue()));
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
