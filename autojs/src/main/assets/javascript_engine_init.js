
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

var toast = function(text){
    __runtime__.toast(text);
}

var app = require("app")(__runtime__);

var launchPackage = function(package){
    app.launchPackage(package);
}

var launch = app.launch.bind(app);

var launchApp = function(appName){
    app.launchApp(appName);
}

var getPackageName = function(appName){
    return app.getPackageName(appName);
}

var openAppSetting = function(packageName){
    return app.openAppSetting(packageName);
}



var sleep = function(millis){
    __runtime__.sleep(millis);
}

var isStopped = function(){
    return __runtime__.isStopped();
}

var notStopped = function(){
    return !isStopped();
}

var stop = function(){
    __runtime__.stop();
}

var console = __runtime__.console;

var log = function(str){
    console.log(str);
}

var print = log;

var err = function(e){
   console.e(e);
}

var openConsole = function(){
    console.show();
}

var clearConsole = function(){
    console.clear();
}

var shell = function(cmd, root){
    root = root ? 1 : 0;
    return __runtime__.shell(cmd, root);
}

var currentPackage = function(){
    return __runtime__.info.getLatestPackage();
}

var currentActivity = function(){
    return __runtime__.info.getLatestActivity();
}



var __this__ = this;

var back = function(){
    return __runtime__.automator.back();
}

var home = function(){
    return __runtime__.automator.home();
}

var powerDialog = function(){
    return __runtime__.automator.powerDialog();
}

var notifications = function(){
    return __runtime__.automator.notifications();
}

var quickSettings = function(){
    return __runtime__.automator.quickSettings();
}

var recents = function(){
    return __runtime__.automator.recents();
}

var splitScreen = function(){
    return __runtime__.automator.splitScreen();
}

function performAction(action, args){
    if(args.length == 4){
        return action(__runtime__.automator.bounds(args[0], args[1], args[2], args[3]));
    }else if(args.length == 2){
        return action(__runtime__.automator.text(args[0], args[1]));
    }else {
        return action(__runtime__.automator.text(args[0], -1));
    }
}

var click = function(){
    return performAction(function(target){
        return __runtime__.automator.click(target);
    }, arguments);
}

var longClick = function(a, b, c, d){
    return performAction(function(target){
        return __runtime__.automator.longClick(target);
    }, arguments);
}


var scrollDown = function(a, b, c, d){
    if(arguments.length == 0)
        return __runtime__.automator.scrollMaxForward();
    if(arguments.length == 1 && typeof a === 'number')
        return __runtime__.automator.scrollForward(a);
    return performAction(function(target){
        return __runtime__.automator.scrollForward(target);
    }, arguments);
}

var scrollUp = function(a, b, c, d){
     if(arguments.length == 0)
        return __runtime__.automator.scrollMaxBackward();
     if(arguments.length == 1 && typeof a === 'number')
        return __runtime__.automator.scrollBackward(a);
      return performAction(function(target){
        return __runtime__.automator.scrollBackward(target);
    }, arguments);
}

var input = function(a, b){
    if(arguments.length == 1){
        return __runtime__.automator.setText(__runtime__.automator.editable(-1), a);
    }else{
        return __runtime__.automator.setText(__runtime__.automator.editable(a), b);
    }
}


var setClip = function(text){
    __runtime__.setClip(text);
}


var SetScreenMetrics = function(w, h){
    __runtime__.SetScreenMetrics(w, h);
}


var Tap = function(x, y){
    __runtime__.shellExecAsync("input tap " + x + " " + y);
}

var Swipe = function(x1, y1, x2, y2, duration){
    if(arguments.length == 5){
        __runtime__.shellExecAsync("input swipe " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + duration);
    }else{
         __runtime__.shellExecAsync("input swipe " + x1 + " " + y1 + " " + x2 + " " + y2);
    }
}

var Screencap = function(path){
    __runtime__.shellExecAsync("screencap -p " + path);
}

var KeyCode = function(keyCode){
    __runtime__.shellExecAsync("input keyevent " + keyCode);
}

var Home = function(){
    return KeyCode(3);
}

var Back = function(){
    return KeyCode(4);
}

var Power = function(){
    return KeyCode(26);
}

var Up = function(){
    return KeyCode(19);
}

var Down = function(){
    return KeyCode(20);
}

var Left = function(){
    return KeyCode(21);
}

var Right = function(){
    return KeyCode(22);
}

var OK = function(){
    return KeyCode(23);
}

var VolumeUp = function(){
    return KeyCode(24);
}

var VolumeDown = function(){
    return KeyCode(25);
}

var Menu = function(){
    return KeyCode(1);
}

var Camera = function(){
    return KeyCode(27);
}

var Text = function(text){
     __runtime__.shellExecAsync("input text " + text);
}

var selector = function(){
    return __runtime__.selector(__engine__);
}

var __selector__ = selector();
var __obj__ = new java.lang.Object();

for(var x in __selector__){
    if(!__obj__[x] && !this[x]){
        this[x] = (function(method) {
            return function(){
                var s = selector();
                //这里不知道怎么写。尴尬。只能写成这样。
                if(arguments.length == 0){
                   return s[method]();
                }else if(arguments.length == 1){
                   return s[method](arguments[0]);
                }else if(arguments.length == 2){
                   return s[method](arguments[0], arguments[1]);
                }else if(arguments.length == 3){
                   return s[method](arguments[0], arguments[1], arguments[2]);
                }else if(arguments.length == 4){
                   return s[method](arguments[0], arguments[1], arguments[2], arguments[3]);
                }else{
                   return s[method].call(s, Array.prototype.slice.call(arguments));
                }
            };
        })(x);
    }

}

var open = function(path, mode, encoding, bufferSize){
    if(arguments.length == 1){
        return com.stardust.pio.PFile.open(path);
    }else if(arguments.length == 2){
        return com.stardust.pio.PFile.open(path, mode);
    }else if(arguments.length == 3){
        return com.stardust.pio.PFile.open(path, mode, encoding);
    }else if(arguments.length == 4){
        return com.stardust.pio.PFile.open(path, mode, encoding, bufferSize);
    }
}


var newInjectableWebClient = function(){
    return new com.stardust.autojs.runtime.api.InjectableWebClient(org.mozilla.javascript.Context.getCurrentContext(), __this__);
}

var newInjectableWebView = function(activity){
    return new com.stardust.autojs.runtime.api.InjectableWebView(activity, org.mozilla.javascript.Context.getCurrentContext(), __this__);
}
