package org.autojs.autojs;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test() {
        Matcher matcher = Pattern.compile("[0-9]+").matcher("2937Finish!");
        if (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void testAutoReorder() {

    }


}