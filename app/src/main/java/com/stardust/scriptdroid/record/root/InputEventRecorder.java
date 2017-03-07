package com.stardust.scriptdroid.record.root;

import android.util.Pair;

import com.stardust.scriptdroid.tool.Shell;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * Created by Stardust on 2017/3/6.
 */

public class InputEventRecorder {

    private Thread mRecordThread;
    private Shell mShell;

    public void record() {
        mShell = new Shell(true);
        mRecordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mShell.execute("getevent -t");
                    readOutput(mShell);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mRecordThread.start();
    }

    private void readOutput(Shell shell) {
        String line;
        try {
            BufferedReader succeedReader = shell.getSucceedReader();
            StringBuilder succeedOutput = shell.getSucceedOutput();
            double time = 0;
            DecimalFormat df = new DecimalFormat("#.###");
            while ((line = succeedReader.readLine()) != null) {
                if (line.startsWith("[")) {
                    Pair<Double, String> c = InputEventConverter.convert(line);
                    if (time == 0) {
                        time = c.first;
                    } else if (c.first - time > 0.1f) {
                        succeedOutput.append("sleep ").append(df.format(c.first - time)).append("\n");
                        time = c.first;
                    }
                    succeedOutput.append(c.second).append("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String stop() {
        mRecordThread.interrupt();
        mShell.destroy();
        return mShell.getSucceedOutput().toString();
    }


}
