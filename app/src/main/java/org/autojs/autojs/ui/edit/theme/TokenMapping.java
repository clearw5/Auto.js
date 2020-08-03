package org.autojs.autojs.ui.edit.theme;


import org.mozilla.javascript.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2018/2/21.
 */

public class TokenMapping {


    public static final int TOKEN_MATCHED_BRACKET = Token.LAST_TOKEN + 1;

    private static final List<Integer> KEYWORD = tokenNamesToTypes(Arrays.asList("return", "new", "delete", "typeof", "null", "this", "false", "true", "throw", "in", "instanceof", "yield", "try", "function", "if", "else", "switch", "case", "default", "while", "do", "for", "break", "continue", "var", "with", "catch", "finally", "void", "let", "const", "debugger"));
    private static final List<Integer> KEYWORD_CONTROL = tokenNamesToTypes(Arrays.asList("if", "else", "switch", "case", "break", "continue", "goto", "return", "try", "catch", "throw", "finally"));
    private static final List<Integer> KEYWORD_OPERATOR = Collections.emptyList();

    public static List<Integer> getTokensForScope(String scope) {
        switch (scope) {
            case "keyword":
                return KEYWORD;
            case "keyword.operator":
                return KEYWORD_OPERATOR;
            case "keyword.control":
                return KEYWORD_CONTROL;
            default:
                int token = tokenNameToType(scope);
                if (isValidToken(token)) {
                    return Collections.singletonList(token);
                }
        }
        return Collections.emptyList();
    }

    public static boolean isValidToken(int token) {
        return token >= -1;
    }

    public static int tokenNameToType(String name) {
        switch (name) {
            case "this.self":
                return Token.THIS;
            case "keyword.operator.quantifier.regexp":
                return Token.REGEXP;
            case "variable":
                return Token.NAME;
            case "constant.numeric":
                return Token.NUMBER;
            case "bracket.matched":
                return TOKEN_MATCHED_BRACKET;

        }
        for (int token = Token.ERROR; token < Token.LAST_TOKEN; token++) {
            if (token == Token.STRICT_SETNAME || token == Token.SETCONSTVAR)
                continue;
            if (Token.typeToName(token).equalsIgnoreCase(name)) {
                return token;
            }
        }
        return -2;
    }

    private static List<Integer> tokenNamesToTypes(List<String> tokenNames) {
        List<Integer> types = new ArrayList<>(tokenNames.size());
        for (String name : tokenNames) {
            types.add(tokenNameToType(name));
        }
        return types;
    }
}
