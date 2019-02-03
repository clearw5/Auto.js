package org.autojs.autojs.ui.main.drawer;

import android.annotation.SuppressLint;

import org.autojs.autojs.R;
import org.autojs.autojs.network.UserService;
import org.autojs.autojs.network.entity.notification.Notification;
import org.autojs.autojs.ui.main.community.CommunityFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/12/10.
 */
@SuppressLint("CheckResult")
public class CommunityDrawerMenu {

    private DrawerMenuItem mUnreadItem = new DrawerMenuItem(R.drawable.community_inbox, R.string.text_community_unread, this::showUnread);
    private DrawerMenuItem mLogoutItem = new DrawerMenuItem(R.drawable.ic_exit_to_app_black_24dp, R.string.text_logout, this::logout);
    private DrawerMenuItem mNotificationItem = new DrawerMenuItem(R.drawable.ic_ali_notification, R.string.text_notification, this::showNotifications);

    private List<DrawerMenuItem> mDrawerMenuItems = new ArrayList<>(Arrays.asList(
            new DrawerMenuGroup(R.string.text_community),
            mNotificationItem,
            new DrawerMenuItem(R.drawable.community_list, R.string.text_community_category, this::showCategories),
            mUnreadItem,
            new DrawerMenuItem(R.drawable.community_time, R.string.text_community_recent, this::showRecent),
            new DrawerMenuItem(R.drawable.community_fire, R.string.text_community_popular, this::showPopular),
            new DrawerMenuItem(R.drawable.community_tags, R.string.text_community_tags, this::showTags),
            mLogoutItem
    ));

    private boolean mShown = false;
    private DrawerMenuAdapter mMenuAdapter;


    public void showCommunityMenu(DrawerMenuAdapter adapter) {
        mMenuAdapter = adapter;
        mShown = true;
        List<DrawerMenuItem> items = adapter.getDrawerMenuItems();
        if (items.get(0) == mDrawerMenuItems.get(0)) {
            refreshUserStatus(adapter);
            return;
        }
        items.addAll(0, mDrawerMenuItems);
        adapter.notifyItemRangeInserted(0, mDrawerMenuItems.size());
        adapter.notifyItemChanged(mDrawerMenuItems.size());
        refreshUserStatus(adapter);
    }


    private void refreshUserStatus(DrawerMenuAdapter adapter) {
        UserService.getInstance().refreshOnlineStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(online -> {
                    setUserOnlineStatus(adapter, online);
                    if (online) {
                        refreshNotificationCount(adapter);
                    }
                }, Throwable::printStackTrace);

    }

    public void refreshNotificationCount(DrawerMenuAdapter adapter) {
        UserService.getInstance().getNotifications()
                .flatMap(Observable::fromIterable)
                .filter(n -> !n.isRead())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .count()
                .subscribe(count -> setNotificationCount(adapter, count),
                        Throwable::printStackTrace);
    }

    private void setNotificationCount(DrawerMenuAdapter adapter, long count) {
        mNotificationItem.setNotificationCount((int) count);
        adapter.notifyItemChanged(mNotificationItem);
    }

    public void setUserOnlineStatus(DrawerMenuAdapter adapter, boolean online) {
        if (online) {
            addItem(adapter, R.string.text_community, mNotificationItem);
            addItem(adapter, R.string.text_community_category, mUnreadItem);
            addItem(adapter, R.string.text_community_tags, mLogoutItem);
        } else {
            removeItem(adapter, R.string.text_community_unread);
            removeItem(adapter, R.string.text_logout);
            removeItem(adapter, R.string.text_notification);
        }

    }

    private void addItem(DrawerMenuAdapter adapter, int title, DrawerMenuItem itemToAdd) {
        List<DrawerMenuItem> items = adapter.getDrawerMenuItems();
        for (int i = 0; i < items.size(); i++) {
            DrawerMenuItem item = items.get(i);
            if (item.getTitle() == title) {
                if (i >= items.size() || items.get(i + 1) != itemToAdd) {
                    items.add(i + 1, itemToAdd);
                    adapter.notifyItemInserted(i + 1);
                }
                break;
            }
        }

    }

    private void removeItem(DrawerMenuAdapter adapter, int title) {
        List<DrawerMenuItem> items = adapter.getDrawerMenuItems();
        for (int i = 0; i < items.size(); i++) {
            DrawerMenuItem item = items.get(i);
            if (item.getTitle() == title) {
                items.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
    }


    public void hideCommunityMenu(DrawerMenuAdapter adapter) {
        List<DrawerMenuItem> items = adapter.getDrawerMenuItems();
        mShown = false;
        if (items.isEmpty() || items.get(0) != mDrawerMenuItems.get(0)) {
            return;
        }
        for (int i = 1; i < items.size(); i++) {
            DrawerMenuItem item = items.get(i);
            if (item instanceof DrawerMenuGroup) {
                items.subList(0, i).clear();
                adapter.notifyItemRangeRemoved(0, i);
                adapter.notifyItemChanged(0);
                break;
            }
        }
    }

    private void showNotifications(DrawerMenuItemViewHolder holder) {
        EventBus.getDefault().post(new CommunityFragment.LoadUrl("/notifications"));
        setNotificationCount(mMenuAdapter, 0);
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


    private void logout(DrawerMenuItemViewHolder holder) {
        UserService.getInstance().logout()
                .subscribeOn(Schedulers.io())
                .subscribe();
        EventBus.getDefault().post(new CommunityFragment.LoadUrl("/"));
    }

    public boolean isShown() {
        return mShown;
    }
}
