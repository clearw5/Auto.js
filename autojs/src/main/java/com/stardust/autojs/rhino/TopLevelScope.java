package com.stardust.autojs.rhino;

import org.apache.log4j.Logger;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TopLevelScope extends ImporterTopLevel {
    private static final Logger logger = Logger.getLogger(TopLevelScope.class);
    private static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setName("TopLevelScope recycle");
        return t;
    });
    private final long createdStamp;
    private long releasedStamp;
    private long recycledStamp;
    private String engineSource;
    private boolean noRecycle;

    public TopLevelScope() {
        super();
        this.createdStamp = System.currentTimeMillis();
    }

/* 不需要覆写 直接交给回收线程1分钟后自动回收
    @Override
    public void finalize() throws Throwable {
        logger.debug("回收TopLevelScope资源[" + engineSource + "], 存活总时间："
                + (System.currentTimeMillis() - this.createdStamp)
                + "ms 释放后经过：" + (System.currentTimeMillis() - releasedStamp) + "ms"
                + (recycledStamp > 0 ? " 资源已经在[" + recycledStamp + "]被完全释放" : "")
        );
        super.finalize();
        recycle();
    }
 */

    public void markReleased(String engineSource) {
        this.engineSource = engineSource;
        releasedStamp = System.currentTimeMillis();
        logger.debug("标记TopLevelScope资源已不再使用[" + engineSource + "], 存活总时间："
                + (releasedStamp - this.createdStamp) + "ms");
        if (noRecycle) {
            return;
        }
        // 1分钟后强制回收
        scheduledExecutor.schedule(this::recycle, 1, TimeUnit.MINUTES);
    }

    /**
     * 回收资源，验证下来发现不是很必要 暂时不处理
     */
    private void recycle() {
        if (recycledStamp > 0) {
            return;
        }
        long start = System.currentTimeMillis();
        try {
            Field importedPackagesField = ImporterTopLevel.class.getDeclaredField("importedPackages");
            importedPackagesField.setAccessible(true);
            importedPackagesField.set(this, null);
        } catch (Exception e) {
            // ...
        }
        try {
            Field associatedValuesField = ScriptableObject.class.getDeclaredField("associatedValues");
            associatedValuesField.setAccessible(true);
            Object value = associatedValuesField.get(this);
            if (value instanceof Map) {
                // 清空针对自身的引用
                ((Map) value).clear();
            }
        } catch (Exception e) {
            // ...
        }

        try {
            Field prototypeValuesField = IdScriptableObject.class.getDeclaredField("prototypeValues");
            prototypeValuesField.setAccessible(true);
            prototypeValuesField.set(this, null);
        } catch (Exception e) {
            //
        }

        try {
            Field slotMap = ScriptableObject.class.getDeclaredField("slotMap");
            slotMap.setAccessible(true);
            slotMap.set(this, null);
        } catch (Exception e) {
            //
        }

        try {
            Field ctorsField = TopLevel.class.getDeclaredField("ctors");
            ctorsField.setAccessible(true);
            Object value = ctorsField.get(this);
            if (value instanceof Map) {
                ((Map) value).clear();
            }
        } catch (Exception e) {
            // ...
        }

        try {
            Field errorsField = TopLevel.class.getDeclaredField("errors");
            errorsField.setAccessible(true);
            Object value = errorsField.get(this);
            if (value instanceof Map) {
                ((Map) value).clear();
            }
        } catch (Exception e) {
            // ...
        }
        setPrototype(null);
        logger.debug("彻底回收[" + engineSource + "]资源 释放后经过："
                + (System.currentTimeMillis() - releasedStamp)
                + "ms 耗时：" + (System.currentTimeMillis() - start) + "ms");
        recycledStamp = System.currentTimeMillis();
    }

    /**
     * 标记当前脚本可以持续运行 不被主动回收 否则十分钟后必备回收
     * 适用于new java.lang.Thread 之类的 后台线程 但实际上不应该有这种操作 很容易内存泄露
     * 这里保留这么个入口
     */
    public void setNoRecycle() {
        noRecycle = true;
    }

    public boolean isRecycled() {
        return recycledStamp > 0;
    }
}
