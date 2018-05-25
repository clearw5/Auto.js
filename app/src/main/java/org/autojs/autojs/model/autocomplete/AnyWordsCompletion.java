package org.autojs.autojs.model.autocomplete;

import android.text.Editable;

import org.autojs.autojs.model.indices.Property;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2018/2/26.
 */

public class AnyWordsCompletion implements SimpleTextWatcher.AfterTextChangedListener {


    private static final String PATTERN = "[\\W]";
    private ExecutorService mExecutorService;
    private volatile DictionaryTree<String> mDictionaryTree;

    public AnyWordsCompletion(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    @Override
    public void afterTextChanged(Editable s) {
        mExecutorService.execute(() -> splitWords(s.toString()));
    }

    private void splitWords(String s) {
        DictionaryTree<String> tree = new DictionaryTree<>();
        String[] words = s.split(PATTERN);
        for (String word : words) {
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
