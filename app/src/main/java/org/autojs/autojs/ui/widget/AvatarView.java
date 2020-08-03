package org.autojs.autojs.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import org.autojs.autojs.R;
import org.autojs.autojs.network.NodeBB;
import org.autojs.autojs.network.entity.user.User;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 婷 on 2017/9/29.
 */

public class AvatarView extends FrameLayout {

    @BindView(R.id.icon_text)
    TextView mIconText;

    @BindView(R.id.icon)
    RoundedImageView mIcon;

    private GradientDrawable mIconTextBackground;


    public AvatarView(@NonNull Context context) {
        super(context);
        init();
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.avatar_view, this);
        ButterKnife.bind(this);
        mIconTextBackground = (GradientDrawable) mIconText.getBackground();
    }

    public void setIcon(int resId) {
        mIcon.setVisibility(View.VISIBLE);
        mIconText.setVisibility(View.GONE);
        mIcon.setImageResource(resId);
    }

    public void setUser(final User user) {
        if (TextUtils.isEmpty(user.getPicture())) {
            mIcon.setVisibility(View.GONE);
            mIconText.setVisibility(View.VISIBLE);
            mIconTextBackground.setColor(Color.parseColor(user.getIconBgColor()));
            mIconTextBackground.setCornerRadius(getWidth() / 2);
            mIconText.setText(user.getIconText());
        } else {
            mIcon.setVisibility(View.VISIBLE);
            mIconText.setVisibility(View.GONE);
            mIcon.setCornerRadius(getWidth() / 2);
            Glide.with(getContext())
                    .load(NodeBB.BASE_URL + user.getPicture())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                    )
                    .into(mIcon);
        }
    }
}

