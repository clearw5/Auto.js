package com.stardust.autojs.rhino;

import org.apache.log4j.Logger;
import org.mozilla.javascript.EmbeddedSlotMap;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;


import java.lang.reflect.Field;
import java.util.Map;

public class TopLevelScope extends ImporterTopLevel {
    private static final Logger logger = Logger.getLogger(TopLevelScope.class);
    private long createdStamp;
    private long releasedStamp;
    private String engineSource;

    public TopLevelScope() {
        super();
        this.createdStamp = System.currentTimeMillis();
    }

    @Override
    public void finalize() {
        logger.debug("回收TopLevelScope资源, 存活总时间：" + (System.currentTimeMillis() - this.createdStamp) + "ms 释放后经过：" + (System.currentTimeMillis() - releasedStamp));
//        recycle();
    }

    public void markReleased(String engineSource) {
        this.engineSource = engineSource;
        releasedStamp = System.currentTimeMillis();
    }

    /**
     * 回收资源，验证下来发现不是很必要 暂时不处理
     */
    private void recycle() {
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
            slotMap.set(this, new EmbeddedSlotMap());
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
    }
}
