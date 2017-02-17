package com.stardust.scriptdroid.record;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.scriptdroid.droid.runtime.action.FilterAction;
import com.stardust.util.SparseArrayEntries;

import java.util.List;

import static com.stardust.scriptdroid.bounds_assist.BoundsAssistant.boundsToString;
import static com.stardust.scriptdroid.bounds_assist.BoundsAssistant.getBoundsInScreen;


/**
 * Created by Stardust on 2017/2/14.
 */

public class ActionRecorder {

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

    public void record(AccessibilityService service, AccessibilityEvent event) {
        EventToScriptConverter converter = CONVERTER_MAP.get(event.getEventType());
        if (converter != null) {
            if (mFirstAction) {
                mFirstAction = false;
                return;
            }
            converter.onAccessibilityEvent(service, event, mScript);
            mScript.append("\n");
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
            String bounds = boundsToString(getBoundsInScreen(source));
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
            List<AccessibilityNodeInfo> editableList = FilterAction.EditableFilter.findEditable(service.getRootInActiveWindow());
            int i = findInEditableList(editableList, source);
            recycle(editableList);
            sb.append("while(!input(").append(i).append(", \"").append(source.getText()).append("\"));");
            source.recycle();
        }

        private void recycle(List<AccessibilityNodeInfo> list) {
            for (AccessibilityNodeInfo nodeInfo : list) {
                nodeInfo.recycle();
            }
        }


        private static int findInEditableList(List<AccessibilityNodeInfo> editableList, AccessibilityNodeInfo editable) {
            int i = 0;
            for (AccessibilityNodeInfo nodeInfo : editableList) {
                if (getBoundsInScreen(nodeInfo).equals(getBoundsInScreen(editable))) {
                    return i;
                }
                i++;
            }
            return -1;
        }
    }

}
