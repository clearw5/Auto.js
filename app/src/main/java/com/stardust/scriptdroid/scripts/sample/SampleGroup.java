package com.stardust.scriptdroid.scripts.sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/13.
 */

public class SampleGroup {

    public String name;
    public List<Sample> sampleList;

    public SampleGroup(String name) {
        this(name, new ArrayList<Sample>());
    }

    public SampleGroup(String name, List<Sample> samples) {
        this.name = name;
        sampleList = samples;
    }

    public void add(Sample sample){
        sampleList.add(sample);
    }

}