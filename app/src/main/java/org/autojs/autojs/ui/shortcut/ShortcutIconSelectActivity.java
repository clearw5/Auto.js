package org.autojs.autojs.ui.shortcut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.R;
import org.autojs.autojs.tool.BitmapTool;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.workground.WrapContentGridLayoutManger;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/10/25.
 */
@EActivity(R.layout.activity_shortcut_icon_select)
public class ShortcutIconSelectActivity extends BaseActivity {

    public static final String EXTRA_PACKAGE_NAME = "extra_package_name";

    @ViewById(R.id.apps)
    RecyclerView mApps;

    private PackageManager mPackageManager;
    private List<AppItem> mAppList = new ArrayList<>();

    @AfterViews
    void setupViews() {
        mPackageManager = getPackageManager();
        setToolbarAsBack(getString(R.string.text_select_icon));
        setupApps();
    }

    private void setupApps() {
        mApps.setAdapter(new AppsAdapter());
        WrapContentGridLayoutManger manager = new WrapContentGridLayoutManger(this, 5);
        manager.setDebugInfo("IconSelectView");
        mApps.setLayoutManager(manager);
        loadApps();
    }

    @SuppressLint("CheckResult")
    private void loadApps() {
        List<ApplicationInfo> packages = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        Observable.fromIterable(packages)
                .observeOn(Schedulers.computation())
                .filter(appInfo -> appInfo.icon != 0)
                .map(AppItem::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(icon -> {
                    mAppList.add(icon);
                    mApps.getAdapter().notifyItemInserted(mAppList.size() - 1);
                });

    }

    private void selectApp(AppItem appItem) {
        setResult(RESULT_OK, new Intent()
                .putExtra(EXTRA_PACKAGE_NAME, appItem.info.packageName));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shortcut_icon_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*"), 11234);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    public static Observable<Bitmap> getBitmapFromIntent(Context context, Intent data) {
        String packageName = data.getStringExtra(EXTRA_PACKAGE_NAME);
        if (packageName != null) {
            return Observable.fromCallable(() -> {
                Drawable drawable = context.getPackageManager().getApplicationIcon(packageName);
                return BitmapTool.drawableToBitmap(drawable);
            });
        }
        Uri uri = data.getData();
        if (uri == null) {
            return Observable.error(new IllegalArgumentException("invalid intent"));
        }
        return Observable.fromCallable(() ->
                BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri))
        );
    }

    private class AppItem {
        Drawable icon;
        ApplicationInfo info;

        public AppItem(ApplicationInfo info) {
            this.info = info;
            icon = info.loadIcon(mPackageManager);
        }
    }

    private class AppIconViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;

        public AppIconViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView;
            icon.setOnClickListener(v -> selectApp(mAppList.get(getAdapterPosition())));
        }
    }


    private class AppsAdapter extends RecyclerView.Adapter<AppIconViewHolder> {

        @Override
        public AppIconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AppIconViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.app_icon_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AppIconViewHolder holder, int position) {
            holder.icon.setImageDrawable(mAppList.get(position).icon);
        }

        @Override
        public int getItemCount() {
            return mAppList.size();
        }
    }


}
