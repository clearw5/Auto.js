package com.stardust.autojs.runtime.api;

import com.stardust.util.Shell;

/**
 * Created by Stardust on 2017/3/7.
 */

public class SlowShell extends AbstractShell {

    private Shell mShell;

    public SlowShell(boolean root) {
        super(root);
    }

    @Override
    protected void init(String initialCommand) {
        mShell = new Shell(initialCommand);
    }

    @Override
    public void exec(String command) {
        mShell.execute(command);
    }


}
