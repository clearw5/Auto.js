
module.exports = function(runtime, global){
    var floaty = {};

    floaty.window = function(layout){
        if(typeof(layout) == 'xml'){
            layout = layout.toString();
        }
        return wrap(runtime.floaty.window(layout));
    }

    floaty.__view_cache__ = {};

    function wrap(window){
        var proxyObject = new com.stardust.autojs.rhino.ProxyJavaObject(global, window, window.getClass());
        proxyObject.__proxy__ = {
            set: function(name, value){
                window[name] = value;
            },
            get: function(name) {
               var value = window[name];
               if(typeof(value) == 'undefined'){
                   value = floaty.__view_cache__[name];
                   if(!value){
                        value = window.getView(name);
                        if(value){
                            value = ui.__decorate__(value);
                            floaty.__view_cache__[name] = value;
                        }
                   }
                   if(!value){
                      value = undefined;
                   }
               }
               return value;
            }
        };
        return proxyObject;
    }
    
    floaty.closeAll = runtime.floaty.closeAll.bind(runtime.floaty);

    return floaty;
}

