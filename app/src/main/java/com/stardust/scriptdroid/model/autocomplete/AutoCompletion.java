package com.stardust.scriptdroid.model.autocomplete;

import android.content.Context;

import com.stardust.scriptdroid.model.indices.Module;
import com.stardust.scriptdroid.model.indices.Modules;
import com.stardust.scriptdroid.model.indices.Property;

import java.util.ArrayList;
import java.util.List;
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

    public AutoCompletion(Context context) {
        buildDictionaryTree(context);
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
        if (mPropertyPrefill == null && mModuleName == null)
            return;
        Module module = getModule(mModuleName);
        List<CodeCompletion> completions = findCodeCompletion(module, mPropertyPrefill);
        CodeCompletions codeCompletions = new CodeCompletions(cursor, completions);
        mAutoCompleteCallback.updateCodeCompletion(codeCompletions);
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
        List<DictionaryTree.Entry<Property>> result = mGlobalPropertyTree.searchByPrefill(propertyPrefill);
        List<CodeCompletion> completions = new ArrayList<>();
        for (DictionaryTree.Entry<Property> entry : result) {
            Property property = entry.tag;
            completions.add(new CodeCompletion(property.getKey(), property.getUrl(), propertyPrefill.length()));
        }
        return completions;
    }


}
