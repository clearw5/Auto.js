package com.stardust.scriptdroid.ui.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.file.FileUtils;
import com.stardust.scriptdroid.ui.BaseActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/3/6.
 */

public class HelpCatalogueActivity extends BaseActivity {

    public static void showCatalogue(Context context, String catalogue) {
        context.startActivity(new Intent(context, HelpCatalogueActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("catalogue", catalogue));
    }

    public static void showCatalogue(Context context) {
        showCatalogue(context, "main");
    }

    private static final Map<String, List<Item>> CATALOGUES;

    private static class Item extends JSONObject {

        String title;
        String summary;
        private String catalogue;
        private String documentation;

        Item(String title, String summary) {
            this.title = title;
            this.summary = summary;
        }

        Item(String title) {
            this(title, null);
        }

        Item catalogue(String catalogue) {
            this.catalogue = catalogue;
            return this;
        }

        Item documentation(String documentation) {
            this.documentation = documentation;
            return this;
        }

        void redirect(Context context) {
            if (documentation != null) {
                DocumentationActivity.openDocumentation(context, title, documentation);
            } else if (catalogue != null) {
                showCatalogue(context, catalogue);
            } else {
                DocumentationActivity.openDocumentation(context, title, title);
            }
        }
    }

    List<Item> mItems;
    private RecyclerView mRecyclerView;
    private View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
            if (holder != null) {
                mItems.get(holder.getAdapterPosition()).redirect(HelpCatalogueActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent();
        setUpUI();
    }

    private void handleIntent() {
        String catalogue = getIntent().getStringExtra("catalogue");
        if (catalogue == null)
            catalogue = "main";
        mItems = CATALOGUES.get(catalogue);
    }

    private void setUpUI() {
        setContentView(R.layout.activity_help);
        setToolbarAsBack(getString(R.string.text_help));
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mRecyclerView = $(R.id.catalogue);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new Adapter());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int view = R.layout.activity_help_catalogue_item;
            return new ViewHolder(LayoutInflater.from(HelpCatalogueActivity.this).inflate(view, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Item item = mItems.get(position);
            holder.title.setText(item.title);
            if (item.summary != null) {
                holder.summary.setVisibility(View.VISIBLE);
                holder.summary.setText(item.summary);
            } else {
                holder.summary.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, summary;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mOnItemClickListener);
            title = (TextView) itemView.findViewById(R.id.title);
            summary = (TextView) itemView.findViewById(R.id.summary);
        }
    }

    static {
        Map<String, List<Item>> fromJson;
        try {
            String json = FileUtils.readString(App.getApp().getAssets().open("help/catalogue.json"));
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<Item>>>() {
            }.getType();
            fromJson = gson.fromJson(json, type);
        } catch (IOException e) {
            fromJson = new HashMap<>();
            e.printStackTrace();
        }
        CATALOGUES = fromJson;
    }
}
