
module.exports = function(__runtime__, scope){
    var floaty = {};

    floaty.window = function(layout){
        if(typeof(layout) == 'xml'){
            layout = layout.toString();
        }
        return wrap(__runtime__.floaty.window(layout));
    }

    function wrap(window){
        var proxyObject = new com.stardust.autojs.rhino.ProxyJavaObject(scope, window, window.getClass());
        proxyObject.__proxy__ = {
            set: function(name, value){
                window[name] = value;
            },
            get: function(name) {
               var value = window[name];
               if(typeof(value) == 'undefined'){
                   value = window.getView(name);
                   if(!value){
                      value = undefined;
                   }else{
                      value = scope.ui.__decorate__(value);

                   }
               }
               return value;
            }
        };
        return proxyObject;
    }

    return floaty;
}

