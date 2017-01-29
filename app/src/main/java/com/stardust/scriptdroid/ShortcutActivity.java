package com.stardust.scriptdroid;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.stardust.scriptdroid.droid.Droid;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Stardust on 2017/1/23.
 */
public class ShortcutActivity extends Activity {

    interface BooleanSupplier {
        boolean getAsBoolean();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String path = getIntent().getStringExtra("path");
        if (!ensure(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return !TextUtils.isEmpty(path);
            }
        }, R.string.text_path_is_empty))
            return;
        final File scriptFile = new File(path);
        new Domino()
                .then(new Tile() {
                    @Override
                    public boolean fall() {
                        return ShortcutActivity.this.ensure(new BooleanSupplier() {
                            @Override
                            public boolean getAsBoolean() {
                                return scriptFile.exists();
                            }
                        }, R.string.text_file_not_exists);
                    }
                })
                .then(new Tile() {
                    @Override
                    public boolean fall() {
                        return ShortcutActivity.this.ensure(new BooleanSupplier() {
                            @Override
                            public boolean getAsBoolean() {
                                return ShortcutActivity.this.hasStorageReadPermission();
                            }
                        }, R.string.text_no_file_rw_permission);
                    }
                })
                .then(new Tile() {
                    @Override
                    public boolean fall() {
                        ShortcutActivity.this.runScriptFile(path);
                        return true;
                    }
                })
                .fall();
    }

    public void onStart() {
        super.onStart();
        finish();
    }

    private void runScriptFile(String path) {
        try {
            Droid.getInstance().runScriptFile(path);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean ensure(BooleanSupplier bool, String message) {
        boolean b = bool.getAsBoolean();
        if (!b) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        return b;
    }

    private boolean ensure(BooleanSupplier bool, int resId) {
        return ensure(bool, getString(resId));
    }

    private boolean hasStorageReadPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
    }

    interface Tile {
        boolean fall();
    }

    public static class Domino {

        private List<Tile> mTiles = new LinkedList<>();

        Domino then(Tile next) {
            mTiles.add(next);
            return this;
        }

        void fall() {
            for (Tile tile : mTiles) {
                if (!tile.fall())
                    break;
            }
        }
    }

}
