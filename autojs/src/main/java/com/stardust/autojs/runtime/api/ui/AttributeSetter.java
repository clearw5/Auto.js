package com.stardust.autojs.runtime.api.ui;

import android.view.View;

/**
 * Created by Stardust on 2017/5/13.
 */

public interface AttributeSetter<V extends View> {

    boolean putAttribute(V view, String value);


}
