package org.autojs.autojs.storage.file;

import com.stardust.pio.PFiles;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class FileObservable {

    public static Observable<File> copy(String fromPath, String toPath) {
        return copy(fromPath, toPath, false);
    }

    public static Observable<File> move(String fromPath, String toPath) {
        return copy(fromPath, toPath, true);
    }

    private static Observable<File> copy(String fromPath, String toPath, boolean deleteOld) {
        return new Observable<File>() {
            @Override
            protected void subscribeActual(Observer<? super File> observer) {
                try {
                    copy(new File(fromPath), new File(toPath), deleteOld, observer);
                    observer.onComplete();
                } catch (IOException e) {
                    observer.onError(e);
                }
            }
        };
    }

    private static void copyDir(File fromDir, File toDir, boolean deleteOld, Observer<? super File> progress) throws IOException {
        if (!fromDir.isDirectory()) {
            return;
        }
        File[] files = fromDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            copy(file, new File(toDir, file.getName()), deleteOld, progress);
        }
    }

    private static void copy(File fromFile, File toFile, boolean deleteOld, Observer<? super File> progress) throws IOException {
        progress.onNext(fromFile);
        if (fromFile.isDirectory()) {
            copyDir(fromFile, toFile, deleteOld, progress);
        } else {
            PFiles.ensureDir(toFile.getPath());
            FileUtils.copyFile(fromFile, toFile);
        }
        if (deleteOld) {
            fromFile.delete();
        }
    }


}
