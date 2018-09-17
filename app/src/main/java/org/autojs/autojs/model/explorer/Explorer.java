package org.autojs.autojs.model.explorer;

import android.support.annotation.Nullable;
import android.util.LruCache;

import com.stardust.pio.PFile;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static org.autojs.autojs.model.explorer.ExplorerChangeEvent.*;


public class Explorer {

    private final ExplorerProvider mExplorerProvider;
    private final EventBus mEventBus;
    @Nullable
    private final LruCache<String, ExplorerPage> mExplorerPageLruCache;

    public Explorer(ExplorerProvider explorerProvider, int cacheSize, EventBus eventBus) {
        mExplorerPageLruCache = cacheSize <= 0 ? null : new LruCache<>(cacheSize);
        mExplorerProvider = explorerProvider;
        mEventBus = eventBus;
    }

    public Explorer(ExplorerProvider explorerProvider, int cacheSize) {
        this(explorerProvider, cacheSize, EventBus.getDefault());
    }

    public void notifyChildrenChanged(ExplorerPage itemGroup) {
        clearCache(itemGroup);
        mEventBus.post(new ExplorerChangeEvent(itemGroup));
    }

    public void notifyItemChanged(ExplorerItem oldItem, ExplorerItem newItem) {
        ExplorerPage itemGroup = getParentFromCache(oldItem);
        if (itemGroup != null) {
            itemGroup.updateChild(oldItem, newItem);
        }
        mEventBus.post(new ExplorerChangeEvent(oldItem.getParent(), CHANGE, oldItem, newItem));
    }

    private ExplorerPage getParentFromCache(ExplorerItem item) {
        if (mExplorerPageLruCache == null) {
            return null;
        }
        ExplorerPage parent = item.getParent();
        if (parent == null) {
            if (item instanceof ExplorerFileItem) {
                PFile parentFile = ((ExplorerFileItem) item).getFile().getParentFile();
                return parentFile == null ? null : mExplorerPageLruCache.get(parentFile.getPath());
            }
            return null;
        }
        return mExplorerPageLruCache.get(parent.getPath());
    }


    public void notifyItemRemoved(ExplorerItem item) {
        ExplorerPage itemGroup = getParentFromCache(item);
        if (itemGroup != null) {
            itemGroup.removeChild(item);
        }
        mEventBus.post(new ExplorerChangeEvent(item.getParent(), REMOVE, item));
    }

    public void notifyItemCreated(ExplorerItem item) {
        ExplorerPage itemGroup = getParentFromCache(item);
        if (itemGroup != null) {
            itemGroup.addChild(item);
        }
        mEventBus.post(new ExplorerChangeEvent(item.getParent(), CREATE, item));
    }

    @SuppressWarnings("unchecked")
    public void refreshAll() {
        if (mExplorerPageLruCache != null)
            mExplorerPageLruCache.evictAll();
        mEventBus.post(new ExplorerChangeEvent(ALL));
    }


    public Single<ExplorerPage> fetchChildren(ExplorerPage page) {
        ExplorerPage cachedGroup = mExplorerPageLruCache.get(page.getPath());
        if (cachedGroup != null) {
            page.copyChildren(cachedGroup);
            return Single.just(page);
        }
        return mExplorerProvider.getExplorerPage(page)
                .observeOn(AndroidSchedulers.mainThread())
                .map(g -> {
                    mExplorerPageLruCache.put(g.getPath(), g);
                    page.copyChildren(g);
                    return page;
                });
    }

    private void clearCache(ExplorerPage item) {
        if (mExplorerPageLruCache != null)
            mExplorerPageLruCache.remove(item.getPath());
    }

    public void registerChangeListener(Object subscriber) {
        if (!mEventBus.isRegistered(subscriber))
            mEventBus.register(subscriber);
    }

    public void unregisterChangeListener(Object subscriber) {
        mEventBus.unregister(subscriber);
    }
}
