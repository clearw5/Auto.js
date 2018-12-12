var global = this;

runtime.init();

(function () {
    //重定向importClass使得其支持字符串参数
    global.importClass =
        (function () {
            var __importClass__ = importClass;
            return function (pack) {
                if (typeof (pack) == "string") {
                    __importClass__(Packages[pack]);
                } else {
                    __importClass__(pack);
                }
            }
        })();

    //内部函数
    global.__asGlobal__ = function (obj, functions) {
        var len = functions.length;
        for (var i = 0; i < len; i++) {
            var funcName = functions[i];
            var func = obj[funcName]
            if (!func) {
                continue;
            }
            (function (obj, funcName, func) {
                global[funcName] = function () {
                    return func.apply(obj, arguments);
                };
            })(obj, funcName, func);
        }
    }

    global.__exitIfError__ = function (action, defReturnValue) {
        try {
            return action();
        } catch (err) {
            if (err instanceof java.lang.Throwable) {
                exit(err);
            } else if (err instanceof Error) {
                exit(new org.mozilla.javascript.EvaluatorException(err.name + ": " + err.message, err.fileName, err.lineNumber));
            } else {
                exit();
            }
            return defReturnValue;
        }
    };

     // 初始化基础模块
     global.timers = require('__timers__.js')(runtime, global);

     //初始化不依赖环境的模块
     global.JSON = require('__json2__.js');
     global.util = require('__util__.js');
     global.device = runtime.device;
     global.Promise = require('promise.js');
 
     //设置JavaScriptBridges用于与Java层的交互和数据转换
     runtime.bridges.setBridges(require('__bridges__.js'));

    //初始化全局函数
    require("__globals__")(runtime, global);
    //初始化一般模块
    (function (scope) {
        var modules = ['app', 'automator', 'console', 'dialogs', 'io', 'selector', 'shell', 'web', 'ui',
            "images", "threads", "events", "engines", "RootAutomator", "http", "storages", "floaty",
            "sensors", "media", "plugins", "continuation"];
        var len = modules.length;
        for (var i = 0; i < len; i++) {
            var m = modules[i];
            scope[m] = require('__' + m + '__')(scope.runtime, scope);
        }
    })(global);

    importClass(android.view.KeyEvent);
    importClass(com.stardust.autojs.core.util.Shell);
    importClass(android.graphics.Paint);
    Canvas = com.stardust.autojs.core.graphics.ScriptCanvas;
    Image = com.stardust.autojs.core.image.ImageWrapper;

    //重定向require以便支持相对路径和npm模块
    Module = require("jvm-npm.js");
    require = Module.require;


})();


