package autojs.script.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Stardust on 2017/3/27.
 */

public class StorageScriptFileList extends ScriptFileList {

    private List<ScriptFile> mFiles;
    private ScriptFile mFolder;

    public StorageScriptFileList() {
        this(ScriptFile.DEFAULT_DIRECTORY_PATH);
    }


    public ScriptFile getFolder() {
        return mFolder;
    }

    public StorageScriptFileList(ScriptFile folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("file is not folder:" + folder);
        }
        mFolder = folder;
        mFiles = Arrays.asList(folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || (file.getName().endsWith(".js") && !file.getName().startsWith("."));
            }
        }));
        Collections.sort(mFiles, new Comparator<ScriptFile>() {
            @Override
            public int compare(ScriptFile o1, ScriptFile o2) {
                if (o1.isDirectory() != o2.isDirectory()) {
                    return o1.isDirectory() ? -1 : 1;
                }
                return 0;
            }
        });
    }

    public StorageScriptFileList(String path) {
        this(new ScriptFile(path));
    }


    @Override
    public void add(ScriptFile scriptFile) {
        try {
            scriptFile.createNewFile();
            mFiles.add(scriptFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ScriptFile get(int i) {
        return mFiles.get(i);
    }

    @Override
    public void remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void rename(int position, String newName, boolean renameFile) {
        if (!renameFile)
            throw new UnsupportedOperationException();
        mFiles.get(position).renameTo(newName);
    }

    @Override
    public int size() {
        return mFiles.size();
    }

    @Override
    public boolean containsPath(String path) {
        return false;
    }
}
