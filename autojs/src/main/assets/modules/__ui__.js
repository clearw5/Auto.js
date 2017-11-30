module.exports = function(__runtime__, scope){
    var ui = {};
    ui.__view_cache__ = {};

    ui.layout = function(xml){
        var view = __runtime__.ui.layoutInflater.inflate(activity, xml.toString());
        ui.setContentView(view);
    }

    ui.setContentView = function(view){
        ui.view = view;
        ui.__view_cache__ = {};
        ui.run(function(){
            activity.setContentView(view);
        });
    }

    ui.id = function(id){
        if(!ui.view)
            return null;
        var v = ui.findViewByStringId(ui.view.getChildAt(0), id);
        if(v){
            v = decorate(v);
        }
        return v;
    }

    ui.run = function(action){
        activity.runOnUiThread(action);
    }

    ui.nonUi = function(action){
        if(!ui.__executor__){
            ui.__executor__ = java.util.concurrent.Executors.newSingleThreadExecutor();
        }
        ui.__executor__.submit(action);
    }

    ui.postDelay = function(action, delay){
        __runtime__.getUiHandler().postDelay(action, delay);
    }

    ui.statusBarColor = function(color){
        if(typeof(color) == 'string'){
            color = android.graphics.Color.parseColor(color);
        }
        if(android.os.Build.VERSION.SDK_INT >= 21){
            ui.run(function(){
                activity.getWindow().setStatusBarColor(color);
            });
        }
    }

    ui.finish = function(){
        ui.run(function(){
            activity.finish();
        });
    }

    ui.findViewByStringId = function(view, id){
        return com.stardust.autojs.core.ui.JsViewHelper.findViewByStringId(view, id);
    }

    function decorate(view){
        var view = Object.create(view);
        view._id = function(id){
            return ui.findViewByStringId(view, id);
        }
        view.click = function(listener){
            if(listener){
                view.setOnClickListener(new android.view.View.OnClickListener(listener));
            }else{
                view.performClick();
            }
        }
        view.longClick = function(listener){
            if(listener){
                view.setOnLongClickListener(new android.view.View.OnLongClickListener(listener));
            }else{
                view.performLongClick();
            }
        }
        return view;
    }

    var proxy = __runtime__.ui;
    proxy.__proxy__ = {
        set: function(name, value){
            ui[name] = value;
        },
        get: function(name) {
           if(!ui[name] && ui.view){
               var cacheView = ui.__view_cache__[name];
               if(cacheView){
                   return cacheView;
               }
               cacheView = ui.id(name);
               if(cacheView){
                   ui.__view_cache__[name] = cacheView;
                   return cacheView;
               }
           }
           return ui[name];
        }
    };


    return proxy;
}