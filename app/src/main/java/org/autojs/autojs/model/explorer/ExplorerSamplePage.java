package org.autojs.autojs.model.explorer;

import com.stardust.pio.PFile;

import java.io.File;

public class ExplorerSamplePage extends ExplorerDirPage {

    private boolean mRoot = false;

    public ExplorerSamplePage(PFile file, ExplorerPage parent) {
        super(file, parent);
    }

    public ExplorerSamplePage(String path, ExplorerPage parent) {
        super(path, parent);
    }

    public ExplorerSamplePage(File file, ExplorerPage parent) {
        super(file, parent);
    }

    public boolean isRoot() {
        return mRoot;
    }

    public static ExplorerSamplePage createRoot(PFile dir) {
        ExplorerSamplePage page = new ExplorerSamplePage(dir, null);
        page.mRoot = true;
        return page;
    }

}
