package com.stardust.autojs.core.ui.inflater.inflaters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.ViewGroup;
import android.widget.Spinner;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Dimensions;
import com.stardust.autojs.core.ui.inflater.util.Strings;
import com.stardust.autojs.core.ui.inflater.util.ValueMapper;
import com.stardust.autojs.core.ui.widget.JsSpinner;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/29.
 */

public class SpinnerInflater extends BaseViewInflater<JsSpinner> {

    protected static final ValueMapper<Integer> SPINNER_MODES = new ValueMapper<Integer>("spinnerMode")
            .map("dialog", Spinner.MODE_DIALOG)
            .map("dropdown", Spinner.MODE_DROPDOWN);

    public SpinnerInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(JsSpinner view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "dropDownHorizontalOffset":
                view.setDropDownHorizontalOffset(Dimensions.parseToIntPixel(value, view));
                break;
            case "dropDownSelector":
                Exceptions.unsupports(view, attr, value);
                break;
            case "dropDownVerticalOffset":
                view.setDropDownVerticalOffset(Dimensions.parseToIntPixel(value, view));
                break;
            case "dropDownWidth":
                view.setDropDownWidth(Dimensions.parseToIntPixel(value, view));
                break;
            case "popupBackground":
                view.setPopupBackgroundDrawable(getDrawables().parse(view, value));
                break;
            case "prompt":
                view.setPrompt(Strings.parse(view, value));
                break;
            case "entries":
                view.setAdapter(view.new Adapter(view.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, value.split("[|]")));
                break;
            case "textStyle":
                view.setTextStyle(TextViewInflater.TEXT_STYLES.split(value));
                break;
            case "textColor":
                view.setTextColor(Colors.parse(view.getContext(), value));
                break;
            case "textSize":
                view.setTextSize(Dimensions.parseToPixel(value, view));
                break;
            case "entryTextStyle":
                view.setEntryTextStyle(TextViewInflater.TEXT_STYLES.split(value));
                break;
            case "entryTextColor":
                view.setEntryTextColor(Colors.parse(view.getContext(), value));
                break;
            case "entryTextSize":
                view.setEntryTextSize(Dimensions.parseToPixel(value, view));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    @Nullable
    @Override
    public ViewCreator<Spinner> getCreator() {
        return (context, attrs) -> {
            String mode = attrs.remove("android:spinnerMode");
            if (mode == null) {
                return new JsSpinner(context);
            }
            return new JsSpinner(context, SPINNER_MODES.get(mode));
        };
    }


}
