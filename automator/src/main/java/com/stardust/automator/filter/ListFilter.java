package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/9.
 */

public interface ListFilter {

    List<UiObject> filter(List<UiObject> nodes);

    abstract class Default implements Filter, ListFilter {

        @Override
        public List<UiObject> filter(List<UiObject> nodes) {
            List<UiObject> list = new ArrayList<>();
            for (UiObject node : nodes) {
                list.addAll(filter(node));
            }
            return list;
        }
    }

}
