package org.autojs.autojs.tool;

import com.stardust.app.GlobalAppContext;

import io.reactivex.functions.Consumer;

public class Observers {

    private static final Consumer CONSUMER = ignored -> {

    };

    private static final Consumer<Throwable> TOAST_MESSAGE = e -> {
        e.printStackTrace();
        GlobalAppContext.toast(e.getMessage());
    };


    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> consumer() {
        return CONSUMER;
    }

    public static Consumer<Throwable> toastMessage() {
        return TOAST_MESSAGE;
    }
}
