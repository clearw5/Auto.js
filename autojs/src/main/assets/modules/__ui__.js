module.exports = function (runtime, global) {

    require("object-observe-lite.min")();
    require("array-observe.min")();

    var J = util.java;
    var ui = {};

    ui.__widgets__ = {};

    ui.__defineGetter__("emitter", ()=>  activity ? activity.getEventEmitter() : null);

    ui.layout = function (xml) {
        if(typeof(activity) == 'undefined'){
            throw new Error("需要在ui模式下运行才能使用该函数");
        }
        runtime.ui.layoutInflater.setContext(activity);
        var view = runtime.ui.layoutInflater.inflate(xml, activity.window.decorView, false);
        ui.setContentView(view);
    }

    ui.layoutFile = function(file) {
        ui.layout(files.read(file));
    }

    ui.inflate = function(xml, parent, attachToParent){
        if(typeof(xml) == 'xml'){
            xml = xml.toXMLString();
        }
        parent = parent || null;
        attachToParent = !!attachToParent;
        let ctx;
        if(typeof(activity) == 'undefined') {
            ctx = new android.view.ContextThemeWrapper(context, com.stardust.autojs.R.style.ScriptTheme);
        } else {
            ctx = activity;
        }
        runtime.ui.layoutInflater.setContext(ctx);
        return runtime.ui.layoutInflater.inflate(xml.toString(), parent, attachToParent);
    }

    ui.__inflate__ = function(ctx, xml, parent, attachToParent){
        if(typeof(xml) == 'xml'){
            xml = xml.toXMLString();
        }
        parent = parent || null;
        attachToParent = !!attachToParent;
        return runtime.ui.layoutInflater.inflate(ctx, xml.toString(), parent, attachToParent);
    }

    ui.registerWidget = function(name, widget){
        if(typeof(widget) !== 'function'){
            throw new TypeError('widget should be a class-like function');
        }
        ui.__widgets__[name] = widget;
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

    ui.findView = function(id) {
        return ui.findById(id);
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
        if(delay == undefined){
            runtime.getUiHandler().post(wrapUiAction(action));
        }else{
            runtime.getUiHandler().postDelayed(wrapUiAction(action), delay);
        }
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
        beforeConvertXml: function (context, xml) {
            return null;
        },
        afterConvertXml: function (context, xml) {
            return xml;
        },
        afterInflation: function (context, result, xml, parent) {
            return result;
        },
        beforeInflation: function (context, xml, parent) {
            return null;
        },
        beforeInflateView: function (context, node, parent, attachToParent) {
            return null;
        },
        afterInflateView: function (context, view, node, parent, attachToParent) {
            let widget = view.widget;
            if(widget && context.get("root") != widget){
                widget.notifyAfterInflation(view);
            }
            return view;
        },
        beforeCreateView: function (context, node, viewName, parent, attrs) {
            if(ui.__widgets__.hasOwnProperty(viewName)){
                let Widget = ui.__widgets__[viewName];
                let widget = new Widget();
                let ctx = layoutInflater.newInflateContext();
                ctx.put("root", widget);
                ctx.put("widget", widget);
                let view = ui.__inflate__(ctx, widget.renderInternal(), parent, false);
                return view;
            };
            return null;
        },
        afterCreateView: function (context, view, node, viewName, parent, attrs) {
            if (view.getClass().getName() == "com.stardust.autojs.core.ui.widget.JsListView" ||
                view.getClass().getName() == "com.stardust.autojs.core.ui.widget.JsGridView") {
                initListView(view);
            }
            var widget = context.get("widget");
            if(widget != null){
                widget.view = view;
                view.widget = widget;
                let viewAttrs = com.stardust.autojs.core.ui.ViewExtras.getViewAttributes(view, layoutInflater.resourceParser);
                viewAttrs.setViewAttributeDelegate({
                    has: function(name) {
                        return widget.hasAttr(name);
                    },
                    get: function(view, name, getter){
                        return widget.getAttr(view, name, getter);
                    },
                    set: function(view, name, value, setter) {
                        widget.setAttr(view, name, value, setter);
                    }
                });
                widget.notifyViewCreated(view);
            }
            return view;
        },
        beforeApplyAttributes: function (context, view, inflater, attrs, parent) {
            return false;
        },
        afterApplyAttributes: function (context, view, inflater, attrs, parent) {
            context.remove("widget");
        },
        beforeInflateChildren: function (context, inflater, node, parent) {
            return false;
        },
        afterInflateChildren: function (context, inflater, node, parent) {

        },
        afterApplyPendingAttributesOfChildren: function (context, inflater, view) {

        },
        beforeApplyPendingAttributesOfChildren: function (context, inflater, view) {
            return false;
        },
        beforeApplyAttribute: function (context, inflater, view, ns, attrName, value, parent, attrs) {
            var isDynamic = layoutInflater.isDynamicValue(value);
            if ((isDynamic && layoutInflater.getInflateFlags() == layoutInflater.FLAG_IGNORES_DYNAMIC_ATTRS)
                    || (!isDynamic && layoutInflater.getInflateFlags() == layoutInflater.FLAG_JUST_DYNAMIC_ATTRS)) {
                return true;
            }
            value = bind(value);
            let widget = context.get("widget");
            if(widget != null && widget.hasAttr(attrName)){
                widget.setAttr(view, attrName, value, (view, attrName, value)=>{
                    inflater.setAttr(view, ns, attrName, value, parent, attrs);
                });
            } else {
                inflater.setAttr(view, ns, attrName, value, parent, attrs);
            }
            this.afterApplyAttribute(context, inflater, view, ns, attrName, value, parent, attrs);
            return true;
        },
        afterApplyAttribute: function (context, inflater, view, ns, attrName, value, parent, attrs) {

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

    ui.Widget = (function(){
        function Widget(){
            this.__attrs__ = {};
        }
        Widget.prototype.renderInternal = function(){
            if(typeof(this.render) === 'function'){
                return this.render();
            }
            return (< />)
        };
        Widget.prototype.defineAttr = function(attrName, getter, setter){
            var attrAlias = attrName;
            var applier = null;
            if(typeof(arguments[1]) == 'string'){
                attrAlias = arguments[1];
                if(arguments.length >= 3){
                    applier = arguments[2];
                }
            } else if(typeof(arguments[1]) == 'function' && typeof(arguments[2]) != 'function'){
                applier = arguments[1];
            }
            if(!(typeof(arguments[1]) == 'function' && typeof(arguments[2]) == 'function')){
                getter = ()=> {
                    return this[attrAlias];
                };
                setter = (view, attrName, value, setter)=> {
                    this[attrAlias] = value;
                    applier && applier(view, attrName, value, setter);
                };
            }
            this.__attrs__[attrName] = {
                getter: getter,
                setter: setter
            };
        };
        Widget.prototype.hasAttr = function(attrName){
            return this.__attrs__.hasOwnProperty(attrName);
        };
        Widget.prototype.setAttr = function(view, attrName, value, setter){
            this.__attrs__[attrName].setter(view, attrName, value, setter);
        };
        Widget.prototype.getAttr = function(view, attrName, getter){
            return this.__attrs__[attrName].getter(view, attrName, getter);
        };
        Widget.prototype.notifyViewCreated = function(view){
            if(typeof(this.onViewCreated) == 'function'){
                this.onViewCreated(view);
            }
        };
        Widget.prototype.notifyAfterInflation = function(view){
            if(typeof(this.onFinishInflation) == 'function'){
                this.onFinishInflation(view);
            }
        }
        return Widget;
    })();

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