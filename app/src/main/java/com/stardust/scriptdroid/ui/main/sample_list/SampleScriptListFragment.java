package com.stardust.scriptdroid.ui.main.sample_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.app.Fragment;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.sample.SampleFileManager;

/**
 * Created by Stardust on 2017/3/13.
 */

public class SampleScriptListFragment extends Fragment {

    private SampleScriptListRecyclerView mSampleScriptListRecyclerView;

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample_script_list, container, false);
    }

    @Override
    protected void afterCreateView() {
        mSampleScriptListRecyclerView = $(R.id.script_list);
        mSampleScriptListRecyclerView.setSamples(SampleFileManager.getInstance().getSamplesFromAssets(getContext().getAssets(), "sample"));

    }

}
