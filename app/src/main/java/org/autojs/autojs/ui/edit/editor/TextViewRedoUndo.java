package org.autojs.autojs.ui.edit.editor;
/*
 * Copyright 2016. SHENQINCI(沈钦赐)<946736079@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Objects;
import java.util.Stack;

/**
 * 撤销和恢复撤销
 * Created by 沈钦赐 on 16/6/23.
 */
public class TextViewRedoUndo {
    //操作序号(一次编辑可能对应多个操作，如替换文字，就是删除+插入)
    int index;
    //撤销栈
    Stack<Action> history = new Stack<>();
    //恢复栈
    Stack<Action> historyBack = new Stack<>();

    private Editable editable;
    private EditText editText;
    //自动操作标志，防止重复回调,导致无限撤销
    private boolean flag = false;

    private int mInitialHistoryStackSize;

    public TextViewRedoUndo(@NonNull EditText editText) {
        this.editable = editText.getText();
        this.editText = editText;
        editText.addTextChangedListener(new Watcher());
    }

    protected void onEditableChanged(Editable s) {

    }

    protected void onTextChanged(Editable s) {
        if (history.size() < mInitialHistoryStackSize) {
            mInitialHistoryStackSize = 0;
        }
    }


    /**
     * 清理记录
     * Clear history.
     */
    public final void clearHistory() {
        history.clear();
        historyBack.clear();
        mInitialHistoryStackSize = 0;
    }

    public boolean canUndo() {
        return !history.empty();
    }

    /**
     * 撤销
     * Undo.
     */
    public final void undo() {
        if (history.empty()) return;
        //锁定操作
        flag = true;
        Action action = history.pop();
        historyBack.push(action);
        if (action.isAdd) {
            //撤销添加
            editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
            editText.setSelection(action.startCursor, action.startCursor);
        } else {
            //插销删除
            editable.insert(action.startCursor, action.actionTarget);
            if (action.endCursor == action.startCursor) {
                editText.setSelection(action.startCursor + action.actionTarget.length());
            } else {
                editText.setSelection(action.startCursor, action.endCursor);
            }
        }
        //释放操作
        flag = false;
        //判断是否是下一个动作是否和本动作是同一个操作，直到不同为止
        if (!history.empty() && history.peek().index == action.index) {
            undo();
        }
    }

    public boolean canRedo() {
        return !historyBack.empty();
    }

    /**
     * 恢复
     * Redo.
     */
    public final void redo() {
        if (historyBack.empty()) return;
        flag = true;
        Action action = historyBack.pop();
        history.push(action);
        if (action.isAdd) {
            //恢复添加
            editable.insert(action.startCursor, action.actionTarget);
            if (action.endCursor == action.startCursor) {
                editText.setSelection(action.startCursor + action.actionTarget.length());
            } else {
                editText.setSelection(action.startCursor, action.endCursor);
            }
        } else {
            //恢复删除
            editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
            editText.setSelection(action.startCursor, action.startCursor);
        }
        flag = false;
        //判断是否是下一个动作是否和本动作是同一个操作
        if (!historyBack.empty() && historyBack.peek().index == action.index)
            redo();
    }

    /**
     * 首次设置文本
     * Set default text.
     */
    public final void setDefaultText(CharSequence text) {
        clearHistory();
        flag = true;
        editable.replace(0, editable.length(), text);
        flag = false;
    }

    public boolean isTextChanged() {
        return history.size() != mInitialHistoryStackSize;
    }

    public void markTextAsUnchanged() {
        mInitialHistoryStackSize = history.size();
    }

    private class Watcher implements TextWatcher {

        /**
         * Before text changed.
         *
         * @param s     the s
         * @param start the start 起始光标
         * @param count the endCursor 选择数量
         * @param after the after 替换增加的文字数
         */
        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            int end = start + count;
            if (end > start && end <= s.length()) {
                CharSequence charSequence = s.subSequence(start, end);
                //删除了文字
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, false);
                    if (count > 1) {
                        //如果一次超过一个字符，说名用户选择了，然后替换或者删除操作
                        action.setSelectCount(count);
                    } else if (count == 1 && count == after) {
                        //一个字符替换
                        action.setSelectCount(count);
                    }
                    //还有一种情况:选择一个字符,然后删除(暂时没有考虑这种情况)

                    history.push(action);
                    historyBack.clear();
                    action.setIndex(++index);
                }
            }
        }

        /**
         * On text changed.
         *
         * @param s      the s
         * @param start  the start 起始光标
         * @param before the before 选择数量
         * @param count  the endCursor 添加的数量
         */
        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
            if (flag) return;
            int end = start + count;
            if (end > start) {
                CharSequence charSequence = s.subSequence(start, end);
                //添加文字
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, true);

                    history.push(action);
                    historyBack.clear();
                    if (before > 0) {
                        //文字替换（先删除再增加），删除和增加是同一个操作，所以不需要增加序号
                        action.setIndex(index);
                    } else {
                        action.setIndex(++index);
                    }
                }
            }
        }

        @Override
        public final void afterTextChanged(Editable s) {
            if (flag) return;
            if (s != editable) {
                editable = s;
                onEditableChanged(s);
            }
            TextViewRedoUndo.this.onTextChanged(s);
        }

    }

    private class Action {
        /**
         * 改变字符.
         */
        CharSequence actionTarget;
        /**
         * 光标位置.
         */
        int startCursor;
        int endCursor;
        /**
         * 标志增加操作.
         */
        boolean isAdd;
        /**
         * 操作序号.
         */
        int index;


        public Action(CharSequence actionTag, int startCursor, boolean add) {
            this.actionTarget = actionTag;
            this.startCursor = startCursor;
            this.endCursor = startCursor;
            this.isAdd = add;
        }

        public void setSelectCount(int count) {
            this.endCursor = endCursor + count;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }


}