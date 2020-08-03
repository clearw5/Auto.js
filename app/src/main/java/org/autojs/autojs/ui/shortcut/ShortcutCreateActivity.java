package org.autojs.autojs.ui.shortcut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.autojs.autojs.R;
import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autojs.external.shortcut.Shortcut;
import org.autojs.autojs.external.shortcut.ShortcutActivity;
import org.autojs.autojs.external.shortcut.ShortcutManager;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.tool.BitmapTool;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/10/25.
 */

public class ShortcutCreateActivity extends AppCompatActivity {

    public static final String EXTRA_FILE = "file";
    private static final String LOG_TAG = "ShortcutCreateActivity";
    private ScriptFile mScriptFile;
    private boolean mIsDefaultIcon = true;

    @BindView(R.id.name)
    TextView mName;

    @BindView(R.id.icon)
    ImageView mIcon;

    @BindView(R.id.use_android_n_shortcut)
    CheckBox mUseAndroidNShortcut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScriptFile = (ScriptFile) getIntent().getSerializableExtra(EXTRA_FILE);
        showDialog();
    }

    private void showDialog() {
        View view = View.inflate(this, R.layout.shortcut_create_dialog, null);
        ButterKnife.bind(this, view);
        mUseAndroidNShortcut.setVisibility(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                View.VISIBLE : View.GONE);
        mName.setText(mScriptFile.getSimplifiedName());
        new ThemeColorMaterialDialogBuilder(this)
                .customView(view, false)
                .title(R.string.text_send_shortcut)
                .positiveText(R.string.ok)
                .onPositive((dialog, which) -> {
                    createShortcut();
                    finish();
                })
                .cancelListener(dialog -> finish())
                .show();
    }


    @OnClick(R.id.icon)
    void selectIcon() {
        ShortcutIconSelectActivity_.intent(this)
                .startForResult(21209);
    }


    @SuppressLint("NewApi") //for fool android studio
    private void createShortcut() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && mUseAndroidNShortcut.isChecked())
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createShortcutByShortcutManager();
            return;
        }
        Shortcut shortcut = new Shortcut(this);
        if (mIsDefaultIcon) {
            shortcut.iconRes(R.drawable.ic_node_js_black);
        } else {
            Bitmap bitmap = BitmapTool.drawableToBitmap(mIcon.getDrawable());
            shortcut.icon(bitmap);
        }
        shortcut.name(mName.getText().toString())
                .targetClass(ShortcutActivity.class)
                .extras(new Intent().putExtra(ScriptIntents.EXTRA_KEY_PATH, mScriptFile.getPath()))
                .send();
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void createShortcutByShortcutManager() {
        Icon icon;
        if (mIsDefaultIcon) {
            icon = Icon.createWithResource(this, R.drawable.ic_file_type_js);
        } else {
            Bitmap bitmap = BitmapTool.drawableToBitmap(mIcon.getDrawable());
            icon = Icon.createWithBitmap(bitmap);
        }
        PersistableBundle extras = new PersistableBundle(1);
        extras.putString(ScriptIntents.EXTRA_KEY_PATH, mScriptFile.getPath());
        Intent intent = new Intent(this, ShortcutActivity.class)
                .putExtra(ScriptIntents.EXTRA_KEY_PATH, mScriptFile.getPath())
                .setAction(Intent.ACTION_MAIN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager.getInstance(this).addPinnedShortcut(mName.getText(), mScriptFile.getPath(), icon, intent);
        } else {
            ShortcutManager.getInstance(this).addDynamicShortcut(mName.getText(), mScriptFile.getPath(), icon, intent);
        }

    }


    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        String packageName = data.getStringExtra(ShortcutIconSelectActivity.EXTRA_PACKAGE_NAME);
        if (packageName != null) {
            try {
                mIcon.setImageDrawable(getPackageManager().getApplicationIcon(packageName));
                mIsDefaultIcon = false;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }
        Uri uri = data.getData();
        if(uri == null){
            return;
        }
        Observable.fromCallable(() -> BitmapFactory.decodeStream(getContentResolver().openInputStream(uri)))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((bitmap -> {
                    mIcon.setImageBitmap(bitmap);
                    mIsDefaultIcon = false;
                }), error -> {
                    Log.e(LOG_TAG, "decode stream", error);
                });

    }
}
