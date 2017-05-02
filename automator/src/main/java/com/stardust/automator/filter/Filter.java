package com.stardust.automator.filter;

import com.stardust.automator.UiObject;

import java.util.List;

/**
 * Created by Stardust on 2017/3/9.
 */

public interface Filter {

    List<UiObject> filter(UiObject node);

}
