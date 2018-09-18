package org.autojs.autojs.model.explorer;

import com.stardust.pio.PFile;

import org.autojs.autojs.model.script.ScriptFile;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExplorerFileItem implements ExplorerItem {

    private static final Set<String> sEditableFileExts = new HashSet<>(Arrays.asList(
            "js", "java", "xml", "json", "txt", "log", "ts"
    ));

    private PFile mFile;
    private final ExplorerPage mParent;

    public ExplorerFileItem(PFile file, ExplorerPage parent) {
        mFile = file;
        mParent = parent;
    }

    public ExplorerFileItem(String path, ExplorerPage parent) {
        mFile = new PFile(path);
        mParent = parent;
    }

    public ExplorerFileItem(File file, ExplorerPage parent) {
        mFile = new PFile(file.getPath());
        mParent = parent;
    }

    public PFile getFile() {
        return mFile;
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public ExplorerPage getParent() {
        return mParent;
    }

    @Override
    public String getPath() {
        return mFile.getPath();
    }

    @Override
    public long lastModified() {
        return mFile.lastModified();
    }

    @Override
    public boolean canDelete() {
        return mFile.canWrite();
    }

    @Override
    public boolean canRename() {
        return mFile.canWrite();
    }

    public ExplorerFileItem rename(String newName) {
        return new ExplorerFileItem(mFile.renameAndReturnNewFile(newName), getParent());
    }

    @Override
    public String getType() {
        if (mFile.isDirectory()) {
            return "/";
        }
        return mFile.getExtension();
    }

    @Override
    public long getSize() {
        return mFile.length();
    }

    @Override
    public ScriptFile toScriptFile() {
        return new ScriptFile(mFile);
    }

    @Override
    public boolean isEditable() {
        return sEditableFileExts.contains(getType());
    }

    @Override
    public boolean isExecutable() {
        String type = getType();
        return type.equals("js") || type.equals("auto");
    }
}
