package com.stardust.autojs.core.ui.inflater;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Stardust on 2017/11/3.
 */

public interface ImageLoader {

    interface BitmapCallback {
        void onLoaded(Bitmap bitmap);
    }

    interface DrawableCallback {
        void onLoaded(Drawable drawable);
    }

    void loadInto(ImageView view, Uri uri);

    void loadIntoBackground(View view, Uri uri);

    Drawable load(View view, Uri uri);

    void load(View view, Uri uri, DrawableCallback callback);

    void load(View view, Uri uri, BitmapCallback callback);

}
