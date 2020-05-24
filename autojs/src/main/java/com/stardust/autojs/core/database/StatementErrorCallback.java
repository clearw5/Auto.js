package com.stardust.autojs.core.database;

import android.database.SQLException;

public interface StatementErrorCallback {

    boolean handleEvent(Transaction transaction, SQLException error);

}
