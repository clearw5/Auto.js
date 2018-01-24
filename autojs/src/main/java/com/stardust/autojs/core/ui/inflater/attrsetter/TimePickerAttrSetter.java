package com.stardust.autojs.core.ui.inflater.attrsetter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TimePicker;

import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.inflater.ValueParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;

/**
 * Created by Stardust on 2017/11/29.
 */

public class TimePickerAttrSetter extends BaseViewAttrSetter<TimePicker> {

    public TimePickerAttrSetter(ValueParser valueParser) {
        super(valueParser);
    }

    @Nullable
    @Override
    public ViewCreator<TimePicker> getCreator() {
        return (context, attrs) -> {
            String datePickerMode = attrs.remove("android:timePickerMode");
            if (datePickerMode == null || !datePickerMode.equals("spinner")) {
                return new TimePicker(context);
            }
            return (TimePicker) View.inflate(context, R.layout.time_picker_spinner, null);
        };
    }
}
