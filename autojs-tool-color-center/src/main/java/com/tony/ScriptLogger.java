package com.tony;

public interface ScriptLogger {
    void debug(String message);

    void log(String message);

    void error(String message);
}
