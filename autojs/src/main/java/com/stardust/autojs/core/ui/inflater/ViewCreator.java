package com.stardust.autojs.core.ui.inflater;

import android.content.Context;
import android.view.View;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/29.
 */

public interface ViewCreator<V extends View> {

    V create(Context context, Map<String, String> attrs);

}
