module.exports = function(__runtime__, scope){
    var ui = Object(__runtime__.ui);

    ui.layout = function(xml){
        view = ui.inflate(activity, xml);
        ui.setContentView(view);
    }

    ui.setContentView = function(view){
        ui.view = view;
        activity.setContentView(view);
    }

    ui.id = function(id){
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
            if(!ui[name]){
                var widget = ui.id(name);
                if(widget){
                    ui[name] = widget;
                    return widget;
                }
            }
            return ui[name];
         }
     });
}
