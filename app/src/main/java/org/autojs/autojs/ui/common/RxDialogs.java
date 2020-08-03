package org.autojs.autojs.ui.common;

import android.content.Context;

import org.autojs.autojs.R;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/10/21.
 */

public class RxDialogs {


    public static Observable<Boolean> confirm(Context context, String text) {
        PublishSubject<Boolean> subject = PublishSubject.create();
        new ThemeColorMaterialDialogBuilder(context)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> subject.onNext(true))
                .onNegative((dialog, which) -> subject.onNext(false))
                .content(text)
                .show();
        return subject;
    }

    public static Observable<Boolean> confirm(Context context, int res) {
        return confirm(context, context.getString(res));
    }

}
