package com.stardust.autojs.engine.preprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by Stardust on 2017/5/15.
 */

public abstract class AbstractProcessor implements Preprocessor {

    @Override
    public Reader preprocess(Reader reader) throws IOException {
        reset();
        int ch;
        if (!(reader instanceof BufferedReader))
            reader = new BufferedReader(reader);
        while ((ch = reader.read()) != -1) {
            handleChar(ch);
        }
        return getReaderAndClear();
    }

    protected abstract void handleChar(int ch);

    public abstract void reset();

    public abstract Reader getReaderAndClear();

}
