package com.stardust.autojs.core.ui.inflater.inflaters;

import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TimePicker;

import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;

/**
 * Created by Stardust on 2017/11/29.
 */

public class TimePickerInflater extends BaseViewInflater<TimePicker> {

    public TimePickerInflater(ResourceParser resourceParser) {
        super(resourceParser);
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
