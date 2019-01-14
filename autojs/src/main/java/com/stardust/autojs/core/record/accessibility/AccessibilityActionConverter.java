package com.stardust.autojs.core.record.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.automator.UiObject;
import com.stardust.automator.simple_action.FilterAction;
import com.stardust.util.SparseArrayEntries;
import com.stardust.view.accessibility.AccessibilityNodeInfoHelper;
import com.stardust.view.accessibility.NodeInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Created by Stardust on 2017/2/14.
 */

public class AccessibilityActionConverter {

    private static final SparseArray<EventToScriptConverter> CONVERTER_MAP = new SparseArrayEntries<EventToScriptConverter>()
            .entry(AccessibilityEvent.TYPE_VIEW_CLICKED, new DoUtilSucceedConverter("click"))
            .entry(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED, new DoUtilSucceedConverter("longClick"))
            .entry(AccessibilityEvent.TYPE_VIEW_SCROLLED, new DoOnceConverter("//scroll???"))
            .entry(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED, new SetTextEventConverter())
            .sparseArray();

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CONVERTER_MAP.put(AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED, new DoOnceConverter("contextClick"));
        }
    }

    private StringBuilder mScript = new StringBuilder();
    private boolean mFirstAction = true;

    public AccessibilityActionConverter(boolean shouldIgnoreFirstAction) {
        mShouldIgnoreFirstAction = shouldIgnoreFirstAction;
    }

    private boolean mShouldIgnoreFirstAction = false;

    public void record(AccessibilityService service, AccessibilityEvent event) {
        EventToScriptConverter converter = CONVERTER_MAP.get(event.getEventType());
        if (converter != null) {
            if (mFirstAction && mShouldIgnoreFirstAction) {
                mFirstAction = false;
                return;
            }
            converter.onAccessibilityEvent(service, event, mScript);
            mScript.append("\n");
            EventBus.getDefault().post(new AccessibilityActionRecorder.AccessibilityActionRecordEvent(event));
        }
    }

    public String getScript() {
        return mScript.toString();
    }

    public void onResume() {
        mFirstAction = true;
    }

    interface EventToScriptConverter {

        void onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event, StringBuilder sb);
    }

    private static abstract class BoundsEventConverter implements EventToScriptConverter {

        @Override
        public void onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event, StringBuilder sb) {
            AccessibilityNodeInfo source = event.getSource();
            if (source == null)
                return;
            String bounds = NodeInfo.Companion.boundsToString(AccessibilityNodeInfoHelper.INSTANCE.getBoundsInScreen(source));
            source.recycle();
            onAccessibilityEvent(event, bounds, sb);
        }

        protected abstract void onAccessibilityEvent(AccessibilityEvent event, String bounds, StringBuilder sb);

    }

    private static class DoOnceConverter extends BoundsEventConverter {

        private String mActionFunction;

        DoOnceConverter(String actionFunction) {
            mActionFunction = actionFunction;
        }

        @Override
        protected void onAccessibilityEvent(AccessibilityEvent event, String bounds, StringBuilder sb) {
            sb.append(mActionFunction).append(bounds).append(";");
        }
    }

    private static class DoUtilSucceedConverter extends BoundsEventConverter {

        private String mActionFunction;

        DoUtilSucceedConverter(String actionFunction) {
            mActionFunction = actionFunction;
        }

        @Override
        protected void onAccessibilityEvent(AccessibilityEvent event, String bounds, StringBuilder sb) {
            sb.append("while(!").append(mActionFunction).append(bounds).append(");");
        }
    }

    private static class SetTextEventConverter implements EventToScriptConverter {

        @Override
        public void onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event, StringBuilder sb) {
            AccessibilityNodeInfo source = event.getSource();
            if (source == null)
                return;
            UiObject uiObject = UiObject.Companion.createRoot(service.getRootInActiveWindow());
            List<UiObject> editableList = FilterAction.EditableFilter.Companion.findEditable(uiObject);
            int i = findInEditableList(editableList, source);
            sb.append("while(!input(").append(i).append(", \"").append(source.getText()).append("\"));");
            source.recycle();
        }

        private static int findInEditableList(List<UiObject> editableList, AccessibilityNodeInfo editable) {
            int i = 0;
            for (UiObject nodeInfo : editableList) {
                if (AccessibilityNodeInfoHelper.INSTANCE.getBoundsInScreen(nodeInfo).equals(AccessibilityNodeInfoHelper.INSTANCE.getBoundsInScreen(editable))) {
                    return i;
                }
                i++;
            }
            return -1;
        }
    }

}
