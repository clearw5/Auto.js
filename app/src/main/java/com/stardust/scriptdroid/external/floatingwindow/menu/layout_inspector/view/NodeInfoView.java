package com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floatingwindow.menu.layout_inspector.NodeInfo;
import com.stardust.util.ClipboardUtil;

import java.lang.reflect.Field;

import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

/**
 * Created by Stardust on 2017/3/10.
 */

public class NodeInfoView extends TableView {

    private static final Field[] fields = NodeInfo.class.getFields();
    private String[][] mData = new String[fields.length][2];

    public NodeInfoView(Context context) {
        super(context);
        init();
    }

    public NodeInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NodeInfoView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        for (int i = 0; i < fields.length; i++) {
            try {
                Object value = fields[i].get(nodeInfo);
                mData[i][1] = value == null ? "null" : value.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        getDataAdapter().notifyDataSetChanged();
    }

    private void init() {
        initData();
        setUpTableConfig();
        setAdapter();
        setUpStyle();
    }

    private void setAdapter() {
        setDataAdapter(new TableDataAdapter<String[]>(getContext(), mData) {
            SimpleTableDataAdapter mSimpleTableDataAdapter = new SimpleTableDataAdapter(getContext(), mData);

            @Override
            public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
                final TextView textView = (TextView) mSimpleTableDataAdapter.getCellView(rowIndex, columnIndex, parentView);
                textView.setSingleLine(false);
                textView.setMaxLines(3);
                textView.setTextColor(0xcc000000);
                textView.setTextIsSelectable(true);
                textView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardUtil.setClip(getContext(), textView.getText());
                        Toast.makeText(getContext(), R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show();
                        return true;
                    }

                });
                return textView;
            }
        });
    }

    private void setUpTableConfig() {
        setColumnCount(2);
        setColumnModel(new TableColumnWeightModel(2));
    }

    private void initData() {
        for (int i = 0; i < mData.length; i++) {
            mData[i][0] = fields[i].getName();
            mData[i][1] = "";
        }
    }

    private void setUpStyle() {
        int colorEvenRows = Color.WHITE;
        int colorOddRows = 0xffe7e7e7;
        setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        getChildAt(0).setVisibility(GONE);
    }


}