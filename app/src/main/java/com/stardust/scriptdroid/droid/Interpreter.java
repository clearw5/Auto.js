package com.stardust.scriptdroid.droid;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.util.Pair;

import com.stardust.scriptdroid.action.Action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_FOCUS;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_LONG_CLICK;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
import static android.view.accessibility.AccessibilityNodeInfo.ACTION_SELECT;

/**
 * Created by Stardust on 2017/1/21.
 */

public class Interpreter {

    interface Filter {
        boolean filter(String str);
    }

    private static final Filter FILTER_STRING = new Filter() {
        @Override
        public boolean filter(String str) {
            return str.startsWith("\"") && str.endsWith("\"");
        }
    };


    private static final Filter FILTER_ADDRESS = (str) -> str.startsWith("(") && str.endsWith(")");

    private static final Filter FILTER_DESCRIPTION = new Filter() {
        @Override
        public boolean filter(String str) {
            return str.startsWith("[") && str.endsWith("]");
        }
    };

    private static final Filter FILTER_ANY = new Filter() {
        @Override
        public boolean filter(String str) {
            return true;
        }
    };

    private static class StateTree {

        private List<Pair<Filter, StateTree>> mChildren = new LinkedList<>();

        StateTree child(final String str, StateTree child) {
            return child(new Filter() {
                @Override
                public boolean filter(String s) {
                    return str.equals(s);
                }
            }, child);
        }

        StateTree child(Filter filter, StateTree child) {
            mChildren.add(new Pair<>(filter, child));
            return this;
        }

        StateLeaf match(String[] command, int startIndex) {
            for (Pair<Filter, StateTree> child : mChildren) {
                if (child.first.filter(command[startIndex])) {
                    return child.second.match(command, startIndex + 1);
                }
            }
            return null;
        }

        public StateTree child(String command, final int action) {
            return child(command, new StateTree()
                    .child(FILTER_STRING, new StateLeaf() {
                        @Override
                        Action handle(String[] command) {
                            return Action.FindUpwardlyFilterAction.createActionByText(action, StringTool.removeDoubleQuotes(command[1]));
                        }
                    })
                    .child(FILTER_DESCRIPTION, new StateLeaf() {
                        @Override
                        Action handle(String[] command) {
                            return Action.FindUpwardlyFilterAction.createActionByDescription(action, StringTool.removeDoubleQuotes(command[1]));
                        }
                    })
                    .child(FILTER_ADDRESS, new StateLeaf() {
                        @Override
                        Action handle(String[] command) {
                            String[] intStrings = StringTool.removeDoubleQuotes(command[1]).split(",");
                            int[] bounds = Stream.of(intStrings).mapToInt(Integer::parseInt).toArray();
                            Rect rect = new Rect(bounds[0], bounds[1], bounds[2], bounds[3]);
                            return Action.FindDownwardlyDfsFilterAction.createActionByBounds(action, rect);
                        }
                    })
                    .child(FILTER_ANY, new StateLeaf() {
                        @Override
                        Action handle(String[] command) {
                            return Action.FindUpwardlyFilterAction.createActionById(action, StringTool.removeDoubleQuotes(command[1]));
                        }
                    }));
        }
    }

    private abstract static class StateLeaf extends StateTree {

        abstract Action handle(String[] command);

        StateLeaf match(String[] command, int startIndex) {
            return this;
        }
    }

    private Context mContext;

    private StateTree stateTree = new StateTree()
            .child("click", ACTION_CLICK)
            .child("longclick", ACTION_LONG_CLICK)
            .child("focus", ACTION_FOCUS)
            .child("select", ACTION_SELECT)
            .child("forward", ACTION_SCROLL_FORWARD)
            .child("backward", ACTION_SCROLL_BACKWARD)
            .child("scrollup", new StateLeaf() {
                @Override
                Action handle(String[] command) {
                    return new Action.ScrollAction(Action.ScrollAction.SCROLL_BACKWARD);
                }
            }).child("scrolldown", new StateLeaf() {
                @Override
                Action handle(String[] command) {
                    return new Action.ScrollAction(Action.ScrollAction.SCROLL_FORWARD);
                }
            }).child("input", new StateTree().child(FILTER_DESCRIPTION, new StateLeaf() {
                @Override
                Action handle(String[] command) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        return new Action.InputAction(StringTool.removeDoubleQuotes(command[1]), command[2]);
                    } else {
                        return null;
                    }
                }
            })).child("open", new StateTree()
                    .child(FILTER_STRING, new StateLeaf() {
                        @Override
                        Action handle(String[] command) {
                            String appName = StringTool.removeDoubleQuotes(command[1]);
                            List<ApplicationInfo> installedApplications = mContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
                            for (ApplicationInfo applicationInfo : installedApplications) {
                                if (mContext.getPackageManager().getApplicationLabel(applicationInfo).toString().equals(appName)) {
                                    return new Action.IntentAction(mContext.getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName));
                                }
                            }
                            return null;
                        }
                    }).child(FILTER_ANY, new StateLeaf() {
                        @Override
                        Action handle(String[] command) {
                            return new Action.IntentAction(mContext.getPackageManager().getLaunchIntentForPackage(command[1]));
                        }
                    }));


    public Interpreter(Context context) {
        mContext = context;
    }

    public Action interpreterInner(String line) {
        String[] words = line.split(" ");
        return stateTree.match(words, 0).handle(words);
    }

    public Action interpreter(String line) {
        boolean independent = true;
        String[] actions = line.split("\\|");
        if (actions.length == 1) {
            actions = line.split("\\&");
            independent = false;
        }
        if (actions.length == 1)
            return interpreterInner(line);
        List<Action> list = new ArrayList<>(actions.length);
        for (String action : actions) {
            list.add(interpreterInner(action));
        }
        return new Action.MultiAction(list, independent);
    }


    public List<Action> interpreterAll(String str) {
        String[] lines = str.split("\n");
        List<Action> list = new ArrayList<>(lines.length);
        for (String line : lines) {
            list.add(interpreter(line));
        }
        return list;
    }

}
