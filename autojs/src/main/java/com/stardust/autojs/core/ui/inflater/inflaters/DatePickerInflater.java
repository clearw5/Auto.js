package com.stardust.autojs.core.ui.inflater.inflaters;

import android.os.Build;
import androidx.annotation.Nullable;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;


import com.stardust.autojs.R;
import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Stardust on 2017/11/29.
 */

public class DatePickerInflater extends BaseViewInflater<DatePicker> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy", Locale.getDefault());

    public DatePickerInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(DatePicker view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "calendarTextColor":
                Exceptions.unsupports(view, attr, value);
                break;
            case "calendarViewShown":
                view.setCalendarViewShown(Boolean.parseBoolean(attr));
                break;
            case "dayOfWeekBackground":
            case "dayOfWeekTextAppearance":
            case "endYear":
                Exceptions.unsupports(view, attr, value);
                break;
            case "firstDayOfWeek":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setFirstDayOfWeek(Integer.parseInt(value));
                }
                break;
            case "headerBackground":
            case "headerDayOfMonthTextAppearance":
            case "headerMonthTextAppearance":
            case "headerYearTextAppearance":
                Exceptions.unsupports(view, attr, value);
                break;
            case "maxDate":
                try {
                    view.setMaxDate(DATE_FORMAT.parse(value).getTime());
                } catch (ParseException e) {
                    throw new InflateException(e);
                }
                break;
            case "minDate":
                try {
                    view.setMinDate(DATE_FORMAT.parse(value).getTime());
                } catch (ParseException e) {
                    throw new InflateException(e);
                }
                break;
            case "spinnersShown":
                view.setSpinnersShown(Boolean.parseBoolean(value));
                break;
            case "startYear":
            case "yearListItemTextAppearance":
            case "yearListSelectorColor":
                Exceptions.unsupports(view, attr, value);
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    @Nullable
    @Override
    public ViewCreator<DatePicker> getCreator() {
        return (context, attrs) -> {
            String datePickerMode = attrs.remove("android:datePickerMode");
            if (datePickerMode == null || !datePickerMode.equals("spinner")) {
                return new DatePicker(context);
            }
            DatePicker datePicker = (DatePicker) View.inflate(context, R.layout.date_picker_spinner, null);
            datePicker.setCalendarViewShown(false);
            return datePicker;
        };
    }
}
