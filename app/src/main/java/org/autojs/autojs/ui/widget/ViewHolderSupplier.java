package org.autojs.autojs.ui.widget;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

/**
 * Created by Stardust on 2017/4/8.
 */

public interface ViewHolderSupplier<VH extends RecyclerView.ViewHolder> {

    VH createViewHolder(ViewGroup parent, int viewType);

    interface ViewHolderCreator<VH extends RecyclerView.ViewHolder> {
        VH createViewHolder(View itemView);
    }

    static <VH extends RecyclerView.ViewHolder> ViewHolderSupplier<VH> of(final Class<VH> c, final int layoutRes) {
        return (parent, viewType) -> {
            try {
                Constructor<VH> constructor = c.getConstructor(View.class);
                return constructor.newInstance(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static <VH extends RecyclerView.ViewHolder> ViewHolderSupplier<VH> of(ViewHolderCreator<VH> creator, final int layoutRes) {
        return (parent, viewType) ->
                creator.createViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false)
                );
    }


}
