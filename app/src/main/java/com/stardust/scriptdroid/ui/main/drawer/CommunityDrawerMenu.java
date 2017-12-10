package com.stardust.scriptdroid.ui.main.drawer;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.main.community.CommunityFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Stardust on 2017/12/10.
 */

public class CommunityDrawerMenu {


    private List<DrawerMenuItem> mDrawerMenuItems = new ArrayList<>(Arrays.asList(
            new DrawerMenuGroup(R.string.text_community),
            new DrawerMenuItem(R.drawable.community_list, R.string.text_community_category, this::showCategories),
            new DrawerMenuItem(R.drawable.community_inbox, R.string.text_community_unread, this::showUnread),
            new DrawerMenuItem(R.drawable.community_time, R.string.text_community_recent, this::showRecent),
            new DrawerMenuItem(R.drawable.community_fire, R.string.text_community_popular, this::showPopular),
            new DrawerMenuItem(R.drawable.community_tags, R.string.text_community_tags, this::showTags)
    ));

    private boolean mShown = false;

    public List<DrawerMenuItem> getItems() {
        return mDrawerMenuItems;
    }

    public void showCommunityMenu(DrawerMenuAdapter adapter) {
        List<DrawerMenuItem> items = adapter.getDrawerMenuItems();
        if (items.get(0) == mDrawerMenuItems.get(0)) {
            return;
        }
        items.addAll(0, mDrawerMenuItems);
        adapter.notifyItemRangeInserted(0, mDrawerMenuItems.size());
        adapter.notifyItemChanged(mDrawerMenuItems.size());
    }

    public void hideCommunityMenu(DrawerMenuAdapter adapter) {
        List<DrawerMenuItem> items = adapter.getDrawerMenuItems();
        if (items.isEmpty() || items.get(0) != mDrawerMenuItems.get(0)) {
            return;
        }
        items.subList(0, mDrawerMenuItems.size()).clear();
        adapter.notifyItemChanged(mDrawerMenuItems.size());
        adapter.notifyItemRangeRemoved(0, mDrawerMenuItems.size());
    }

    private void showCategories(DrawerMenuItemViewHolder holder) {
        EventBus.getDefault().post(new CommunityFragment.LoadUrl("/categories"));
    }

    private void showUnread(DrawerMenuItemViewHolder holder) {
        EventBus.getDefault().post(new CommunityFragment.LoadUrl("/unread"));

    }

    private void showRecent(DrawerMenuItemViewHolder holder) {
        EventBus.getDefault().post(new CommunityFragment.LoadUrl("/recent"));

    }

    private void showPopular(DrawerMenuItemViewHolder holder) {
        EventBus.getDefault().post(new CommunityFragment.LoadUrl("/popular"));

    }

    private void showTags(DrawerMenuItemViewHolder holder) {
        EventBus.getDefault().post(new CommunityFragment.LoadUrl("/tags"));

    }


}
