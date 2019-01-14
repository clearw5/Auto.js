package org.autojs.autojs.ui.edit.keyboard;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.autojs.autojs.R;
import org.autojs.autojs.model.indices.Module;
import org.autojs.autojs.model.indices.Modules;
import org.autojs.autojs.model.indices.Property;
import org.autojs.autojs.ui.widget.GridDividerDecoration;
import org.autojs.autojs.workground.WrapContentGridLayoutManger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Stardust on 2017/12/9.
 */

public class FunctionsKeyboardView extends FrameLayout {

    public interface ClickCallback {
        void onModuleLongClick(Module module);

        void onPropertyClick(Module m, Property property);

        void onPropertyLongClick(Module m, Property property);
    }

    private static final int SPAN_COUNT = 4;
    @BindView(R.id.module_list)
    RecyclerView mModulesView;

    @BindView(R.id.properties)
    RecyclerView mPropertiesView;

    private List<Module> mModules;
    private Map<Module, List<Integer>> mSpanSizes = new HashMap<>();
    private Module mSelectedModule;
    private View mSelectedModuleView;
    private Paint mPaint;
    private ClickCallback mClickCallback;

    public FunctionsKeyboardView(@NonNull Context context) {
        super(context);
        init();
    }

    public FunctionsKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FunctionsKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FunctionsKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setClickCallback(ClickCallback clickCallback) {
        mClickCallback = clickCallback;
    }

    private void init() {
        inflate(getContext(), R.layout.functions_keyboard_view, this);
        ButterKnife.bind(this);
        initModulesView();
        initPropertiesView();
    }

    private void initPropertiesView() {
        WrapContentGridLayoutManger manager = new WrapContentGridLayoutManger(getContext(), SPAN_COUNT);
        manager.setDebugInfo("FunctionsKeyboardView");
        mPropertiesView.setLayoutManager(manager);
        mPropertiesView.setAdapter(new PropertiesAdapter());
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                return mSpanSizes.get(mSelectedModule).get(position);
            }
        });
        Drawable divider = ContextCompat.getDrawable(getContext(), R.drawable.divider_functions_view);
        GridDividerDecoration dividerItemDecoration = new GridDividerDecoration(getContext(), divider);
        mPropertiesView.addItemDecoration(dividerItemDecoration);

    }

    private void initSpanSizes(Module module) {
        if (mSpanSizes.containsKey(module))
            return;
        if (getMeasuredWidth() == 0)
            throw new IllegalStateException();
        List<Integer> spanSizes = new ArrayList<>();
        //初始化spanSizes列表
        for (Property property : mSelectedModule.getProperties()) {
            int width = Math.max(getTextWidth(property.getKey()), getTextWidth(property.getSummary()));
            int spanSize = (int) Math.ceil(width / ((double) getMeasuredWidth() / 4));
            spanSizes.add(Math.min(spanSize, 2));
        }
        //遍历这个列表，调整spanSize。例如以下这种情况时:
        // [] [] []
        // [   ] [] []
        // [] [] [] []
        //把第一行的第三个元素的spanSize设置为2
        int column = 0;
        for (int i = 0; i < spanSizes.size(); i++) {
            int spanSize = spanSizes.get(i);
            if (spanSize + column > SPAN_COUNT) {
                spanSizes.set(i - 1, 2);
                column = spanSize;
            } else {
                column += spanSize;
            }
            if (column == 4) {
                column = 0;
            }
        }
        mSpanSizes.put(module, spanSizes);
    }

    private String getDisplayText(Property property) {
        if (TextUtils.isEmpty(property.getSummary()))
            return property.getKey();
        return property.getKey() + "\n" + property.getSummary();
    }

    private int getTextWidth(String text) {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.textSize_item_property));
        }
        Rect r = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), r);
        return r.width();
    }

    private void initModulesView() {
        mModulesView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
        mModulesView.setAdapter(new ModulesAdapter());
    }

    private void loadModules() {
        Modules.getInstance().getModules(getContext())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(modules -> {
                    mModules = modules;
                    if (modules.size() > 0) {
                        setSelectedModule(modules.get(0), null);
                    }
                    mModulesView.getAdapter().notifyDataSetChanged();
                    mPropertiesView.getAdapter().notifyDataSetChanged();
                });
    }


    private void setSelectedModule(Module module, @Nullable View moduleView) {
        mSelectedModule = module;
        if (mSelectedModuleView != null) {
            mSelectedModuleView.setSelected(false);
        }
        mSelectedModuleView = moduleView;
        if (mSelectedModuleView != null)
            mSelectedModuleView.setSelected(true);
        initSpanSizes(mSelectedModule);
        mPropertiesView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mModules == null)
            loadModules();
    }

    private class ModuleViewHolder extends RecyclerView.ViewHolder {


        private TextView mTextView;
        private Module mModule;

        ModuleViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            mTextView.setOnClickListener(v -> {
                if (mModule == null)
                    return;
                setSelectedModule(mModule, mTextView);
            });
            mTextView.setOnLongClickListener(v -> {
                if (mClickCallback != null) {
                    mClickCallback.onModuleLongClick(mModule);
                    return true;
                }
                return false;
            });
        }


        void bind(Module module) {
            mModule = module;
            mTextView.setText(module.getSummary());
            mTextView.setSelected(module == mSelectedModule);
            if (module == mSelectedModule) {
                mSelectedModuleView = mTextView;
            }
        }
    }


    private class PropertyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private Property mProperty;

        PropertyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            mTextView.setOnLongClickListener(v -> {
                if (mClickCallback != null) {
                    mClickCallback.onPropertyLongClick(mSelectedModule, mProperty);
                    return true;
                }
                return false;
            });
            mTextView.setOnClickListener(v -> {
                if (mClickCallback != null) {
                    mClickCallback.onPropertyClick(mSelectedModule, mProperty);
                }
            });
        }

        void bind(Property property) {
            mProperty = property;
            mTextView.setText(getDisplayText(property));
        }
    }

    private class ModulesAdapter extends RecyclerView.Adapter<ModuleViewHolder> {

        @Override
        public ModuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ModuleViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_module, parent, false));
        }

        @Override
        public void onBindViewHolder(ModuleViewHolder holder, int position) {
            holder.bind(mModules.get(position));
        }

        @Override
        public int getItemCount() {
            return mModules == null ? 0 : mModules.size();
        }
    }

    private class PropertiesAdapter extends RecyclerView.Adapter<PropertyViewHolder> {

        @Override
        public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PropertyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_property, parent, false));
        }

        @Override
        public void onBindViewHolder(PropertyViewHolder holder, int position) {
            holder.bind(mSelectedModule.getProperties().get(position));
        }

        @Override
        public int getItemCount() {
            return mSelectedModule == null ? 0 : mSelectedModule.getProperties().size();
        }
    }


}
