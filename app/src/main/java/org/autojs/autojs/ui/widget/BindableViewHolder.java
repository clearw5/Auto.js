package org.autojs.autojs.ui.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Stardust on 2017/4/8.
 */

public abstract class BindableViewHolder<DataType> extends RecyclerView.ViewHolder{


    public BindableViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(DataType data, int position);
}
