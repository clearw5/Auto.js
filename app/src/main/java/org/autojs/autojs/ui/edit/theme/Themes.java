package org.autojs.autojs.ui.edit.theme;

import android.content.Context;

import com.stardust.pio.UncheckedIOException;
import org.autojs.autojs.Pref;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2018/2/22.
 */

public class Themes {


    private static final String ASSETS_THEMES_PATH = "editor/theme";
    private static final String DEFAULT_THEME = "Quiet Light";
    private static final String DARK_THEME = "Dark (Visual Studio)";

    private static List<Theme> sThemes;
    private static Theme sDefaultTheme;

    public static Observable<List<Theme>> getAllThemes(Context context) {
        if (sThemes != null) {
            return Observable.just(sThemes);
        }
        PublishSubject<List<Theme>> subject = PublishSubject.create();
        getAllThemesInner(context)
                .subscribeOn(Schedulers.io())
                .subscribe(themes -> {
                    setThemes(themes);
                    subject.onNext(sThemes);
                    subject.onComplete();
                }, Throwable::printStackTrace);
        return subject;
    }

    public static Observable<Theme> getDefault(Context context) {
        if (sDefaultTheme != null)
            return Observable.just(sDefaultTheme);
        return getAllThemes(context)
                .map(themes -> sDefaultTheme);
    }

    private synchronized static void setThemes(List<Theme> themes) {
        if (sThemes != null)
            return;
        sThemes = Collections.unmodifiableList(themes);
        for (Theme theme : sThemes) {
            if (DEFAULT_THEME.equals(theme.getName())) {
                sDefaultTheme = theme;
                return;
            }
        }
        sDefaultTheme = sThemes.get(0);
    }

    private static Observable<List<Theme>> getAllThemesInner(Context context) {
        if (sThemes != null) {
            return Observable.just(sThemes);
        }
        try {
            return Observable.fromArray(context.getAssets().list(ASSETS_THEMES_PATH))
                    .map(file -> context.getAssets().open(ASSETS_THEMES_PATH + "/" + file))
                    .map(stream -> Theme.fromJson(new InputStreamReader(stream)))
                    .collectInto((List<Theme>) new ArrayList<Theme>(), List::add)
                    .toObservable();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Observable<Theme> getCurrent(Context context) {
        String currentTheme = Pref.isNightModeEnabled() ? DARK_THEME : Pref.getCurrentTheme();
        if (currentTheme == null)
            return getDefault(context);
        return getAllThemes(context)
                .map(themes -> {
                    for (Theme theme : themes) {
                        if (currentTheme.equals(theme.getName()))
                            return theme;
                    }
                    return themes.get(0);
                });
    }

    public static void setCurrent(String name) {
        Pref.setCurrentTheme(name);
    }
}
