package org.autojs.autojs.ui.edit.editor;

/**
 * Created by Stardust on 2018/2/25.
 */

public class BracketMatching {

    public static int UNMATCHED_BRACKET = -2;
    public static int BRACKET_NOT_FOUND = -1;


    private static final char[] PAIR_LEFT = {'(', '{', '['};
    private static final char[] PAIR_RIGHT = {')', '}', ')'};


    public static int bracketMatching(CharSequence text, int index) {
        char ch = text.charAt(index);
        for (int i = 0; i < PAIR_LEFT.length; i++) {
            if (PAIR_LEFT[i] == ch) {
                return findRightBracket(text, index + 1, PAIR_LEFT[i], PAIR_RIGHT[i]);
            }
        }
        for (int i = 0; i < PAIR_RIGHT.length; i++) {
            if (PAIR_RIGHT[i] == ch) {
                return findLeftBracket(text, index - 1, PAIR_LEFT[i], PAIR_RIGHT[i]);
            }
        }
        return BRACKET_NOT_FOUND;
    }

    public static int findLeftBracket(CharSequence text, int index, char left, char right) {
        int rightBracketCount = 0;
        for (int i = index; i >= 0; i--) {
            char ch = text.charAt(i);
            if (ch == left) {
                if (rightBracketCount == 0) {
                    return i;
                }
                rightBracketCount--;
            } else if (ch == right) {
                rightBracketCount++;
            }
        }
        return UNMATCHED_BRACKET;
    }

    public static int findRightBracket(CharSequence text, int index, char left, char right) {
        int leftBracketCount = 0;
        for (int i = index; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == left) {
                leftBracketCount++;
            } else if (ch == right) {
                if (leftBracketCount == 0) {
                    return i;
                }
                leftBracketCount--;
            }
        }
        return BRACKET_NOT_FOUND;
    }
}
