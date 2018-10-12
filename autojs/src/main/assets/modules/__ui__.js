module.exports = function (runtime, global) {

    require("object-observe-lite.min")();
    require("array-observe.min")();

    var J = util.java;
    var ui = {};

    ui.__defineGetter__("emitter", ()=>  activity ? activity.getEventEmitter() : null);

    ui.layout = function (xml) {
        if(!activity){
            throw new Error("需要在ui模式下运行才能使用该函数");
        }
        if(typeof(xml) == 'xml'){
            xml = xml.toXMLString();
        }
        runtime.ui.layoutInflater.setContext(activity);
        var view = runtime.ui.layoutInflater.inflate(xml, activity.window.decorView, false);
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
        ui.run(function () {
            activity.setContentView(view);
        });
    }

    ui.findById = function (id) {
        if (!ui.view)
            return null;
        return ui.findByStringId(ui.view, id);
    }

    ui.isUiThread = function () {
        let Looper = android.os.Looper;
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
        runtime.getUiHandler().postDelayed(wrapUiAction(action), delay);
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

    function wrapUiAction(action, defReturnValue) {
        if (typeof (activity) != 'undefined') {
            return function () { return action(); };
        }
        return function () {
            return __exitIfError__(action, defReturnValue);
        }
    }

    var proxy = runtime.ui;
    proxy.__proxy__ = {
        set: function (name, value) {
            ui[name] = value;
        },
        get: function (name) {
            if (!ui[name] && ui.view) {
                let v = ui.findById(name);
                if (v) {
                    return v;
                }
            }
            return ui[name];
        }
    };


    return proxy;
}