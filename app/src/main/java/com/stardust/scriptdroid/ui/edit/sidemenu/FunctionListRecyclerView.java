package com.stardust.scriptdroid.ui.edit.sidemenu;

import android.workground.WrapContentLinearLayoutManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stardust.pio.PFile;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.widget.ExpandableRecyclerView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Stardust on 2017/2/4.
 */

public class FunctionListRecyclerView extends ExpandableRecyclerView {

    public interface OnFunctionClickListener {
        void onClick(Function function, int position);
    }

    public static class Function {
        public String name;
        String description;

        public Function(String name, String description) {
            this.description = description;
            this.name = name;
        }

    }


    private static final List<Function> FUNCTION_LIST = new ArrayList<>();

    static {
        initFunctionList();
    }

    private List<Function> mFunctionList;
    private OnFunctionClickListener mOnFunctionClickListener;

    public FunctionListRecyclerView(Context context) {
        super(context);
        init();
    }

    public FunctionListRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FunctionListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        setAdapter(new Adapter());
        setFunctionList(FUNCTION_LIST);
        setOnChildClickListener(new OnChildClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (mOnFunctionClickListener != null) {
                    mOnFunctionClickListener.onClick(mFunctionList.get(position), position);
                }
            }
        });
    }

    public void setFunctionList(List<Function> functionList) {
        mFunctionList = functionList;
    }

    public void setOnFunctionClickListener(OnFunctionClickListener onFunctionClickListener) {
        mOnFunctionClickListener = onFunctionClickListener;
    }


    private class Adapter extends ExpandableRecyclerView.DefaultTitleAdapter {

        Adapter() {
            setIcon(R.drawable.ic_function_mathematical_green);
            setTitle(R.string.text_common_function);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            return new ChildViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.function_list_recycler_view_item, parent, false));
        }

        @Override
        protected void onBindChildViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChildViewHolder viewHolder = (ChildViewHolder) holder;
            Function function = mFunctionList.get(position);
            viewHolder.mFunctionName.setText(function.name);
            viewHolder.mDescription.setText(function.description);
        }

        @Override
        protected int getChildItemCount() {
            return mFunctionList.size();
        }

        @Override
        protected int getChildItemViewType(int position) {
            return 0;
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {

        TextView mFunctionName, mDescription;

        public ChildViewHolder(View itemView) {
            super(itemView);
            mFunctionName = (TextView) itemView.findViewById(R.id.function_name);
            mDescription = (TextView) itemView.findViewById(R.id.description);
        }

    }

    private static void initFunctionList() {
        String[] functions = PFile.read(App.getApp().getResources().openRawResource(R.raw.edit_side_menu_functions)).split("\n");
        for (String f : functions) {
            String[] str = f.split(" ");
            FUNCTION_LIST.add(new Function(str[0], str[1]));
        }
        final Comparator cmp = Collator.getInstance();
        Collections.sort(FUNCTION_LIST, new Comparator<Function>() {
            @Override
            public int compare(Function f1, Function f2) {
                return cmp.compare(f1.description, f2.description);
            }
        });
    }
}
