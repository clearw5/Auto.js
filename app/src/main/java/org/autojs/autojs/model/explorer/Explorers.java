package org.autojs.autojs.model.explorer;

import com.stardust.app.GlobalAppContext;

import org.greenrobot.eventbus.EventBus;

public class Explorers {


    private static Explorer sWorkspaceExplorer = new Explorer(Providers.workspace(), 20);

    private static Explorer sExternalExplorer = new Explorer(new ExplorerFileProvider(), 10);

    public static Explorer workspace() {
        return sWorkspaceExplorer;
    }

    public static Explorer external() {
        return sExternalExplorer;
    }

    public static class Providers {
        private static WorkspaceFileProvider sWorkspaceFileProvider = new WorkspaceFileProvider(GlobalAppContext.get(), null);

        public static WorkspaceFileProvider workspace() {
            return sWorkspaceFileProvider;
        }
    }
}
