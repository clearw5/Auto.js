package org.autojs.autojs.model.autocomplete;

import android.text.Editable;
import android.text.TextWatcher;

import org.autojs.autojs.model.indices.Property;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2018/2/26.
 */

public class AnyWordsCompletion extends SimpleTextWatcher {


    private static final String PATTERN = "[\\W]";
    private ExecutorService mExecutorService;
    private volatile DictionaryTree<String> mDictionaryTree;
    private AtomicInteger mExecuteId = new AtomicInteger();

    public AnyWordsCompletion(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        int id = mExecuteId.incrementAndGet();
        mExecutorService.execute(() -> splitWords(id, str));
    }

    private void splitWords(int id, String s) {
        if(id != mExecuteId.get()){
            return;
        }
        DictionaryTree<String> tree = new DictionaryTree<>();
        String[] words = s.split(PATTERN);
        for (String word : words) {
            if(id != mExecuteId.get()){
                return;
            }
            tree.putWord(word, word);
        }
        mDictionaryTree = tree;
    }

    public void findCodeCompletion(List<CodeCompletion> completions, String wordPrefill) {
        if (mDictionaryTree == null)
            return;
        List<DictionaryTree.Entry<String>> result = mDictionaryTree.searchByPrefill(wordPrefill);
        for (DictionaryTree.Entry<String> entry : result) {
            completions.add(new CodeCompletion(entry.tag, null, wordPrefill.length()));
        }
    }
}
