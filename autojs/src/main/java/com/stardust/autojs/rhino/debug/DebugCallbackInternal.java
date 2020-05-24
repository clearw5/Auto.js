package com.stardust.autojs.rhino.debug;
/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */


import com.stardust.autojs.engine.RhinoJavaScriptEngine;
import com.stardust.autojs.engine.ScriptEngine;

import org.mozilla.javascript.Context;

/**
 * Interface for communication between the debugger and its GUI.  This
 * should be implemented by the GUI.
 */
interface DebugCallbackInternal {

    /**
     * Called when the source text of some script has been changed.
     */
    void updateSourceText(Dim.SourceInfo sourceInfo);

    /**
     * Called when the interrupt loop has been entered.
     */
    void enterInterrupt(Dim.StackFrame lastFrame,
                        String threadTitle,
                        String alertMessage);

    /**
     * Returns whether the current thread is the GUI's event thread.
     * This information is required to avoid blocking the event thread
     * from the debugger.
     */
    boolean isGuiEventThread();

    /**
     * Processes the next GUI event.  This manual pumping of GUI events
     * is necessary when the GUI event thread itself has been stopped.
     */
    void dispatchNextGuiEvent() throws InterruptedException;

    /**
     * Returns whether the debugger should attach to this engine or not.
     */
    boolean shouldAttachDebugger(RhinoJavaScriptEngine engine);
}