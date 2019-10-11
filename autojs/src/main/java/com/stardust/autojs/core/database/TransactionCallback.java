package com.stardust.autojs.core.database;

public interface TransactionCallback {
    void handleEvent(Transaction transaction);
}
