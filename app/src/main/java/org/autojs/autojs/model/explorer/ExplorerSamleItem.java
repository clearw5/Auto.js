package org.autojs.autojs.model.explorer;

import com.stardust.pio.PFile;

import java.io.File;

public class ExplorerSamleItem extends ExplorerFileItem {
    public ExplorerSamleItem(PFile file, ExplorerPage parent) {
        super(file, parent);
    }

    public ExplorerSamleItem(String path, ExplorerPage parent) {
        super(path, parent);
    }

    public ExplorerSamleItem(File file, ExplorerPage parent) {
        super(file, parent);
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }
}
