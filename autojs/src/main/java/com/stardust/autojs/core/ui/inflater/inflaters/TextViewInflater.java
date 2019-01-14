package com.stardust.autojs.core.ui.inflater.inflaters;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.TextKeyListener;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;
import com.stardust.autojs.core.ui.inflater.util.Gravities;
import com.stardust.autojs.core.ui.inflater.util.Res;
import com.stardust.autojs.core.ui.inflater.util.Strings;
import com.stardust.autojs.core.ui.inflater.util.ValueMapper;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/3.
 */

public class TextViewInflater<V extends TextView> extends BaseViewInflater<V> {

    private static final int LEFT = 0;
    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 3;


    private static final ValueMapper<Integer> AUTO_LINK_MASKS = new ValueMapper<Integer>("autoLink")
            .map("all", Linkify.ALL)
            .map("email", Linkify.EMAIL_ADDRESSES)
            .map("map", Linkify.MAP_ADDRESSES)
            .map("none", 0)
            .map("phone", Linkify.PHONE_NUMBERS)
            .map("web", Linkify.WEB_URLS);
    private static final ValueMapper<TextUtils.TruncateAt> ELLIPSIZE = new ValueMapper<TextUtils.TruncateAt>("ellipsize")
            .map("end", TextUtils.TruncateAt.END)
            .map("marquee", TextUtils.TruncateAt.MARQUEE)
            .map("none", null)
            .map("start", TextUtils.TruncateAt.START)
            .map("middle", TextUtils.TruncateAt.MIDDLE);
    private static final ValueMapper<Integer> HYPHENATION_FREQUENCY = new ValueMapper<Integer>("hyphenationFrequency")
            .map("full", 2)
            .map("none", 0)
            .map("normal", 1);

    // TODO: 2017/11/4 IME FLAG
    private static final ValueMapper<Integer> IME_OPTIONS = new ValueMapper<Integer>("imeOptions")
            .map("actionDone", EditorInfo.IME_ACTION_DONE)
            .map("actionGo", EditorInfo.IME_ACTION_DONE)
            .map("actionNext", EditorInfo.IME_ACTION_DONE)
            .map("actionNone", EditorInfo.IME_ACTION_DONE)
            .map("actionPrevious", EditorInfo.IME_ACTION_DONE)
            .map("actionSearch", EditorInfo.IME_ACTION_DONE)
            .map("actionSend", EditorInfo.IME_ACTION_DONE)
            .map("actionUnspecified", EditorInfo.IME_ACTION_DONE);

    private static final ValueMapper<Integer> INPUT_TYPES = new ValueMapper<Integer>("inputType")
            .map("date", 0x14)
            .map("datetime", 0x4)
            .map("none", 0x0)
            .map("number", 0x2)
            .map("numberDecimal", 0x2002)
            .map("numberPassword", 0x12)
            .map("numberSigned", 0x1002)
            .map("phone", 0x3)
            .map("text", 0x1)
            .map("textAutoComplete", 0x10001)
            .map("textAutoCorrect", 0x8001)
            .map("textCapCharacters", 0x1001)
            .map("textCapSentences", 0x4001)
            .map("textCapWords", 0x2001)
            .map("textEmailAddress", 0x21)
            .map("textEmailSubject", 0x31)
            .map("textFilter", 0xb1)
            .map("textImeMultiLine", 0x40001)
            .map("textLongMessage", 0x51)
            .map("textMultiLine", 0x20001)
            .map("textNoSuggestions", 0x80001)
            .map("textPassword", 0x81)
            .map("textPersonName", 0x61)
            .map("textPhonetic", 0xc1)
            .map("textPostalAddress", 0x71)
            .map("textShortMessage", 0x41)
            .map("textUri", 0x11)
            .map("textVisiblePassword", 0x91)
            .map("textWebEditText", 0xa1)
            .map("textWebEmailAddress", 0xd1)
            .map("textWebPassword", 0xe1)
            .map("time", 0x24);

    private static final ValueMapper<Integer> INPUT_TYPE_NUMERIC = new ValueMapper<Integer>("numeric")
            .map("decimal", InputType.TYPE_NUMBER_FLAG_DECIMAL)
            .map("number", InputType.TYPE_CLASS_NUMBER)
            .map("signed", InputType.TYPE_NUMBER_FLAG_SIGNED);

    static final ValueMapper<Integer> TEXT_STYLES = new ValueMapper<Integer>("textStyle")
            .map("bold", Typeface.BOLD)
            .map("italic", Typeface.ITALIC)
            .map("normal", Typeface.NORMAL);

    private static final ValueMapper<TextKeyListener.Capitalize> CAPITALIZE = new ValueMapper<TextKeyListener.Capitalize>("capitalize")
            .map("characters", TextKeyListener.Capitalize.CHARACTERS)
            .map("none", TextKeyListener.Capitalize.NONE)
            .map("sentences", TextKeyListener.Capitalize.SENTENCES)
            .map("words", TextKeyListener.Capitalize.WORDS);


    private boolean mAutoText;
    private TextKeyListener.Capitalize mCapitalize;
    private Drawable mDrawableBottom;
    private Drawable mDrawableRight;
    private Drawable mDrawableTop;
    private Drawable mDrawableLeft;
    private Integer mLineSpacingExtra;
    private Integer mLineSpacingMultiplier;
    private String mFontFamily;
    private Integer mTextStyle;
    private String mTypeface;

    public TextViewInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(V view, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {
        if (super.setAttr(view, attrName, value, parent, attrs)) {
            return true;
        }
        switch (attrName) {
            case "autoLink":
                view.setAutoLinkMask(AUTO_LINK_MASKS.get(value));
                break;
            case "autoText":
                mAutoText = Boolean.valueOf(value);
                break;
            case "capitalize":
                mCapitalize = CAPITALIZE.get(value);
                break;
            case "cursorVisible":
                view.setCursorVisible(Boolean.valueOf(value));
                break;
            case "digit":
                if (value.equals("true")) {
                    view.setKeyListener(DigitsKeyListener.getInstance());
                } else if (!value.equals("false")) {
                    view.setKeyListener(DigitsKeyListener.getInstance(value));
                }
                break;
            case "drawableBottom":
                mDrawableBottom = getDrawables().parse(view, value);
                break;
            case "drawableTop":
                mDrawableTop = getDrawables().parse(view, value);
                break;
            case "drawableLeft":
                mDrawableLeft = getDrawables().parse(view, value);
                break;
            case "drawableRight":
                mDrawableRight = getDrawables().parse(view, value);
                break;
            case "drawablePadding":
                view.setCompoundDrawablePadding(Dimensions.parseToIntPixel(value, view));
                break;
            case "drawableStart":
            case "drawableEnd":
            case "editable":
            case "editorExtras":
                Exceptions.unsupports(view, attrName, value);
            case "elegantTextHeight":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setElegantTextHeight(Boolean.valueOf(value));
                }
                break;
            case "ellipsize":
                TextUtils.TruncateAt e = ELLIPSIZE.get(value);
                if (e != null) {
                    view.setEllipsize(e);
                }
                break;
            case "ems":
                view.setEms(Integer.valueOf(value));
                break;
            case "fontFamily":
                mFontFamily = value;
                break;
            case "fontFeatureSettings":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setFontFeatureSettings(value);
                }
                break;
            case "freezesText":
                view.setFreezesText(Boolean.valueOf(value));
                break;
            case "gravity":
                view.setGravity(Gravities.parse(value));
                break;
            case "hint":
                view.setHint(Strings.parse(view, value));
                break;
            case "hyphenationFrequency":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setHyphenationFrequency(HYPHENATION_FREQUENCY.get(value));
                }
                break;
            case "imeActionId":
                view.setImeActionLabel(view.getImeActionLabel(), Integer.valueOf(value));
                break;
            case "imeActionLabel":
                view.setImeActionLabel(value, view.getImeActionId());
                break;
            case "imeOptions":
                view.setImeOptions(IME_OPTIONS.split(value));
                break;
            case "includeFontPadding":
                view.setIncludeFontPadding(Boolean.valueOf(value));
                break;
            case "inputMethod":
                Exceptions.unsupports(view, attrName, value);
            case "inputType":
                view.setInputType(INPUT_TYPES.split(value));
                break;
            case "letterSpacing":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setLetterSpacing(Float.valueOf(value));
                }
                break;
            case "lineSpacingExtra":
                view.setLineSpacing(Dimensions.parseToIntPixel(value, view), view.getLineSpacingMultiplier());
                break;
            case "lineSpacingMultiplier":
                view.setLineSpacing(view.getLineSpacingExtra(), Dimensions.parseToIntPixel(value, view));
                break;
            case "lines":
                view.setLines(Integer.valueOf(value));
                break;
            case "linksClickable":
                view.setLinksClickable(Boolean.valueOf(value));
                break;
            case "marqueeRepeatLimit":
                view.setMarqueeRepeatLimit(value.equals("marquee_forever") ? Integer.MAX_VALUE : Integer.valueOf(value));
                break;
            case "maxEms":
                view.setMaxEms(Integer.valueOf(value));
                break;
            case "maxHeight":
                view.setMaxHeight(Integer.valueOf(value));
                break;
            case "maxLength":
                view.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.valueOf(value))});
                break;
            case "maxLines":
                view.setMaxLines(Integer.valueOf(value));
                break;
            case "maxWidth":
                view.setMaxWidth(Integer.valueOf(value));
                break;
            case "minEms":
                view.setMinEms(Integer.valueOf(value));
                break;
            case "minHeight":
                view.setMinHeight(Dimensions.parseToIntPixel(value, view));
                break;
            case "minLines":
                view.setMinLines(Integer.valueOf(value));
                break;
            case "minWidth":
                view.setMinWidth(Dimensions.parseToIntPixel(value, view));
                break;
            case "numeric":
                view.setInputType(INPUT_TYPE_NUMERIC.split(value) | InputType.TYPE_CLASS_NUMBER);
                break;
            case "password":
                if (value.equals("true")) {
                    view.setInputType(view.getInputType() | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
            case "phoneNumber":
                if (value.equals("true")) {
                    view.setInputType(view.getInputType() | InputType.TYPE_TEXT_VARIATION_PHONETIC);
                }
                break;
            case "privateImeOptions":
                view.setPrivateImeOptions(Strings.parse(view, value));
                break;
            case "scrollHorizontally":
                view.setHorizontallyScrolling(Boolean.valueOf(value));
                break;
            case "selectAllOnFocus":
                view.setSelectAllOnFocus(Boolean.valueOf(value));
                break;
            case "shadowColor":
                view.setShadowLayer(view.getShadowRadius(), view.getShadowDx(), view.getShadowDy(), Colors.parse(view, value));
                break;
            case "shadowDx":
                view.setShadowLayer(view.getShadowRadius(), Dimensions.parseToPixel(value, view), view.getShadowDy(), view.getShadowColor());
                break;
            case "shadowDy":
                view.setShadowLayer(view.getShadowRadius(), view.getShadowDx(), Dimensions.parseToPixel(value, view), view.getShadowColor());
                break;
            case "shadowRadius":
                view.setShadowLayer(Dimensions.parseToPixel(value, view), view.getShadowDx(), view.getShadowDy(), view.getShadowColor());
                break;
            case "singleLine":
                view.setSingleLine(Boolean.valueOf(value));
                break;
            case "textAllCaps":
                view.setAllCaps(Boolean.valueOf(value));
                break;
            case "textAppearance":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setTextAppearance(Res.parseStyle(view, value));
                }
                break;
            case "textColorHighlight":
                view.setHighlightColor(Colors.parse(view, value));
                break;
            case "textColorHint":
                view.setHintTextColor(Colors.parse(view, value));
                break;
            case "textColorLink":
                view.setLinkTextColor(Colors.parse(view, value));
                break;
            case "textIsSelectable":
                view.setTextIsSelectable(Boolean.valueOf(value));
                break;
            case "textScaleX":
                view.setTextScaleX(Dimensions.parseToPixel(value, view));
                break;
            case "textStyle":
                mTextStyle = TEXT_STYLES.split(value);
                break;
            case "typeface":
                mTypeface = value;
                break;
            case "text":
                view.setText(Strings.parse(view, value));
                break;
            case "color":
            case "textColor":
                view.setTextColor(Colors.parse(view.getContext(), value));
                break;
            case "size":
            case "textSize":
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, Dimensions.parseToPixel(value, view));
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void applyPendingAttributes(V view, ViewGroup parent) {
        setTypeface(view);
        setLineSpacing(view);
        setDrawables(view);
        setKeyListener(view);
    }

    private void setKeyListener(V view) {
        if (mCapitalize != null) {
            view.setKeyListener(TextKeyListener.getInstance(mAutoText, mCapitalize));
        }
        mCapitalize = null;
        mAutoText = false;
    }

    private void setDrawable(V view, int d) {
        Drawable[] drawables = view.getCompoundDrawables();
        view.setCompoundDrawables(
                mDrawableLeft != null ? mDrawableLeft : drawables[0],
                mDrawableTop != null ? mDrawableTop : drawables[1],
                mDrawableRight != null ? mDrawableRight : drawables[2],
                mDrawableBottom != null ? mDrawableBottom : drawables[3]
        );
        mDrawableLeft = mDrawableBottom = mDrawableRight = mDrawableTop = null;
    }


    private void setDrawables(V view) {
        Drawable[] drawables = view.getCompoundDrawables();
        view.setCompoundDrawables(
                mDrawableLeft != null ? mDrawableLeft : drawables[0],
                mDrawableTop != null ? mDrawableTop : drawables[1],
                mDrawableRight != null ? mDrawableRight : drawables[2],
                mDrawableBottom != null ? mDrawableBottom : drawables[3]
        );
        mDrawableLeft = mDrawableBottom = mDrawableRight = mDrawableTop = null;
    }

    private void setLineSpacing(V view) {
        if (mLineSpacingExtra != null) {
            view.setLineSpacing(mLineSpacingExtra, mLineSpacingMultiplier == null ? 1 : mLineSpacingMultiplier);
        } else if (mLineSpacingMultiplier != null) {
            view.setLineSpacing(0, mLineSpacingMultiplier);
        }
        mLineSpacingMultiplier = mLineSpacingExtra = null;
    }

    private void setTypeface(V view) {
        if (mFontFamily != null) {
            //ignore typeface as android does
            mTypeface = mFontFamily;
        }
        if (mTypeface != null) {
            if (mTextStyle != null) {
                view.setTypeface(Typeface.create(mTypeface, mTextStyle));
            } else {
                view.setTypeface(Typeface.create(mTypeface, view.getTypeface().getStyle()));
            }
        } else if (mTextStyle != null) {
            view.setTypeface(view.getTypeface(), mTextStyle);
        }
        mTypeface = mFontFamily = null;
        mTextStyle = null;
    }
}
