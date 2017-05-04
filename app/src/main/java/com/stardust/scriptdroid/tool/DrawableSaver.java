package com.stardust.scriptdroid.tool;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.pio.PFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Stardust on 2017/4/3.
 */

public abstract class DrawableSaver {

    private static final String PREFIX = "saved_drawable_";

    protected Drawable mOriginalDrawable;
    private Context mContext;
    private String mName;

    public DrawableSaver(Context context, String name, Drawable originalDrawable) {
        mContext = context;
        mName = PREFIX + name;
        mOriginalDrawable = originalDrawable;
        readImageAndApply();
    }

    private void readImageAndApply() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = mContext.openFileInput(mName);
                    Drawable drawable = BitmapDrawable.createFromStream(inputStream, mName);
                    applyDrawableToView(drawable);
                } catch (FileNotFoundException e) {

                }
            }
        }).start();
    }

    public void setDrawable(InputStream inputStream) {
        saveDrawable(inputStream, new Runnable() {
            @Override
            public void run() {
                readImageAndApply();
            }
        });
    }

    private void saveDrawable(final InputStream inputStream, final Runnable callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream outputStream = mContext.openFileOutput(mName, Context.MODE_PRIVATE);
                    PFile.write(inputStream, outputStream);
                    outputStream.close();
                    inputStream.close();
                    callback.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void select(Activity activity, final OnActivityResultDelegate.Mediator mediator) {
        new ImageSelector(activity, mediator, new ImageSelector.ImageSelectorCallback() {
            @Override
            public void onImageSelected(ImageSelector selector, InputStream inputStream) {
                if (inputStream != null)
                    setDrawable(inputStream);
                mediator.removeDelegate(selector);
            }
        }).select();
    }

    public void reset() {
        applyDrawableToView(mOriginalDrawable);
        mContext.deleteFile(mName);
    }

    public static void reset(Context context, String name) {
        context.deleteFile(PREFIX + name);
    }

    protected abstract void applyDrawableToView(Drawable drawable);

    public static class ViewBackgroundSaver extends DrawableSaver {

        private View mView;

        public ViewBackgroundSaver(Context activity, String name, View view) {
            super(activity, name, view.getBackground());
            mView = view;
        }

        @Override
        protected void applyDrawableToView(final Drawable drawable) {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    mView.setBackground(drawable);
                }
            });
        }
    }

    public static class ImageSaver extends DrawableSaver {

        private ImageView mView;

        public ImageSaver(Context activity, String name, ImageView view) {
            super(activity, name, view.getDrawable());
            mView = view;
        }

        @Override
        protected void applyDrawableToView(final Drawable drawable) {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    mView.setImageDrawable(drawable);
                }
            });
        }
    }
}
