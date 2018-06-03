/**
 * Copyright 2018 WHO<980008027@qq.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Modified by project: https://github.com/980008027/JsDroidEditor
 */
package org.autojs.autojs.ui.edit.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TimingLogger;
import android.view.Gravity;

import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.ui.edit.theme.Theme;
import org.autojs.autojs.ui.edit.theme.TokenMapping;
import com.stardust.util.ClipboardUtil;
import com.stardust.util.TextUtils;

import org.mozilla.javascript.Token;

import static org.autojs.autojs.ui.edit.editor.BracketMatching.UNMATCHED_BRACKET;

/**
 * Created by Administrator on 2018/2/11.
 */

public class CodeEditText extends AppCompatEditText {


    static final String LOG_TAG = "CodeEditText";
    private static final boolean DEBUG = false;

    // 文字范围
    protected HVScrollView mParentScrollView;

    private CodeEditor.CursorChangeCallback mCallback;
    private volatile JavaScriptHighlighter.HighlightTokens mHighlightTokens;
    private Theme mTheme;
    private TimingLogger mLogger = new TimingLogger(LOG_TAG, "draw");
    private Paint mLineHighlightPaint = new Paint();
    private int mFirstLineForDraw = -1, mLastLineForDraw;
    private int[] mMatchingBrackets = {-1, -1};
    private int mUnmatchedBracket = -1;


    public CodeEditText(Context context) {
        super(context);
        init();
    }

    public CodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setGravity(Gravity.START);
        // 设值背景透明
        setBackgroundColor(Color.TRANSPARENT);
        // 设置字体颜色
        setTextColor(Color.TRANSPARENT);
        // 设置字体
        setTypeface(Typeface.MONOSPACE);
        setHorizontallyScrolling(true);
        mTheme = Theme.getDefault(getContext());
        mLineHighlightPaint.setStyle(Paint.Style.FILL);
    }

    public void setTheme(Theme theme) {
        mTheme = theme;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mLogger.reset();
        if (mParentScrollView == null) {
            mParentScrollView = (HVScrollView) getParent();
        }
        updatePaddingForGutter();
        updateLineRangeForDraw(canvas);

        //绘制当前行高亮需要在绘制光标之前
        drawLineHighlight(canvas, mLineHighlightPaint, getCurrentLine());

        //调用super.onDraw绘制光标和选择高亮。因为字体颜色被设置为透明因此super.onDraw()绘制的字体不显示
        // TODO: 2018/2/24 优化效率。不绘制透明字体。
        super.onDraw(canvas);
        mLogger.addSplit("super draw");

        canvas.save();
        canvas.translate(0, getExtendedPaddingTop());
        drawText(canvas);
        mLogger.addSplit("draw text");
        canvas.restore();

        mLogger.dumpToLog();
    }

    private void updateLineRangeForDraw(Canvas canvas) {
        Layout layout = getLayout();
        if (layout == null)
            return;
        long lineRange = getLineRangeForDraw(layout, canvas);
        mFirstLineForDraw = LayoutHelper.unpackRangeStartFromLong(lineRange);
        mLastLineForDraw = LayoutHelper.unpackRangeEndFromLong(lineRange);
    }

    private void updatePaddingForGutter() {
        // 根据行号计算左边距padding 留出绘制行号的空间
        String max = Integer.toString(getLineCount());
        float gutterWidth = getPaint().measureText(max) + 20;
        if (getPaddingLeft() != gutterWidth) {
            setPadding((int) gutterWidth, 0, 0, 0);
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            ClipboardUtil.setClip(getContext(), ClipboardUtil.getClip(getContext()).toString());
        }
        return super.onTextContextMenuItem(id);
    }

    //该方法中内联了很多函数来提高效率 但是 这是必要的吗？？？
    // 绘制文本着色
    private void drawText(Canvas canvas) {
        if (mFirstLineForDraw < 0) {
            return;
        }
        JavaScriptHighlighter.HighlightTokens highlightTokens = mHighlightTokens;
        Layout layout = getLayout();
        int lineCount = getLineCount();
        int textLength = highlightTokens == null ? 0 : highlightTokens.getText().length();
        Editable text = getText();
        int paddingLeft = getPaddingLeft();
        int scrollX = Math.max(getRealScrollX() - paddingLeft, 0);
        Paint paint = getPaint();
        int lineNumberColor = mTheme.getLineNumberColor();
        if (DEBUG)
            Log.d(LOG_TAG, "draw line: " + (mLastLineForDraw - mFirstLineForDraw + 1));
        mLogger.addSplit("before draw line");
        for (int line = mFirstLineForDraw; line <= mLastLineForDraw && line < lineCount; line++) {
            int lineBottom = layout.getLineTop(line + 1);
            int lineBaseline = lineBottom - layout.getLineDescent(line);

            //drawLineNumber
            String lineNumberText = Integer.toString(line + 1);
            paint.setColor(lineNumberColor);
            canvas.drawText(lineNumberText, 0, lineNumberText.length(), 10,
                    lineBaseline, paint);

            if (highlightTokens == null)
                continue;

            //drawCode
            int lineStart = layout.getLineStart(line);
            if (lineStart >= textLength) {
                return;
            }
            int lineEnd = Math.min(layout.getLineVisibleEnd(line), highlightTokens.colors.length);
            int visibleCharStart = getVisibleCharIndex(paint, scrollX, lineStart, lineEnd);
            int visibleCharEnd = getVisibleCharIndex(paint, scrollX + mParentScrollView.getWidth(), lineStart, lineEnd) + 1;
            int previousColorPos = visibleCharStart;
            int previousColor;
            if (previousColorPos == mUnmatchedBracket) {
                previousColor = mTheme.getColorForToken(Token.ERROR);
            } else if (previousColorPos == mMatchingBrackets[0] || previousColorPos == mMatchingBrackets[1]) {
                previousColor = mTheme.getColorForToken(TokenMapping.TOKEN_MATCHED_BRACKET);
            } else {
                previousColor = highlightTokens.colors[previousColorPos];
            }
            int i;
            for (i = visibleCharStart; i < visibleCharEnd; i++) {
                int color;
                if (i == mUnmatchedBracket) {
                    color = mTheme.getColorForToken(Token.ERROR);
                } else if (i == mMatchingBrackets[0] || i == mMatchingBrackets[1]) {
                    color = mTheme.getColorForToken(TokenMapping.TOKEN_MATCHED_BRACKET);
                } else {
                    color = highlightTokens.colors[i];
                }
                if (previousColor != color) {
                    paint.setColor(previousColor);
                    float offsetX = paint.measureText(text, lineStart, previousColorPos);
                    canvas.drawText(text, previousColorPos, i, paddingLeft + offsetX, lineBaseline, paint);
                    previousColor = color;
                    previousColorPos = i;
                }
            }
            paint.setColor(previousColor);
            float offsetX = paint.measureText(text, lineStart, previousColorPos);
            canvas.drawText(text, previousColorPos, visibleCharEnd, paddingLeft + offsetX, lineBaseline, paint);
            if (DEBUG) {
                mLogger.addSplit("draw line " + line + " (" + (visibleCharEnd - visibleCharStart) + ") ");
            }
        }
    }

    private void drawLineHighlight(Canvas canvas, Paint paint, int line) {
        if (line < mFirstLineForDraw || line > mLastLineForDraw || mFirstLineForDraw < 0 || line < 0) {
            return;
        }
        int lineTop = getLayout().getLineTop(line);
        int lineBottom = getLayout().getLineTop(line + 1);
        paint.setColor(mTheme.getLineHighlightBackgroundColor());
        canvas.drawRect(0, lineTop, canvas.getWidth(), lineBottom, paint);
    }

    private int getCurrentLine() {
        Layout layout = getLayout();
        if (layout == null)
            return -1;
        return LayoutHelper.getLineOfChar(getLayout(), getSelectionStart());
    }


    private int getVisibleCharIndex(Paint paint, int x, int lineStart, int lineEnd) {
        if (x == 0)
            return lineStart;
        int low = lineStart;
        int high = lineEnd - 1;
        while (low < high) {
            int mid = (high + low) >>> 1;
            float midX = paint.measureText(getText(), lineStart, mid + 1);
            if (x < midX) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

    private long getLineRangeForDraw(Layout layout, Canvas canvas) {
        canvas.save();
        int scrollY = getRealScrollY();
        float clipTop = (scrollY == 0) ? 0
                : getExtendedPaddingTop() + scrollY
                - mParentScrollView.getPaddingTop();
        canvas.clipRect(0, clipTop, getWidth(), scrollY
                + mParentScrollView.getHeight());
        long lineRangeForDraw = LayoutHelper.getLineRangeForDraw(layout, canvas);
        canvas.restore();
        return lineRangeForDraw;
    }

    private int getRealScrollY() {
        return mParentScrollView.getScrollY() + getScrollY();
    }


    private int getRealScrollX() {
        return mParentScrollView.getScrollX() + getScrollX();
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        //调用父类的onSelectionChanged时会发送一个AccessibilityEvent，当文本过大时造成异常
        //super.onSelectionChanged(selStart, selEnd);
        if (mCallback == null || selStart != selEnd) {
            return;
        }
        callCursorChangeCallback(getText(), selStart);
        matchesBracket(getText(), selStart);
    }

    private void matchesBracket(CharSequence text, int cursor) {
        if (checkBracketMatchingAt(text, cursor)) {
            return;
        }
        if (checkBracketMatchingAt(text, cursor - 1)) {
            return;
        }
        mMatchingBrackets[0] = -1;
        mMatchingBrackets[1] = -1;
        mUnmatchedBracket = -1;
    }

    private boolean checkBracketMatchingAt(CharSequence text, int cursor) {
        if (cursor < 0 || cursor >= text.length()) {
            return false;
        }
        int i = BracketMatching.bracketMatching(text, cursor);
        if (i >= 0) {
            mMatchingBrackets[0] = cursor;
            mMatchingBrackets[1] = i;
            mUnmatchedBracket = -1;
            return true;
        } else if (i == UNMATCHED_BRACKET) {
            mUnmatchedBracket = cursor;
            mMatchingBrackets[0] = -1;
            mMatchingBrackets[1] = -1;
            return true;
        }
        return false;

    }

    private void callCursorChangeCallback(CharSequence text, int sel) {
        if (text.length() == 0) {
            return;
        }
        if (mCallback == null)
            return;
        int lineStart = TextUtils.lastIndexOf(text, '\n', sel - 1) + 1;
        if (lineStart < 0) {
            lineStart = 0;
        }
        if (lineStart > text.length() - 1) {
            lineStart = text.length() - 1;
        }
        int lineEnd = TextUtils.indexOf(text, '\n', sel);
        if (lineEnd < 0) {
            lineEnd = text.length();
        }
        if (lineEnd < lineStart || lineStart < 0 || lineEnd > text.length())
            return;
        String line = text.subSequence(lineStart, lineEnd).toString();
        int cursor = sel - lineStart;
        mCallback.onCursorChange(line, cursor);
    }

    public void setCursorChangeCallback(CodeEditor.CursorChangeCallback callback) {
        mCallback = callback;
    }

    public void updateHighlightTokens(JavaScriptHighlighter.HighlightTokens highlightTokens) {
        mHighlightTokens = highlightTokens;
        postInvalidate();
    }

    @Override
    public void setSelection(int index) {
        if (index < 0) {
            index = 0;
        }
        if (index > getText().length()) {
            index = getText().length();
        }
        super.setSelection(index);
    }

}
