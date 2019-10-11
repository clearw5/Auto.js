package com.stardust.autojs.core.ui.attribute;

import android.graphics.Color;
import android.view.View;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;

import androidx.cardview.widget.CardView;

public class CardAttributes extends ViewAttributes {

    public CardAttributes(ResourceParser resourceParser, View view) {
        super(resourceParser, view);
    }

    @Override
    protected void onRegisterAttrs() {
        super.onRegisterAttrs();
        registerAttr("cardBackgroundColor", Color::parseColor, getView()::setCardBackgroundColor);
        registerPixelAttr("cardCornerRadius", getView()::setRadius);
        registerPixelAttr("cardElevation", getView()::setCardElevation);
        registerPixelAttr("cardMaxElevation", getView()::setMaxCardElevation);
        registerBooleanAttr("cardPreventCornerOverlap", getView()::setPreventCornerOverlap);
        registerBooleanAttr("cardUseCompatPadding", getView()::setUseCompatPadding);
        registerAttr("contentPadding", this::setContentPadding);
        registerIntPixelAttr("contentPaddingBottom", this::setContentPaddingBottom);
        registerIntPixelAttr("contentPaddingLeft", this::setContentPaddingLeft);
        registerIntPixelAttr("contentPaddingTop", this::setContentPaddingTop);
        registerIntPixelAttr("contentPaddingRight", this::setContentPaddingRight);
    }

    private void setContentPadding(String value) {
        int[] pixels = Dimensions.parseToIntPixelArray(getView(), value);
        getView().setContentPadding(pixels[0], pixels[1], pixels[2], pixels[3]);
    }


    private void setContentPaddingBottom(int value) {
        CardView cardView = getView();
        cardView.setContentPadding(cardView.getContentPaddingLeft(), cardView.getContentPaddingTop(),
                cardView.getContentPaddingRight(), value);
    }


    private void setContentPaddingLeft(int value) {
        CardView cardView = getView();
        cardView.setContentPadding(value, cardView.getContentPaddingTop(),
                cardView.getContentPaddingRight(), cardView.getContentPaddingBottom());
    }


    private void setContentPaddingTop(int value) {
        CardView cardView = getView();
        cardView.setContentPadding(cardView.getContentPaddingLeft(), value,
                cardView.getContentPaddingRight(), cardView.getContentPaddingBottom());
    }


    private void setContentPaddingRight(int value) {
        CardView cardView = getView();
        cardView.setContentPadding(cardView.getContentPaddingLeft(), cardView.getContentPaddingTop(),
                value, cardView.getContentPaddingBottom());
    }


    @Override
    public CardView getView() {
        return (CardView) super.getView();
    }

}
