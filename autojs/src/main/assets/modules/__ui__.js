module.exports = function (runtime, global) {

    require("object-observe-lite.min")();
    require("array-observe.min")();

    var J = require("__java_util__");


    var ui = {};
    ui.__view_cache__ = {};

    ui.__defineGetter__("emitter", ()=>  activity ? activity.getEventEmitter() : null);

    ui.layout = function (xml) {
        if(!activity){
            throw new Error("需要在ui模式下运行才能使用该函数");
        }
        if(typeof(xml) == 'xml'){
            xml = xml.toXMLString();
        }
        runtime.ui.layoutInflater.setContext(activity);
        var view = runtime.ui.layoutInflater.inflate(xml);
        ui.setContentView(view);
    }

    ui.inflate = function(xml, parent){
        if(!activity){
            throw new Error("需要在ui模式下运行才能使用该函数");
        }
        parent = parent || null;
        runtime.ui.layoutInflater.setContext(activity);
        return decorate(runtime.ui.layoutInflater.inflate(xml.toString(), parent));
    }

    ui.setContentView = function (view) {
        ui.view = view;
        ui.__view_cache__ = {};
        ui.run(function () {
            activity.setContentView(view);
        });
    }

    ui.findById = function (id) {
        if (!ui.view)
            return null;
        var v = ui.findByStringId(ui.view, id);
        if (v) {
            v = decorate(v);
        }
        return v;
    }

    ui.isUiThread = function () {
        importClass(android.os.Looper);
        return Looper.myLooper() == Looper.getMainLooper();
    }

    ui.run = function (action) {
        if (ui.isUiThread()) {
            return action();
        }
        var err = null;
        var result;
        var disposable = global.threads.disposable();
        runtime.uiHandler.post(function () {
            try {
                result = action();
                disposable.setAndNotify(true);
            } catch (e) {
                err = e;
                disposable.setAndNotify(true);
            }
        });
        disposable.blockedGet();
        if (err) {
            throw err;
        }
        return result;
    }

    ui.post = function (action, delay) {
        delay = delay || 0;
        runtime.getUiHandler().postDelay(wrapUiAction(action), delay);
    }

    ui.statusBarColor = function (color) {
        if (typeof (color) == 'string') {
            color = android.graphics.Color.parseColor(color);
        }
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            ui.run(function () {
                activity.getWindow().setStatusBarColor(color);
            });
        }
    }

    ui.finish = function () {
        ui.run(function () {
            activity.finish();
        });
    }

    ui.findByStringId = function (view, id) {
        return com.stardust.autojs.core.ui.JsViewHelper.findViewByStringId(view, id);
    }

    runtime.ui.bindingContext = global;
    var layoutInflater = runtime.ui.layoutInflater;
    layoutInflater.setLayoutInflaterDelegate({
        beforeConvertXml: function (xml) {
            return null;
        },
        afterConvertXml: function (xml) {
            return xml;
        },
        afterInflation: function (result, xml, parent) {
            return result;
        },
        beforeInflation: function (xml, parent) {
            return null;
        },
        beforeInflateView: function (node, parent, attachToParent) {
            return null;
        },
        afterInflateView: function (view, node, parent, attachToParent) {
            return view;
        },
        beforeCreateView: function (node, viewName, attrs) {
            return null;
        },
        afterCreateView: function (view, node, viewName, attrs) {
            if (view.getClass().getName() == "com.stardust.autojs.core.ui.widget.JsListView" ||
                view.getClass().getName() == "com.stardust.autojs.core.ui.widget.JsGridView") {
                initListView(view);
            }
            return view;
        },
        beforeApplyAttributes: function (view, inflater, attrs, parent) {
            return false;
        },
        afterApplyAttributes: function (view, inflater, attrs, parent) {

        },
        beforeInflateChildren: function (inflater, node, parent) {
            return false;
        },
        afterInflateChildren: function (inflater, node, parent) {

        },
        afterApplyPendingAttributesOfChildren: function (inflater, view) {

        },
        beforeApplyPendingAttributesOfChildren: function (inflater, view) {
            return false;
        },
        beforeApplyAttribute: function (inflater, view, ns, attrName, value, parent, attrs) {
            var isDynamic = layoutInflater.isDynamicValue(value);
            if ((isDynamic && layoutInflater.getInflateFlags() == layoutInflater.FLAG_IGNORES_DYNAMIC_ATTRS)
                    || (!isDynamic && layoutInflater.getInflateFlags() == layoutInflater.FLAG_JUST_DYNAMIC_ATTRS)) {
                return true;
            }
            value = bind(value);
            inflater.setAttr(view, ns, attrName, value, parent, attrs);
            this.afterApplyAttribute(inflater, view, ns, attrName, value, parent, attrs);
            return true;
        },
        afterApplyAttribute: function (inflater, view, ns, attrName, value, parent, attrs) {

        }
    });

    function bind(value) {
        var ctx = runtime.ui.bindingContext;
        if (ctx == null)
            return;
        var i = -1;
        while ((i = value.indexOf("{{", i + 1)) >= 0) {
            var j = value.indexOf("}}", i + 1);
            if (j < 0)
                return value;
            value = value.substring(0, i) + evalInContext(value.substring(i + 2, j), ctx) + value.substring(j + 2);
            i = j + 1;
        }
        return value;
    }

    function evalInContext(expr, ctx) {
        return global.__exitIfError__(function() {
            with(ctx) {
                return (function(){
                   return eval(expr);
                }).call(ctx);
            }
        });

    }

    function decorate(view) {
        var javaObject = view;
        var view = global.events.__asEmitter__(Object.create(view));
        view.__javaObject__ = javaObject;
        if (view.getClass().getName() == "com.stardust.autojs.core.ui.widget.JsListView"
            || view.getClass().getName() == "com.stardust.autojs.core.ui.widget.JsGridView") {
            view = decorateList(view);
        }
        var gestureDetector = new android.view.GestureDetector(context, {
            onDown: function (e) {
                e = wrapMotionEvent(e);
                emit("touch_down", e, view);
                return e.consumed;
            },
            onShowPress: function (e) {
                e = wrapMotionEvent(e);
                emit("show_press", e, view);
            },
            onSingleTapUp: function (e) {
                e = wrapMotionEvent(e);
                emit("single_tap", e, view);
                return e.consumed;
            },
            onScroll: function (e1, e2, distanceX, distanceY) {
                e1 = wrapMotionEvent(e1);
                e2 = wrapMotionEvent(e2);
                emit("scroll", e1, e2, distanceX, distanceY, view);
                return e1.consumed || e2.consumed;
            },
            onLongPress: function (e) {
                e = wrapMotionEvent(e);
                emit("long_press", e, view);
            },
            onFling: function (e1, e2, velocityX, velocityY) {
                e1 = wrapMotionEvent(e1);
                e2 = wrapMotionEvent(e2);
                emit("fling", e1, e2, velocityX, velocityY, view);
                return e1.consumed || e2.consumed;
            }
        });
        view.setOnTouchListener(function (v, event) {
            if (gestureDetector.onTouchEvent(event)) {
                return true;
            }
            event = wrapMotionEvent(event);
            event.consumed = false;
            emit("touch", event, view);
            return event.consumed;
        });
        if(!J.instanceOf(view, "android.widget.AdapterView")){
            view.setOnLongClickListener(function (v) {
                var event = {};
                event.consumed = false;
                emit("long_click", event, view);
                return event.consumed;
            });
            view.setOnClickListener(function (v) {
                emit("click", view);
            });
        }

        view.setOnKeyListener(function (v, keyCode, event) {
            event = wrapMotionEvent(event);
            emit("key", keyCode, event, v);
            return event.consumed;
        });
        if (typeof (view.setOnCheckedChangeListener) == 'function') {
            view.setOnCheckedChangeListener(function (v, isChecked) {
                emit("check", isChecked == true ? true : false, view);
            });
        }
        view._id = function (id) {
            return ui.findById(view, id);
        }
        view.click = function (listener) {
            if (listener) {
                view.setOnClickListener(new android.view.View.OnClickListener(wrapUiAction(listener)));
            } else {
                view.performClick();
            }
        }
        view.longClick = function (listener) {
            if (listener) {
                view.setOnLongClickListener(wrapUiAction(listener, false));
            } else {
                view.performLongClick();
            }
        }
        function emit() {
            var args = arguments;
            global.__exitIfError__(function () {
                //不支持使用apply的原因是rhino会把参数中的primitive变成object
                functionApply(view, view.emit, args);
                //view.emit.apply(view, args);
            });
        }
        return view;
    }



    function initListView(list) {
        list.setDataSourceAdapter({
            getItemCount: function (data) {
                return data.length;
            },
            getItem: function (data, i) {
                return data[i];
            },
            setDataSource: function (data) {
                var adapter = list.getAdapter();
                Array.observe(data, function (changes) {
                    changes.forEach(change => {
                        if (change.type == 'splice') {
                            if (change.removed && change.removed.length > 0) {
                                adapter.notifyItemRangeRemoved(change.index, change.removed.length);
                            }
                            if (change.addedCount > 0) {
                                adapter.notifyItemRangeInserted(change.index, change.addedCount);
                            }
                        } else if (change.type == 'update') {
                            try{
                                adapter.notifyItemChanged(parseInt(change.name));
                            }catch(e){}
                        }
                    });
                });
            }
        });
    }

    function decorateList(list) {
        list.setOnItemTouchListener({
            onItemClick: function(listView, itemView, item, pos){
                emit("item_click", item, pos, itemView, listView);
            },
            onItemLongClick: function(listView, itemView, item, pos){
                var event = {};
                event.consumed = false;
                emit("item_long_click", event, item, pos, itemView, listView);
                return event.consumed;
            }
        });
        function emit() {
            var args = arguments;
            global.__exitIfError__(function () {
                //不支持使用apply的原因是rhino会把参数中的primitive变成object
                functionApply(list, list.emit, args);
                //view.emit.apply(view, args);
            });
        }
        return list;
    }

    ui.__decorate__ = decorate;

    function wrapUiAction(action, defReturnValue) {
        if (typeof (activity) != 'undefined') {
            return function () { return action(); };
        }
        return function () {
            return __exitIfError__(action, defReturnValue);
        }
    }

    function wrapMotionEvent(e) {
        e = Object.create(e);
        e.consumed = false;
        return e;
    }

    function functionApply(obj, func, args) {
        if (args.length == 0)
            return func.call(obj);
        if (args.length == 1)
            return func.call(obj, args[0]);
        if (args.length == 2)
            return func.call(obj, args[0], args[1]);
        if (args.length == 3)
            return func.call(obj, args[0], args[1], args[2]);
        if (args.length == 4)
            return func.call(obj, args[0], args[1], args[2], args[3]);
        if (args.length == 5)
            return func.call(obj, args[0], args[1], args[2], args[3], args[4]);
        if (args.length == 6)
            return func.call(obj, args[0], args[1], args[2], args[3], args[4], args[5]);
        throw new Error("too many arguments: " + args.length);
    }

    var proxy = runtime.ui;
    proxy.__proxy__ = {
        set: function (name, value) {
            ui[name] = value;
        },
        get: function (name) {
            if (!ui[name] && ui.view) {
                var cache = ui.__view_cache__[name];
                if (cache) {
                    return cache;
                }
                cache = ui.findById(name);
                if (cache) {
                    ui.__view_cache__[name] = cache;
                     return cache;
                }
            }
            return ui[name];
        }
    };


    return proxy;
}