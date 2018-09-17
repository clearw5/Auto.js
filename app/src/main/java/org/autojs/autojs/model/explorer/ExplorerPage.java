package org.autojs.autojs.model.explorer;

import java.util.ArrayList;
import java.util.Collections;

public interface ExplorerPage extends ExplorerItem, Iterable<ExplorerItem> {

    void copyChildren(ExplorerPage page);

    boolean updateChild(ExplorerItem oldItem, ExplorerItem newItem);

    boolean removeChild(ExplorerItem item);

    void addChild(ExplorerItem item);


}
