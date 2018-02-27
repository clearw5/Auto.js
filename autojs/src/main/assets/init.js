
__runtime__.init();

__importClass__ = importClass;
var importClass = function(pack){
    if(typeof(pack) == "string"){
        __importClass__(Packages[pack]);
    }else{
        __importClass__(pack);
    }
}

var __that__ = this;
var Promise = require('promise.js');
var JSON = require('__json2__.js');
var util = require('__util__.js');


__runtime__.bridges.setBridges({
    call: function(func, target, args){
       var arr = [];
       var len = args.length;
       for(var i = 0; i < len; i++){
          arr.push(args[i]);
       }
       return func.apply(target, arr);
    },
    toArray: function(o){
        var arr = [];
        for(var i = 0; i < o.size(); i++){
            arr.push(o.get(i));
        }
        for(var key in o){
            if(arr[key])
                continue;
            var v = o[key];
            if(typeof(v) == 'function'){
                arr[key] = v.bind(o);
            }else{
                arr[key] = v;
            }
        }
        return arr;
    },
    toString: function(o){
        return String(o);
    }
});

var device = __runtime__.device;

var __asGlobal__ = function(obj, functions){
    var len = functions.length;
    for(var i = 0; i < len; i++) {
        var funcName = functions[i];
        __that__[funcName] = obj[funcName].bind(obj);
    }
}

var __exitIfError__ = function(action, defReturnValue){
    try{
       return action();
    }catch(err){
        log(err.toString());
        if(err instanceof java.lang.Throwable){
            exit(err);
        }else if(err instanceof Error){
            exit(new org.mozilla.javascript.EvaluatorException(err.name + ": " + err.message, err.fileName, err.lineNumber));
            //new java.lang.RuntimeException(err.name + ": " + err.message + "\n" + err.stack));
        }else{
            exit();
        }
        return defReturnValue;
    }
};

require("__globals__")(__runtime__, this);


(function(scope){
    var modules = ['app', 'automator', 'console', 'dialogs', 'io', 'selector', 'shell', 'web', 'ui',
        "images", "timers", "threads", "events", "engines", "RootAutomator", "http", "storages", "floaty",
        "sensors"];
    var len = modules.length;
    for(var i = 0; i < len; i++) {
        var m = modules[i];
        scope[m] = require('__' + m + '__')(scope.__runtime__, scope);
    }
})(__that__);

__importClass__(android.view.KeyEvent);
__importClass__(com.stardust.autojs.core.util.Shell);

(function(){
    var __require__ = require;
    require = function(path){
        path = files.path(path);
        return __require__(path);
    };
})();

