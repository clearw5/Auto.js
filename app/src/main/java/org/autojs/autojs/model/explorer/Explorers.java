package org.autojs.autojs.model.explorer;

import com.stardust.app.GlobalAppContext;

import org.greenrobot.eventbus.EventBus;

public class Explorers {


    private static Explorer sWorkspaceExplorer = new Explorer(new WorkspaceFileProvider(GlobalAppContext.get(), null), 20);

    private static Explorer sExternalExplorer = new Explorer(new ExplorerFileProvider(), 10);

    public static Explorer workspace() {
        return sWorkspaceExplorer;
    }

    public static Explorer external() {
        return sExternalExplorer;
    }
}
