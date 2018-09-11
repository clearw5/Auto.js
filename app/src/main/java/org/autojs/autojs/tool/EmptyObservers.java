package org.autojs.autojs.tool;

import io.reactivex.functions.Consumer;

public class EmptyObservers {

    private static final Consumer CONSUMER = ignored -> {

    };

    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> consumer() {
        return CONSUMER;
    }
}
