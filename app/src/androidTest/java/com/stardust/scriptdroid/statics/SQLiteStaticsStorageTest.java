package com.stardust.scriptdroid.statics;

import android.support.test.InstrumentationRegistry;

import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.util.MapEntries;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Stardust on 2017/5/5.
 */
public class SQLiteStaticsStorageTest {


    private SQLiteStaticsStorage mStorage;

    @Before
    public void setUp() throws Exception {
        mStorage = new SQLiteStaticsStorage(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void record() throws Exception {
        testOneRecord();
        testTwoRecord();
        testRepeatedRecord();
    }


    @Test
    public void testOneRecord() {
        mStorage.clear();
        mStorage.record(new StringScriptSource("Name", "Script"));
        assertEquals(mStorage.getAll(), new MapEntries<String, String>()
                .entry("Name.js", "1")
                .map());
    }

    @Test
    public void testTwoRecord() {
        mStorage.clear();
        mStorage.record(new StringScriptSource("Name", "Script"));
        mStorage.record(new JavaScriptFileSource("/test/test.js"));
        assertEquals(mStorage.getAll(), new MapEntries<String, String>()
                .entry("Name.js", "1")
                .entry("/test/test.js", "1")
                .map());
    }

    @Test
    public void testRepeatedRecord() {
        mStorage.clear();
        mStorage.record(new JavaScriptFileSource("/test/test.js"));
        mStorage.record(new StringScriptSource("Name", "Script"));
        mStorage.record(new JavaScriptFileSource("/test/test.js"));
        mStorage.record(new JavaScriptFileSource("/test/test.js"));
        assertEquals(mStorage.getAll(), new MapEntries<String, String>()
                .entry("Name.js", "1")
                .entry("/test/test.js", "3")
                .map());
    }


    @Test
    public void getMax() throws Exception {
        mStorage.clear();
        put(new JavaScriptFileSource("/test/test.js"), 50);
        put(new StringScriptSource("Name4", "Script"), 10);
        put(new StringScriptSource("Name5", "Script"), 5);
        put(new StringScriptSource("Name6", "Script"), 4);
        put(new StringScriptSource("Name7", "Script"), 3);
        put(new StringScriptSource("Name8", "Script"), 1);
        put(new StringScriptSource("Name9", "Script"), 1);
        put(new StringScriptSource("Name3", "Script"), 20);
        put(new StringScriptSource("Name1", "Script"), 100);
        assertEquals(mStorage.getMax(5), new MapEntries<String, String>()
                .entry("Name1.js", "100")
                .entry("/test/test.js", "50")
                .entry("Name3.js", "20")
                .entry("Name4.js", "10")
                .entry("Name5.js", "5")
                .map());
    }

    private void put(ScriptSource source, int times) {
        for (int i = 0; i < times; i++) {
            mStorage.record(source);
        }
    }

}