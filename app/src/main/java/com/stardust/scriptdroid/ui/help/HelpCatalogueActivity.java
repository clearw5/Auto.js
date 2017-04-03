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
import android.workground.WrapContentLinearLayoutManager;

import com.stardust.pio.PFile;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardust.scriptdroid.R;

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


    public static void showCatalogue(Context context, String title, String catalogue) {
        context.startActivity(new Intent(context, HelpCatalogueActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("title", title)
                .putExtra("catalogue", catalogue));
    }

    public static void showMainCatalogue(Context context) {
        context.startActivity(new Intent(context, HelpCatalogueActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private static final Map<String, List<Item>> CATALOGUES;

    private static class Item extends JSONObject {

        String title;
        String summary;
        private String type;
        private String path;

        Item(String title, String summary) {
            this.title = title;
            this.summary = summary;
        }

        Item(String title) {
            this(title, null);
        }

        void redirect(Context context) {
            switch (type) {
                case "markdown":
                    DocumentationActivity.openDocumentation(context, title, path + "/" + title + ".md");
                    break;
                case "catalogue":
                    showCatalogue(context, title, title);
                    break;
                case "html":
                    LocalWebViewActivity.openAssetsHtml(context, title, path + "/" + title + ".html");
                    break;
            }

        }
    }

    List<Item> mItems;
    private RecyclerView mRecyclerView;
    private String mTitle;
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
        mTitle = getIntent().getStringExtra("title");
        if (mTitle == null)
            mTitle = getString(R.string.text_help);
        mItems = CATALOGUES.get(catalogue);
    }

    private void setUpUI() {
        setContentView(R.layout.activity_help);
        setToolbarAsBack(mTitle);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mRecyclerView = $(R.id.catalogue);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
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
            String json = PFile.read(App.getApp().getAssets().open("help/catalogue.json"));
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
