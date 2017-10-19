function autoJsGlobalScope(){
    var globals = {
      "modules": [
        "app",
        "automator",
        "console",
        "dialogs",
        "events",
        "images",
        "files",
        "timers",
        "ui",
      ],
      "app": [
        "launchPackage",
        "launch",
        "launchApp",
        "getPackageName",
        "openAppSetting"
      ],
      "automator": [
        "click",
        "longClick",
        "press",
        "swipe",
        "gesture",
        "gestures",
        "gestureAsync",
        "gesturesAsync",
        "scrollDown",
        "scrollUp",
        "input",
        "setText"
      ],
      "console": [
        "print",
        "log",
        "err",
        "openConsole",
        "clearConsole"
      ],
      "dialogs": [
        "rawInput",
        "input",
        "alert",
        "confirm",
        "prompt"
      ],
      "images": [
        "requestScreenCapture",
        "captureScreen",
        "findColor",
        "findColorInRegion",
        "findColorEquals"
      ],
      "files": [
        "open"
      ],
      "selector": [
        "id",
        "idContains",
        "idStartsWith",
        "idEndsWith",
        "idMatches",
        "text",
        "textContains",
        "textStartsWith",
        "textEndsWith",
        "textMatches",
        "desc",
        "descContains",
        "descStartsWith",
        "descEndsWith",
        "descMatches",
        "className",
        "classNameContains",
        "classNameStartsWith",
        "classNameEndsWith",
        "classNameMatches",
        "packageName",
        "packageNameContains",
        "packageNameStartsWith",
        "packageNameEndsWith",
        "packageNameMatches",
        "bounds",
        "boundsInside",
        "boundsContains",
        "drawingOrder",
        "checkable",
        "checked",
        "focusable",
        "focused",
        "visibleToUser",
        "accessibilityFocused",
        "selected",
        "clickable",
        "longClickable",
        "enabled",
        "password",
        "scrollable",
        "editable",
        "contentInvalid",
        "contextClickable",
        "multiLine",
        "dismissable",
        "checkable",
        "checked",
        "focusable",
        "focused",
        "visibleToUser",
        "accessibilityFocused",
        "selected",
        "clickable",
        "longClickable",
        "enabled",
        "password",
        "scrollable",
        "editable",
        "contentInvalid",
        "contextClickable",
        "multiLine",
        "dismissable"
      ],
      "shell": [
        "SetScreenMetrics",
        "Tap",
        "Swipe",
        "Screencap",
        "KeyCode",
        "Home",
        "Back",
        "Power",
        "Up",
        "Down",
        "Left",
        "Right",
        "OK",
        "VolumeUp",
        "VolumeDown",
        "Menu",
        "Camera",
        "Text"
      ],
      "timers": [
        "loop",
        "setTimeout",
        "clearTimeout",
        "setInterval",
        "clearInterval",
        "setImmediate",
        "clearImmediate"
      ],
      "web": [
        "newInjectableWebClient",
        "newInjectableWebView"
      ],
      "general": [
        "toast",
        "toastLog",
        "sleep",
        "isStopped",
        "notStopped",
        "exit",
        "setClip",
        "getClip",
        "currentPackage",
        "currentActivity",
        "waitForActivity",
        "waitForPackage",
        "setScreenMetrics"
      ],
      "variables": [
        "context",
        "activity"
      ]
    };
    var modules = {
      "app": [
        "uninstall",
        "viewFile",
        "editFile",
        "openUrl",
        "launchPackage",
        "launch",
        "launchApp",
        "getPackageName",
        "openAppSetting"
      ],
      "automator": [
        "click",
        "longClick",
        "press",
        "swipe",
        "gesture",
        "gestures",
        "gestureAsync",
        "gesturesAsync",
        "scrollDown",
        "scrollUp",
        "input",
        "setText"
      ],
      "console": [
        "show",
        "hide",
        "clear",
        "verbose",
        "print",
        "info",
        "log",
        "warn",
        "error",
        "assert"
      ],
      "dialogs": [
        "select",
        "singleChoice",
        "multiChoice",
        "rawInput",
        "input",
        "alert",
        "confirm",
        "prompt"
      ],
      "images": [
        "saveImage",
        "pixel",
        "read",
        "requestScreenCapture",
        "captureScreen",
        "findColor",
        "findColorInRegion",
        "findColorEquals"
      ],
      "colors": [
        "red",
        "green",
        "blue",
        "alpha",
        "toString",
        "rgb",
        "argb"
      ],
      "events": [
        "emitter",
        "observeKey",
        "observeTouch",
        "observeNotification",
        "onKeyDown",
        "onKeyUp",
        "onceKeyDown",
        "onceKeyUp",
        "onToast",
        "onNotification",
        "removeAllKeyDownListeners",
        "removeAllKeyUpListeners",
        "onTouch",
        "removeAllTouchListeners",
        "getTouchEventTimeout",
        "setTouchEventTimeout",
        "on",
        "once",
        "emit",
        "getListeners",
        "addListener",
        "eventNames",
        "listenerCount",
        "listeners",
        "prependListener",
        "prependOnceListener",
        "removeAllListeners",
        "removeAllListeners",
        "removeListener",
        "setMaxListeners",
        "getMaxListeners",
        "defaultMaxListeners"
      ],
      "files": [
        "open",
        "isFile",
        "isDir",
        "isEmptyDir",
        "join",
        "create",
        "createIfNotExists",
        "exists",
        "ensureDir",
        "read",
        "write",
        "copy",
        "rename",
        "renameWithoutExtension",
        "getName",
        "getExtension",
        "remove",
        "removeDir",
        "getSdcardPath",
        "listDir"
      ],
      "timers": [
        "loop",
        "setTimeout",
        "clearTimeout",
        "setInterval",
        "clearInterval",
        "setImmediate",
        "clearImmediate"
      ]
    };
    function for_each(obj, func){
        for(var key in obj){
            if(!obj.hasOwnProperty(key)){
                continue;
            }
            func(key, obj[key]);
        }
    }
    var __global__ = {};
    for_each(modules, function(moduleName, moduleVariables){
        if(!__global__[moduleName]){
            __global__[moduleName] = {};
        }
        for(var i = 0; i < moduleVariables.length; i++){
            if(!__global__[moduleName][moduleVariables[i]]){
                __global__[moduleName][moduleVariables[i]] = '';
            }
        }
    });
    for_each(globals, function(moduleName, moduleVariables){
        for(var i = 0; i < moduleVariables.length; i++){
            if(!__global__[moduleVariables[i]]){
                __global__[moduleVariables[i]] = '';
            }
        }
    })
    return __global__;
  }

