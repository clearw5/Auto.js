package com.stardust.autojs.core.ui.inflater.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.stardust.autojs.core.ui.inflater.ImageLoader;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Drawables {

    private static final Pattern DATA_PATTERN = Pattern.compile("data:(\\w+/\\w+);base64,(.+)");
    private static ImageLoader sDefaultImageLoader = new DefaultImageLoader();
    private ImageLoader mImageLoader = sDefaultImageLoader;

    public static void setDefaultImageLoader(ImageLoader defaultImageLoader) {
        if (defaultImageLoader == null)
            throw new NullPointerException();
        sDefaultImageLoader = defaultImageLoader;
    }

    public static ImageLoader getDefaultImageLoader() {
        return sDefaultImageLoader;
    }

    public Drawable parse(Context context, String value) {
        Resources resources = context.getResources();
        if (value.startsWith("@color/") || value.startsWith("@android:color/") || value.startsWith("#")) {
            return new ColorDrawable(Colors.parse(context, value));
        }
        if (value.startsWith("?")) {
            return loadAttrResources(context, value);
        }
        if (value.startsWith("file://")) {
            return decodeImage(value.substring(7));
        }
        return loadDrawableResources(context, value);
    }

    public Drawable loadDrawableResources(Context context, String value) {
        int resId = context.getResources().getIdentifier(value, "drawable",
                context.getPackageName());
        if (resId == 0)
            throw new Resources.NotFoundException("drawable not found: " + value);
        return context.getResources().getDrawable(resId);
    }

    public Drawable loadAttrResources(Context context, String value) {
        int[] attr = {context.getResources().getIdentifier(value.substring(1), "attr",
                context.getPackageName())};
        TypedArray ta = context.obtainStyledAttributes(attr);
        Drawable drawable = ta.getDrawable(0 /* index */);
        ta.recycle();
        return drawable;
    }

    public Drawable decodeImage(String path) {
        return new BitmapDrawable(BitmapFactory.decodeFile(path));
    }

    public Drawable parse(View view, String name) {
        return parse(view.getContext(), name);
    }

    public void loadInto(ImageView view, Uri uri) {
        mImageLoader.loadInto(view, uri);
    }

    public void loadIntoBackground(View view, Uri uri) {
        mImageLoader.loadIntoBackground(view, uri);
    }

    public <V extends ImageView> void setupWithImage(V view, String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            loadInto(view, Uri.parse(value));
        } else if (value.startsWith("data:")) {
            loadDataInto(view, value);
        } else {
            view.setImageDrawable(parse(view, value));
        }
    }

    private void loadDataInto(ImageView view, String data) {
        Bitmap bitmap = loadBase64Data(data);
        view.setImageBitmap(bitmap);
    }

    public static Bitmap loadBase64Data(String data) {
        Matcher matcher = DATA_PATTERN.matcher(data);
        String base64;
        if (!matcher.matches() || matcher.groupCount() != 2) {
            base64 = data;
        } else {
            String mimeType = matcher.group(1);
            base64 = matcher.group(2);
        }
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void setupWithViewBackground(View view, String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            loadIntoBackground(view, Uri.parse(value));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(parse(view, value));
            } else {
                view.setBackgroundDrawable(parse(view, value));
            }
        }
    }

    public void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    private static class DefaultImageLoader implements ImageLoader {

        @Override
        public void loadInto(final ImageView view, Uri uri) {
            load(view, uri, view::setImageDrawable);
        }

        @Override
        public void loadIntoBackground(final View view, Uri uri) {
            load(view, uri, view::setBackground);
        }

        @Override
        public Drawable load(View view, Uri uri) {
            try {
                URL url = new URL(uri.toString());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return new BitmapDrawable(view.getResources(), bmp);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void load(View view, Uri uri, final DrawableCallback callback) {
            load(view, uri, (BitmapCallback) bitmap -> callback.onLoaded(new BitmapDrawable(view.getResources(), bitmap)));
        }

        @Override
        public void load(final View view, final Uri uri, final BitmapCallback callback) {
            new Thread(() -> {
                try {
                    URL url = new URL(uri.toString());
                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    view.post(() -> callback.onLoaded(bmp));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

