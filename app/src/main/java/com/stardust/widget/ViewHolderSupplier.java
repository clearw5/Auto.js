package com.stardust.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Stardust on 2017/4/8.
 */

public abstract class ViewHolderSupplier<VH extends RecyclerView.ViewHolder> {

    public abstract VH createViewHolder(ViewGroup parent, int viewType);

    public static <VH extends RecyclerView.ViewHolder> ViewHolderSupplier<VH> of(final Class<VH> c, final int layoutRes) {
        return new ViewHolderSupplier<VH>() {
            @Override
            public VH createViewHolder(ViewGroup parent, int viewType) {
                try {
                    Constructor<VH> constructor = c.getConstructor(View.class);
                    return constructor.newInstance(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
