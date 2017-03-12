package com.stardust.scriptdroid.ui.main.operation;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stardust.scriptdroid.tool.ViewTool;
import com.stardust.scriptdroid.R;

import java.util.List;

/**
 * Created by Stardust on 2017/1/24.
 */

public class ScriptFileOperationPopupMenu extends PopupWindow {


    public interface OnItemClickListener {
        void onClick(View view, int position, ScriptFileOperation operation);
    }

    private Context mContext;
    private ScriptFileOperationListRecyclerView mOperationListRecyclerView;
    private OnItemClickListener mOnItemClickListener;


    private List<ScriptFileOperation> mScriptFileOperationList;

    private final View.OnClickListener mOnItemClickRealListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                int position = mOperationListRecyclerView.getChildViewHolder(v).getAdapterPosition();
                mOnItemClickListener.onClick(v, position, mScriptFileOperationList.get(position));
            }
        }
    };


    public ScriptFileOperationPopupMenu(Context context, List<ScriptFileOperation> scriptFileOperationList) {
        super(context);
        mContext = context;
        mScriptFileOperationList = scriptFileOperationList;
        init();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void show(View anchor) {
        int y = (int) (getScreenHeight() - anchor.getHeight() - getYInScreen(anchor) - getContentHeight());
        y = Math.min(y, -anchor.getHeight());
        PopupWindowCompat.showAsDropDown(this, anchor, 0, y, Gravity.LEFT | Gravity.BOTTOM);
    }

    private int getContentHeight() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return getContentView().getMeasuredHeight();
    }

    private int getYInScreen(View anchor) {
        int[] location = new int[2];
        anchor.getLocationInWindow(location);
        return location[1];
    }


    private float getScreenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    private void init() {
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable());
        setOutsideTouchable(true);
        setAnimationStyle(-1);
        initContentView();
    }

    private void initContentView() {
        View contentView = View.inflate(mContext, R.layout.script_file_operation_popup_menu_content, null);
        setContentView(contentView);
        mOperationListRecyclerView = ViewTool.$(contentView, R.id.operation_list);
        mOperationListRecyclerView.setOnItemClickListener(mOnItemClickRealListener);
        mOperationListRecyclerView.setScriptFileOperationList(mScriptFileOperationList);
    }


    public static class ScriptFileOperationListRecyclerView extends RecyclerView {

        private OnClickListener mOnItemClickListener;

        private List<ScriptFileOperation> mScriptFileOperationList;

        public ScriptFileOperationListRecyclerView(Context context) {
            super(context);
            init();
        }

        public ScriptFileOperationListRecyclerView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ScriptFileOperationListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        public void setScriptFileOperationList(List<ScriptFileOperation> scriptFileOperationList) {
            mScriptFileOperationList = scriptFileOperationList;
        }

        private void init() {
            setLayoutManager(new LinearLayoutManager(getContext()));
            setAdapter(new Adapter<ViewHolder>() {
                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from(getContext()).inflate(R.layout.script_file_operation_popup_menu_item, parent, false);
                    return new ViewHolder(itemView);
                }

                @Override
                public void onBindViewHolder(ViewHolder holder, int position) {
                    ScriptFileOperation operation = mScriptFileOperationList.get(position);
                    holder.operationName.setText(operation.getName());
                    holder.icon.setImageResource(operation.getIconResId());
                }

                @Override
                public int getItemCount() {
                    return mScriptFileOperationList.size();
                }
            });
        }

        public void setOnItemClickListener(OnClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView operationName;
            ImageView icon;

            ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(mOnItemClickListener);
                operationName = ViewTool.$(itemView, R.id.name);
                icon = ViewTool.$(itemView, R.id.icon);
            }
        }
    }

}
