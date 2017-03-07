package com.stardust.scriptdroid.record.root;

import android.text.TextUtils;
import android.util.Pair;

import com.jecelyin.common.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/3/6.
 */

public class InputEventConverter {

    public static Pair<Double, String> convert(String line) {
        int j = line.indexOf(']');
        double time = 0;
        if (j > 0) {
            time = Double.parseDouble(line.substring(1, j));
            line = line.substring(j + 2);
        }
        String[] str = line.split(" ", 4);
        for (int i = 1; i < 4; i++) {
            str[i] = hex2dec(str[i]);
        }
        str[0] = str[0].substring(0, str[0].length() - 1);
        return new Pair<>(time, "sendevent " + TextUtils.join(" ", str));
    }

    private static String hex2dec(String hex) {
        return String.valueOf((int) Long.parseLong(hex, 16));
    }

    private static class SingleEvent {

        private static final Pattern PATTERN = Pattern.compile("^\\[([^\\]]*)\\]\\s+([^:]*):\\s+([^\\s]*)\\s+([^\\s]*)\\s+([^\\s]*)$");

        double time;
        String device;
        String type;
        String code;
        String value;


        SingleEvent(String str) {
            Matcher matcher = PATTERN.matcher(str);
            matcher.matches();
            time = Double.parseDouble(matcher.group(1));
            device = matcher.group(2);
            type = matcher.group(3);
            code = matcher.group(4);
            value = matcher.group(5);
        }

        @Override
        public String toString() {
            return "SingleEvent{" +
                    "time=" + time +
                    ", device='" + device + '\'' +
                    ", type='" + type + '\'' +
                    ", code='" + code + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public String convertToCode(String events) {
        BufferedReader reader = new BufferedReader(new StringReader(events));
        String line;
        StringBuilder code = new StringBuilder();
        try {
            EventConverter converter = null;
            while ((line = reader.readLine()) != null) {
                SingleEvent event = new SingleEvent(line);
                if (event.value.equals("DOWN") && event.type.equals("BIN_TOUCH")) {
                    converter = new EventConverter();
                } else if (event.value.equals("UP") && event.type.equals("BIN_TOUCH")) {
                    code.append(converter.toString());
                    converter = null;
                } else if (converter != null) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code.toString();
    }

    public static class EventConverter {

        List<SingleEvent> mSingleEvents = new ArrayList<>();

        public void add(SingleEvent event) {
            if (event.type.equals("ABS_MT_POSITION_X") || event.type.equals("ABS_MT_POSITION_Y")) {

            }
        }

    }


}
