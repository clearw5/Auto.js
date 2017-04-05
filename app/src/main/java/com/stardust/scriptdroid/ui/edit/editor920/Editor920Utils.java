package com.stardust.scriptdroid.ui.edit.editor920;

import com.jecelyin.editor.v2.ui.Document;
import com.jecelyin.editor.v2.ui.EditorDelegate;

import java.lang.reflect.Field;

/**
 * Created by Stardust on 2017/4/5.
 */

public class Editor920Utils {

    public static Document getDocument(EditorDelegate editorDelegate) {
        try {
            Field field = EditorDelegate.class.getDeclaredField("document");
            field.setAccessible(true);
            return (Document) field.get(editorDelegate);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setLang(EditorDelegate editorDelegate, String name) {
        getDocument(editorDelegate).setMode(name);
    }
}
