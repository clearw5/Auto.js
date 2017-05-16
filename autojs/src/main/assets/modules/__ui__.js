module.exports = function(__runtime__, scope){
    var ui = Object.create(__runtime__.ui);
    ui.__id_cache__ = {};

    ui.layout = function(xml){
        view = ui.inflate(activity, xml.toString());
        ui.setContentView(view);
    }

    ui.setContentView = function(view){
        ui.view = view;
        ui.__id_cache__ = {};
        activity.setContentView(view);
    }

    ui.id = function(id){
        if(!ui.view)
            return null;
        var v = ui.view.getChildAt(0).id(id);
        if(v){
            v = decorate(v);
        }
        return v;
    }

    ui.run = function(action){
        activity.runOnUiThread(action);
    }

    ui.nonUi = function(action){
        ui.runOnNonUiThread(action);
    }

    ui.postDelay = function(action, delay){
        __runtime__.getUiHandler().postDelay(action, delay);
    }

    ui.statusBarColor = function(color){
        if(typeof(color) == 'string'){
            color = android.graphics.Color.parseColor(color);
        }
        if(Build.VERSION.SDK_INT >= 21){
            activity.getWindow().setStatusBarColor(color);
        }
    }

    ui.finish = function(){
        activity.finish();
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

    return new JavaAdapter(org.mozilla.javascript.NativeObject, {
         put: function(name, start, value) {
             ui[name] = value;
         },
         get: function(name, start) {
            if(!ui[name] && ui.view){
                var cacheView = ui.__id_cache__[name];
                if(cacheView){
                    return cacheView;
                }
                cacheView = ui.id(name);
                if(cacheView){
                    ui.__id_cache__[name] = cacheView;
                    return cacheView;
                }
            }
            return ui[name];
         }
     });
}
