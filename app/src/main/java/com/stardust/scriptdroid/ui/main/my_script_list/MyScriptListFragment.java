package com.stardust.scriptdroid.ui.main.my_script_list;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stardust.app.Fragment;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.scriptdroid.tool.BackPressedHandler;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.main.operation.ScriptFileOperation;
import com.stardust.util.MessageEvent;
import com.stardust.widget.SimpleAdapterDataObserver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Stardust on 2017/3/13.
 */

public class MyScriptListFragment extends Fragment implements BackPressedHandler {

    public static final String MESSAGE_SCRIPT_FILE_ADDED = "MESSAGE_SCRIPT_FILE_ADDED";

    private ScriptListRecyclerView mScriptListRecyclerView;
    private ScriptFileList mScriptFileList;
    private View mNoScriptHint;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if(!(getActivity() instanceof BaseActivity)){
            throw new IllegalArgumentException("The fragment can only be used in BaseActivity");
        }
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_script_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScriptListRecyclerView = $(R.id.script_list);
        mScriptFileList = ScriptFileList.getImpl();
        mNoScriptHint = $(R.id.hint_no_script);
        mScriptListRecyclerView.getAdapter().registerAdapterDataObserver(new SimpleAdapterDataObserver() {
            @Override
            public void onSomethingChanged() {
                if (mScriptListRecyclerView.getAdapter().getItemCount() == 0) {
                    mNoScriptHint.setVisibility(View.VISIBLE);
                } else {
                    mNoScriptHint.setVisibility(View.GONE);
                }
            }
        });
        mScriptListRecyclerView.setScriptFileList(mScriptFileList);
    }

    //// FIXME: 2017/3/24
    @Override
    public boolean onBackPressed(Activity activity) {
        if (mScriptListRecyclerView.getScriptFileOperationPopupMenu().isShowing()) {
            mScriptListRecyclerView.getScriptFileOperationPopupMenu().dismiss();
            return true;
        }
        return false;
    }

    public ScriptListRecyclerView getScriptListRecyclerView() {
        return mScriptListRecyclerView;
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.message.equals(MESSAGE_SCRIPT_FILE_ADDED)) {
            mScriptListRecyclerView.getAdapter().notifyItemInserted(mScriptFileList.size() - 1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(mScriptListRecyclerView);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(mScriptListRecyclerView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void editLatest() {
        ScriptFileOperation.Edit.getInstance().operate(mScriptListRecyclerView, mScriptFileList, mScriptFileList.size() - 1);
    }
}
