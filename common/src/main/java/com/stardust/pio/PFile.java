package com.stardust.pio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Stardust on 2017/10/19.
 */

public class PFile extends File {

    private String mSimplifyPath;
    private String mSimplifiedName;
    private String mExtension;

    public PFile(@NonNull String pathname) {
        super(pathname);
        init();
    }

    public PFile(String parent, @NonNull String child) {
        super(parent, child);
        init();
    }

    public PFile(File parent, @NonNull String child) {
        super(parent, child);
        init();
    }

    public PFile(@NonNull URI uri) {
        super(uri);
        init();
    }


    private void init() {
        if (isDirectory()) {
            mSimplifiedName = getName();
        } else {
            mSimplifiedName = PFiles.getNameWithoutExtension(getName());
        }
        mSimplifyPath = PFiles.getSimplifiedPath(getPath());
    }


    @NonNull
    public PFile renameTo(String newName) {
        PFile newFile = new PFile(getParent(), newName);
        if (renameTo(newFile)) {
            return newFile;
        } else {
            return this;
        }
    }

    @NonNull
    public PFile renameWithoutExt(String newName) {
        PFile newFile = isDirectory() ? new PFile(getParent(), newName) :
                new PFile(getParent(), newName + "." + getExtension());
        if (renameTo(newFile)) {
            return newFile;
        } else {
            return this;
        }
    }

    public String getExtension() {
        if (mExtension == null) {
            mExtension = PFiles.getExtension(getName());
        }
        return mExtension;
    }

    public String getSimplifiedPath() {
        return mSimplifyPath;
    }

    @Override
    public PFile getParentFile() {
        String p = this.getParent();
        if (p == null)
            return null;
        return new PFile(p);
    }

    @Override
    public PFile[] listFiles() {
        String ss[] = list();
        if (ss == null) return null;
        ArrayList<PFile> files = new ArrayList<>();
        for (int i = 0; i < ss.length; i++) {
            if (!ss[i].startsWith(".")) {
                files.add(new PFile(this, ss[i]));
            }
        }
        return files.toArray(new PFile[files.size()]);
    }

    @Override
    public PFile[] listFiles(FilenameFilter filter) {
        String ss[] = list();
        if (ss == null) return null;
        ArrayList<PFile> files = new ArrayList<>();
        for (String s : ss)
            if (!s.startsWith(".") && (filter == null || filter.accept(this, s)))
                files.add(new PFile(this, s));
        return files.toArray(new PFile[files.size()]);
    }

    @Override
    public PFile[] listFiles(FileFilter filter) {
        String ss[] = list();
        if (ss == null) return null;
        ArrayList<PFile> files = new ArrayList<>();
        for (String s : ss) {
            PFile f = new PFile(this, s);
            if (!f.isHidden() && (filter == null || filter.accept(f)))
                files.add(f);
        }
        return files.toArray(new PFile[files.size()]);

    }

    public String getSimplifiedName() {
        return mSimplifiedName;
    }

    public boolean moveTo(PFile to) {
        return renameTo(new File(to, getName()));
    }

}
