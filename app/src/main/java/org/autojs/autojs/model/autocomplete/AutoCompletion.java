package org.autojs.autojs.model.autocomplete;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

import org.autojs.autojs.model.indices.Module;
import org.autojs.autojs.model.indices.Modules;
import org.autojs.autojs.model.indices.Property;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;
import org.mozilla.javascript.ast.Loop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2018/2/3.
 */

public class AutoCompletion {

    public interface AutoCompleteCallback {

        void updateCodeCompletion(CodeCompletions codeCompletions);
    }

    private static final Pattern STATEMENT = Pattern.compile("([A-Za-z]+\\.)?([a-zA-Z][a-zA-Z0-9_]*)?$");

    private String mModuleName;
    private String mPropertyPrefill;
    private List<Module> mModules;
    private DictionaryTree<Property> mGlobalPropertyTree = new DictionaryTree<>();
    private AutoCompleteCallback mAutoCompleteCallback;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private AnyWordsCompletion mAnyWordsCompletion;
    private AtomicInteger mExecuteId = new AtomicInteger();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final EditText mEditText;

    public AutoCompletion(Context context, EditText editText) {
        buildDictionaryTree(context);
        mEditText = editText;
        mAnyWordsCompletion = new AnyWordsCompletion(mExecutorService);
        editText.addTextChangedListener(mAnyWordsCompletion);
    }

    public void setAutoCompleteCallback(AutoCompleteCallback autoCompleteCallback) {
        mAutoCompleteCallback = autoCompleteCallback;
    }

    private void buildDictionaryTree(Context context) {
        Modules.getInstance().getModules(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::buildDictionaryTree)
                .subscribe(modules -> mModules = modules);
    }

    private void buildDictionaryTree(List<Module> modules) {
        for (Module module : modules) {
            if (!module.getName().equals("globals"))
                mGlobalPropertyTree.putWord(module.getName(), module.asGlobalProperty());
            for (Property property : module.getProperties()) {
                if (property.isGlobal())
                    mGlobalPropertyTree.putWord(property.getKey(), property);
            }
        }
    }

    public void onCursorChange(String line, int cursor) {
        if (cursor <= 0 || line == null || line.isEmpty()) {
            return;
        }
        if (mModules == null || mAutoCompleteCallback == null)
            return;
        findStatementOnCursor(line, cursor);
        Module module = getModule(mModuleName);
        if (mPropertyPrefill == null && module == null)
            return;
        String prefill = mPropertyPrefill;
        int id = mExecuteId.incrementAndGet();
        mExecutorService.execute(() -> {
            if (id != mExecuteId.get())
                return;
            List<CodeCompletion> completions = findCodeCompletion(module, prefill);
            CodeCompletions codeCompletions = new CodeCompletions(cursor, completions);
            if (id != mExecuteId.get())
                return;
            mHandler.post(() -> {
                if (id != mExecuteId.get())
                    return;
                mAutoCompleteCallback.updateCodeCompletion(codeCompletions);
            });
        });

    }

    private Module getModule(String moduleName) {
        if (moduleName == null)
            return null;
        for (Module module : mModules) {
            if (module.getName().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    private void findStatementOnCursor(String line, int cursor) {
        Matcher matcher = STATEMENT.matcher(line.substring(0, cursor));
        if (!matcher.find()) {
            mModuleName = mPropertyPrefill = null;
            return;
        }
        if (matcher.groupCount() == 2) {
            String module = matcher.group(1);
            mModuleName = module == null ? null : module.substring(0, module.length() - 1);
            mPropertyPrefill = matcher.group(2);
        } else {
            mModuleName = null;
            mPropertyPrefill = matcher.group(1);
        }
    }

    private List<CodeCompletion> findCodeCompletion(Module module, String propertyPrefill) {
        if (module == null)
            return findCodeCompletionForGlobal(propertyPrefill);
        return findCodeCompletionForModule(module, propertyPrefill);
    }

    private List<CodeCompletion> findCodeCompletionForModule(Module module, String propertyPrefill) {
        List<CodeCompletion> completions = new ArrayList<>();
        int len = propertyPrefill == null ? 0 : propertyPrefill.length();
        for (Property property : module.getProperties()) {
            if (propertyPrefill == null || property.getKey().startsWith(propertyPrefill)) {
                completions.add(new CodeCompletion(property.getKey(), property.getUrl(), len));
            }
        }
        return completions;
    }

    private List<CodeCompletion> findCodeCompletionForGlobal(String propertyPrefill) {
        if (propertyPrefill == null)
            return Collections.emptyList();
        List<CodeCompletion> completions = new ArrayList<>();
        List<DictionaryTree.Entry<Property>> result = mGlobalPropertyTree.searchByPrefill(propertyPrefill);
        for (DictionaryTree.Entry<Property> entry : result) {
            Property property = entry.tag;
            completions.add(new CodeCompletion(property.getKey(), property.getUrl(), propertyPrefill.length()));
        }
        mAnyWordsCompletion.findCodeCompletion(completions, propertyPrefill);
        return completions;
    }


    public void shutdown(){
        mEditText.removeTextChangedListener(mAnyWordsCompletion);
        mExecutorService.shutdownNow();
    }
}
