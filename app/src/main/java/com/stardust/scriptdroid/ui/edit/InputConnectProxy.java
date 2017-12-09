package com.stardust.scriptdroid.ui.edit;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;

/**
 * Created by Stardust on 2017/11/4.
 */

public class InputConnectProxy implements InputConnection {

    private InputConnection mInputConnection;

    public InputConnectProxy(InputConnection inputConnection) {
        mInputConnection = inputConnection;
    }

    @Override
    public CharSequence getTextBeforeCursor(int n, int flags) {
        return mInputConnection.getTextBeforeCursor(n, flags);
    }

    @Override
    public CharSequence getTextAfterCursor(int n, int flags) {
        return mInputConnection.getTextAfterCursor(n, flags);
    }

    @Override
    public CharSequence getSelectedText(int flags) {
        return mInputConnection.getSelectedText(flags);
    }

    @Override
    public int getCursorCapsMode(int reqModes) {
        return mInputConnection.getCursorCapsMode(reqModes);
    }

    @Override
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        return mInputConnection.getExtractedText(request, flags);
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        return mInputConnection.deleteSurroundingText(beforeLength, afterLength);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) {
        return mInputConnection.deleteSurroundingTextInCodePoints(beforeLength, afterLength);
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        return mInputConnection.setComposingText(text, newCursorPosition);
    }

    @Override
    public boolean setComposingRegion(int start, int end) {
        return mInputConnection.setComposingRegion(start, end);
    }

    @Override
    public boolean finishComposingText() {
        return mInputConnection.finishComposingText();
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        return mInputConnection.commitText(text, newCursorPosition);
    }

    @Override
    public boolean commitCompletion(CompletionInfo text) {
        return mInputConnection.commitCompletion(text);
    }

    @Override
    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        return mInputConnection.commitCorrection(correctionInfo);
    }

    @Override
    public boolean setSelection(int start, int end) {
        return mInputConnection.setSelection(start, end);
    }

    @Override
    public boolean performEditorAction(int editorAction) {
        return mInputConnection.performEditorAction(editorAction);
    }

    @Override
    public boolean performContextMenuAction(int id) {
        return mInputConnection.performContextMenuAction(id);
    }

    @Override
    public boolean beginBatchEdit() {
        return mInputConnection.beginBatchEdit();
    }

    @Override
    public boolean endBatchEdit() {
        return mInputConnection.endBatchEdit();
    }

    @Override
    public boolean sendKeyEvent(KeyEvent event) {
        return mInputConnection.sendKeyEvent(event);
    }

    @Override
    public boolean clearMetaKeyStates(int states) {
        return mInputConnection.clearMetaKeyStates(states);
    }

    @Override
    public boolean reportFullscreenMode(boolean enabled) {
        return mInputConnection.reportFullscreenMode(enabled);
    }

    @Override
    public boolean performPrivateCommand(String action, Bundle data) {
        return mInputConnection.performPrivateCommand(action, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean requestCursorUpdates(int cursorUpdateMode) {
        return mInputConnection.requestCursorUpdates(cursorUpdateMode);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Handler getHandler() {
        return mInputConnection.getHandler();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void closeConnection() {
        mInputConnection.closeConnection();
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    public boolean commitContent(@NonNull InputContentInfo inputContentInfo, int flags, @Nullable Bundle opts) {
        return mInputConnection.commitContent(inputContentInfo, flags, opts);
    }
}
