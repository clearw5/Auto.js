package org.autojs.autojs.model.explorer;

import org.autojs.autojs.model.script.ScriptFile;

public interface ExplorerItem {

    String TYPE_DIRECTORY = "/";
    String TYPE_JAVASCRIPT = "js";
    String TYPE_AUTO_FILE = "auto";
    String TYPE_JSON = "json";
    String TYPE_XML = "xml";
    String TYPE_UNKNOWN = "?";

    String getName();

    ExplorerPage getParent();

    String getPath();

    long lastModified();

    String getType();

    long getSize();

    ScriptFile toScriptFile();

    boolean isEditable();

    boolean isExecutable();
}
