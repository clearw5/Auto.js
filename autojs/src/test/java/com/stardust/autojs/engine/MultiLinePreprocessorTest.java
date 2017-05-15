package com.stardust.autojs.engine;

import com.stardust.autojs.engine.preprocess.MultiLinePreprocessor;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

/**
 * Created by Stardust on 2017/5/15.
 */
public class MultiLinePreprocessorTest {


    @Test
    public void test1() throws Exception {
        process("(`2\n3\n4`)0\n0");
    }

    @Test
    public void test2() throws Exception {
        process("(`\n23\n4`)0\n0");
    }

    @Test
    public void test3() throws Exception {
        process("(`\n1\n23\n`)0\n0");
    }

    @Test
    public void test4() throws Exception {
        process("(2`\n\"999\"\n3`)0\n0");
    }

    @Test
    public void test5() throws Exception {
        process("(2`\n\"99`99\"\n3`)0\n0");
    }

    @Test
    public void test6() throws Exception {
        process("(2`\n'999'\n3`)0\n0");
    }

    @Test
    public void test7() throws Exception {
        process("12'345\"6789'\n");
    }

    @Test
    public void test8() throws Exception {
        process("12\"345\"6789'\n\"'''");
    }

    @Test
    public void test9() throws Exception {
        process("(2`\n\"99`88'7799\"\n3`)0\n0");
    }

    @Test
    public void test10() throws Exception {
        process("(`\r\n1\r\n23\r\n`)0\r\n0");
    }

    private void process(String s) throws Exception {
        MultiLinePreprocessor preprocessor = new MultiLinePreprocessor();
        Reader reader = preprocessor.preprocess(new StringReader(s));
        int ch;
        while ((ch = reader.read()) != -1) {
            System.out.print((char) ch);
        }
        System.out.println();
    }

}