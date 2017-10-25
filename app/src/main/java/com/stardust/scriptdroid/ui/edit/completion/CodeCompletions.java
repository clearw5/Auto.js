package com.stardust.scriptdroid.ui.edit.completion;

import java.util.List;

/**
 * Created by Stardust on 2017/9/27.
 */
// TODO: 2017/10/24 refactor
public class CodeCompletions {

    public static class Pos {
        public int line;
        public int ch;

        public Pos(int line, int ch) {
            this.line = line;
            this.ch = ch;
        }
    }

    private Pos mFrom;
    private Pos mTo;
    private List<String> mHints;
    private List<String> mUrls;

    public CodeCompletions(Pos from, Pos to, List<String> hints, List<String> urls) {
        mFrom = from;
        mTo = to;
        mHints = hints;
        mUrls = urls;
    }

    public static CodeCompletions just(List<String> hints) {
        return new CodeCompletions(null, null, hints, null);
    }

    public Pos getFrom() {
        return mFrom;
    }

    public Pos getTo() {
        return mTo;
    }

    public String getUrltAt(int pos) {
        if (mUrls == null)
            return null;
        return mUrls.get(pos);
    }

    public List<String> getHints() {
        return mHints;
    }


    public boolean shouldBeInserted() {
        return mFrom == null && mTo == null;
    }

}
