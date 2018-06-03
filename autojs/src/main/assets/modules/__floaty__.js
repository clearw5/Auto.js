
module.exports = function(runtime, global){
    var floaty = {};

    floaty.window = function(layout){
        if(typeof(layout) == 'xml'){
            layout = layout.toXMLString();
        }
        return wrap(runtime.floaty.window(layout));
    }

    floaty.rawWindow = function(layout){
        if(typeof(layout) == 'xml'){
            layout = layout.toXMLString();
        }
        return wrap(runtime.floaty.rawWindow(layout));
    }

    function wrap(window){
        var proxyObject = new com.stardust.autojs.rhino.ProxyJavaObject(global, window, window.getClass());
        var viewCache = {};
        proxyObject.__proxy__ = {
            set: function(name, value){
                window[name] = value;
            },
            get: function(name) {
               var value = window[name];
               if(typeof(value) == 'undefined'){
                   value = viewCache[name];
                   if(!value){
                        value = window.findView(name);
                        if(value){
                            value = ui.__decorate__(value);
                            viewCache[name] = value;
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

