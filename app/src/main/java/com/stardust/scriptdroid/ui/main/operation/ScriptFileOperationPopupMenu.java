package com.stardust.scriptdroid.ui.main.operation;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stardust.scriptdroid.R;

import static com.stardust.scriptdroid.tool.ViewTool.$;

/**
 * Created by Stardust on 2017/1/24.
 */

public class ScriptFileOperationPopupMenu extends PopupWindow {


    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    private Context mContext;
    private ScriptFileOperationListRecyclerView mOperationListRecyclerView;
    private OnItemClickListener mOnItemClickListener;

    private final View.OnClickListener mOnItemClickRealListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                int position = mOperationListRecyclerView.getChildViewHolder(v).getAdapterPosition();
                mOnItemClickListener.onClick(v, position);
            }
        }
    };

    public ScriptFileOperationPopupMenu(Context context) {
        super();
        mContext = context;
        init();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void show(View anchor) {
        super.showAsDropDown(anchor, 0, -anchor.getWidth());
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
        mOperationListRecyclerView = $(contentView, R.id.operation_list);
        mOperationListRecyclerView.setOnItemClickListener(mOnItemClickRealListener);
    }


    public static class ScriptFileOperationListRecyclerView extends RecyclerView {

        private OnClickListener mOnItemClickListener;

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
                    ScriptFileOperation operation = ScriptFileOperation.getOperation(position);
                    holder.operationName.setText(operation.getName());
                    holder.icon.setImageResource(operation.getIconResId());
                }

                @Override
                public int getItemCount() {
                    return ScriptFileOperation.getOperationNames().size();
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
                operationName = $(itemView, R.id.name);
                icon = $(itemView, R.id.icon);
            }
        }
    }

}
