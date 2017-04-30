package com.stardust.scriptdroid.ui.main.sample_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.Fragment;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.scripts.sample.Sample;
import com.stardust.scriptdroid.scripts.sample.SampleFileManager;
import com.stardust.scriptdroid.ui.edit.ViewSampleActivity;
import com.stardust.scriptdroid.ui.main.MainActivity;
import com.stardust.scriptdroid.ui.main.operation.ScriptFileOperation;

import java.io.IOException;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSampleScriptListRecyclerView = $(R.id.script_list);
        mSampleScriptListRecyclerView.setSamples(SampleFileManager.getInstance().getSamplesFromAssets(getContext().getAssets(), "sample"));
        mSampleScriptListRecyclerView.setOnItemLongClickListener(new SampleScriptListRecyclerView.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Sample sample) {
                showMenuDialog(sample);
            }
        });
        mSampleScriptListRecyclerView.setOnItemClickListener(new SampleScriptListRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(Sample sample) {
                viewSample(sample);
            }
        });
    }

    private void showMenuDialog(final Sample sample) {
        new MaterialDialog.Builder(getActivity())
                .title(sample.name)
                .items(getString(R.string.text_run), getString(R.string.text_copy_to_my_scripts))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        if (position == 0) {
                            ScriptFileOperation.run(getActivity(), sample);
                        } else {
                            copySampleToMyScripts(sample);
                        }
                    }

                })
                .show();
    }

    private void copySampleToMyScripts(Sample sample) {
        MainActivity.importSample(getActivity(), sample);
    }

    private void viewSample(Sample sample) {
        ViewSampleActivity.view(getContext(), sample);
    }

}
