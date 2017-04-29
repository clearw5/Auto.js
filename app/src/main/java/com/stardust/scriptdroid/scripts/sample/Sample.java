package com.stardust.scriptdroid.scripts.sample;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/3/13.
 */

public class Sample implements Serializable {
    public String name;
    public String path;

    public Sample(String name, String path) {
        this.name = name;
        this.path = path;
    }

}
