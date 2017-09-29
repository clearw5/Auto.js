package com.stardust.scriptdroid.ui.edit.completion;

import java.util.List;

/**
 * Created by Stardust on 2017/9/27.
 */

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

    public CodeCompletions(Pos from, Pos to, List<String> hints) {
        mFrom = from;
        mTo = to;
        mHints = hints;
    }

    public static CodeCompletions just(List<String> hints) {
        return new CodeCompletions(null, null, hints);
    }

    public Pos getFrom() {
        return mFrom;
    }

    public Pos getTo() {
        return mTo;
    }

    public List<String> getHints() {
        return mHints;
    }


    public boolean shouldBeInserted() {
        return mFrom == null && mTo == null;
    }

}
