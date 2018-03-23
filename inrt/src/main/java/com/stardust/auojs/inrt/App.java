package com.stardust.auojs.inrt;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.stardust.app.GlobalAppContext;
import com.stardust.auojs.inrt.autojs.AutoJs;
import com.stardust.auojs.inrt.autojs.GlobalKeyObserver;
import com.stardust.autojs.core.ui.inflater.ImageLoader;
import com.stardust.autojs.core.ui.inflater.util.Drawables;

/**
 * Created by Stardust on 2017/7/1.
 */

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        GlobalAppContext.set(this);
        AutoJs.initInstance(this);
        GlobalKeyObserver.init();
        Drawables.setDefaultImageLoader(new ImageLoader() {
            @Override
            public void loadInto(ImageView imageView, Uri uri) {
                Glide.with(App.this)
                        .load(uri)
                        .into(imageView);
            }

            @Override
            public void loadIntoBackground(View view, Uri uri) {
                Glide.with(App.this)
                        .load(uri)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                view.setBackground(resource);
                            }
                        });
            }

            @Override
            public Drawable load(View view, Uri uri) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void load(View view, Uri uri, DrawableCallback drawableCallback) {
                Glide.with(App.this)
                        .load(uri)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                drawableCallback.onLoaded(resource);
                            }
                        });
            }

            @Override
            public void load(View view, Uri uri, BitmapCallback bitmapCallback) {
                Glide.with(App.this)
                        .asBitmap()
                        .load(uri)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                bitmapCallback.onLoaded(resource);
                            }
                        });
            }
        });
    }

}
