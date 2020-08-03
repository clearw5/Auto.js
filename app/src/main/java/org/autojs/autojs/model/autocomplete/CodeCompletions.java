package org.autojs.autojs.model.autocomplete;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/9/27.
 */
public class CodeCompletions {


    private int mFrom;
    private List<CodeCompletion> mCompletions;

    public CodeCompletions(int cursor, List<CodeCompletion> completions) {
        mFrom = cursor;
        mCompletions = completions;
    }

    public static CodeCompletions just(List<String> hints) {
        List<CodeCompletion> completions = new ArrayList<>(hints.size());
        for (String hint : hints) {
            completions.add(new CodeCompletion(hint, null, 0));
        }
        return new CodeCompletions(-1, completions);
    }

    public int getFrom() {
        return mFrom;
    }


    public int size() {
        return mCompletions.size();
    }

    public String getHint(int position) {
        return mCompletions.get(position).getHint();
    }

    public CodeCompletion get(int pos) {
        return mCompletions.get(pos);
    }

    public String getUrl(int pos) {
        return mCompletions.get(pos).getUrl();
    }
}
