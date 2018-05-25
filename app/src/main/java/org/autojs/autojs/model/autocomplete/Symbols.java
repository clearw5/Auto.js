package org.autojs.autojs.model.autocomplete;

import java.util.Arrays;

/**
 * Created by Stardust on 2017/9/28.
 */

public class Symbols {

    private static CodeCompletions sSymbols = CodeCompletions.just(Arrays.asList(
            "\"", "(", ")", "=", ";", "/", "{", "}", "!", "|", "&", "-",
            "[", "]", "+", "-", "<", ">", "\\", "*", "?"));

    public static CodeCompletions getSymbols() {
        return sSymbols;
    }
}
