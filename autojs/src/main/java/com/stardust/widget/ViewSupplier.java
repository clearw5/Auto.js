package com.stardust.widget;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/4/18.
 * <p>
 * The interface and its implementations must be serializable.
 * Only in this way it can be set to intent extra and be used as service argument.
 * So, implementations should not has any non-serializable fields.
 */


public interface ViewSupplier extends Serializable {

    View inflateView(Context context);
}
