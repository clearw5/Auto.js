
__runtime__.init();

__importClass__ = importClass;
var importClass = function(pack){
    if(typeof(pack) == "string"){
        __importClass__(Packages[pack]);
    }else{
        __importClass__(pack);
    }
}

var loadJar = function(path){
    __runtime__.loadJar(path);
}

__runtime__.bridges.setFunctionCaller(function(func, target, args){
   var arr = [];
   var len = args.length;
   for(var i = 0; i < len; i++){
      arr.push(args[i]);
   }
   return func.apply(target, arr);
});

var __that__ = this;

var __asGlobal__ = function(obj, functions){
    var len = functions.length;
    for(var i = 0; i < len; i++) {
        var funcName = functions[i];
        __that__[funcName] = obj[funcName].bind(obj);
    }
}

require("__general__")(__runtime__, this);


(function(scope){
    var modules = ['app', 'automator', 'console', 'dialogs', 'io', 'selector', 'shell', 'web', 'ui',
        "images", "timers", "events", "engines", "RootAutomator", "http"];
    var len = modules.length;
    for(var i = 0; i < len; i++) {
        var m = modules[i];
        scope[m] = require('__' + m + '__')(scope.__runtime__, scope);
    }
})(__that__);

__importClass__(android.view.KeyEvent);
__importClass__(com.stardust.autojs.runtime.api.Shell);
