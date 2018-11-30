package org.autojs.autojs.ui.main.drawer;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.autojs.autojs.R;
import org.autojs.autojs.ui.widget.BindableViewHolder;

/**
 * Created by Stardust on 2017/12/10.
 */

public class DrawerMenuGroupViewHolder extends BindableViewHolder<DrawerMenuItem> {

    private TextView mTextView;

    public DrawerMenuGroupViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.title);
    }

    @Override
    public void bind(DrawerMenuItem data, int position) {
        mTextView.setText(data.getTitle());
        int padding = itemView.getResources().getDimensionPixelOffset(R.dimen.divider_drawer_menu_group);
        itemView.setPadding(0, position == 0 ? 0 : padding, 0, 0);
    }
}
