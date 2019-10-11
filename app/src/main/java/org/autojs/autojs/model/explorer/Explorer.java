package org.autojs.autojs.model.explorer;

import androidx.annotation.Nullable;
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

    public ExplorerProvider getProvider() {
        return mExplorerProvider;
    }

    public void notifyChildrenChanged(ExplorerPage page) {
        clearCache(page);
        mEventBus.post(new ExplorerChangeEvent(page, CHILDREN_CHANGE, null));
    }

    public void notifyItemChanged(ExplorerItem oldItem, ExplorerItem newItem) {
        ExplorerPage parent = getParent(oldItem);
        ExplorerPage cachedParent = getFromCache(parent);
        if (cachedParent != null) {
            cachedParent.updateChild(oldItem, newItem);
        }
        mEventBus.post(new ExplorerChangeEvent(parent, CHANGE, oldItem, newItem));
    }

    private ExplorerPage getFromCache(ExplorerPage parent) {
        if(mExplorerPageLruCache == null || parent == null){
            return null;
        }
        return mExplorerPageLruCache.get(parent.getPath());
    }

    private ExplorerPage getParentFromCache(ExplorerItem item) {
        return getFromCache(getParent(item));
    }


    public void notifyItemRemoved(ExplorerItem item) {
        ExplorerPage parent = getParent(item);
        ExplorerPage cachedParent = getFromCache(parent);
        if (cachedParent != null) {
            cachedParent.removeChild(item);
        }
        mEventBus.post(new ExplorerChangeEvent(parent, REMOVE, item));
    }

    private ExplorerPage getParent(ExplorerItem item) {
        ExplorerPage parent = item.getParent();
        if (parent == null) {
            if (item instanceof ExplorerFileItem) {
                PFile parentFile = ((ExplorerFileItem) item).getFile().getParentFile();
                return new ExplorerDirPage(parentFile, null);
            }
            return null;
        }
        return parent;
    }

    public void notifyItemCreated(ExplorerItem item) {
        ExplorerPage parent = getParent(item);
        ExplorerPage cachedParent = getFromCache(parent);
        if (cachedParent != null) {
            cachedParent.addChild(item);
        }
        mEventBus.post(new ExplorerChangeEvent(parent, CREATE, item, item));
    }

    @SuppressWarnings("unchecked")
    public void refreshAll() {
        if (mExplorerPageLruCache != null)
            mExplorerPageLruCache.evictAll();
        mEventBus.post(ExplorerChangeEvent.EVENT_ALL);
    }


    public Single<ExplorerPage> fetchChildren(ExplorerPage page) {
        ExplorerPage cachedGroup = mExplorerPageLruCache == null ? null : mExplorerPageLruCache.get(page.getPath());
        if (cachedGroup != null) {
            page.copyChildren(cachedGroup);
            return Single.just(page);
        }
        return mExplorerProvider.getExplorerPage(page)
                .observeOn(AndroidSchedulers.mainThread())
                .map(g -> {
                    if (mExplorerPageLruCache != null)
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
