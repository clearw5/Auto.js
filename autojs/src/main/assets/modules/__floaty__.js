
module.exports = function(__runtime__, scope){
    var floaty = {};

    floaty.window = function(layout){
        if(typeof(layout) == 'xml'){
            layout = layout.toString();
        }
        return wrap(__runtime__.floaty.window(layout));
    }

    floaty.__view_cache__ = {};

    function wrap(window){
        var proxyObject = new com.stardust.autojs.rhino.ProxyJavaObject(scope, window, window.getClass());
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

    return floaty;
}

