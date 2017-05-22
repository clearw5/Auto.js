
if(__engine_name__ == "rhino"){
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
}

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
    var modules = ['app', 'automator', 'console', 'io', 'selector', 'shell', 'web', 'ui', "images"];
    var len = modules.length;
    for(var i = 0; i < len; i++) {
        var m = modules[i];
        scope[m] = require('__' + m + '__')(scope.__runtime__, scope);
    }
})(__that__);
