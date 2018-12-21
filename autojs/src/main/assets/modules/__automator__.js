
module.exports = function(runtime, global){
    var automator = {};

    function performAction(action, args){
        if(args.length == 4){
            return action(runtime.automator.bounds(args[0], args[1], args[2], args[3]));
        }else if(args.length == 2){
            return action(runtime.automator.text(args[0], args[1]));
        }else {
            return action(runtime.automator.text(args[0], -1));
        }
    }

    automator.click = function(){
        if(arguments.length == 2 && typeof(arguments[0]) == 'number' && typeof(arguments[1]) == 'number'){
            return runtime.automator.click(arguments[0], arguments[1]);
        }
        return performAction(function(target){
            return runtime.automator.click(target);
        }, arguments);
    }

    automator.longClick = function(a, b, c, d){
        if(arguments.length == 2 && typeof(arguments[0]) == 'number' && typeof(arguments[1]) == 'number'){
            return  runtime.automator.longClick(arguments[0], arguments[1]);
        }
        return performAction(function(target){
            return runtime.automator.longClick(target);
        }, arguments);
    }

     automator.press = runtime.automator.press.bind(runtime.automator);
     automator.gesture = runtime.automator.gesture.bind(runtime.automator, 0);
     automator.gestureAsync = runtime.automator.gestureAsync.bind(runtime.automator, 0);
     automator.swipe = runtime.automator.swipe.bind(runtime.automator);
     automator.gestures  = function(){
        return runtime.automator.gestures(toStrokes(arguments));
     }

     automator.gesturesAsync = function(){
         runtime.automator.gesturesAsync(toStrokes(arguments));
     }

     function toStrokes(args){
        var screenMetrics = runtime.getScreenMetrics();
        var len = args.length;
        var strokes = java.lang.reflect.Array.newInstance(android.accessibilityservice.GestureDescription.StrokeDescription, len);
        for(var i = 0; i < len; i++){
            var gesture = args[i];
            var pointsIndex = 1;
            if(typeof(gesture[1]) == 'number'){
                var start = gesture[0];
                var delay = gesture[1];
                pointsIndex = 2;
            }else{
                var start = 0;
                var delay = gesture[0];
            }
            var gestureLen = gesture.length;
            var path = new android.graphics.Path();
            path.moveTo(screenMetrics.scaleX(gesture[pointsIndex][0]), screenMetrics.scaleY(gesture[pointsIndex][1]));
            for(var j = pointsIndex + 1; j < gestureLen; j++){
                path.lineTo(screenMetrics.scaleX(gesture[j][0]), screenMetrics.scaleY(gesture[j][1]));
            }
            strokes[i] = new android.accessibilityservice.GestureDescription.StrokeDescription(path, start, delay);
        }
        return strokes;
     }

    automator.scrollDown = function(a, b, c, d){
        if(arguments.length == 0)
            return runtime.automator.scrollMaxForward();
        if(arguments.length == 1 && typeof a === 'number')
            return runtime.automator.scrollForward(a);
        return performAction(function(target){
            return runtime.automator.scrollForward(target);
        }, arguments);
    }

    automator.scrollUp = function(a, b, c, d){
         if(arguments.length == 0)
            return runtime.automator.scrollMaxBackward();
         if(arguments.length == 1 && typeof a === 'number')
            return runtime.automator.scrollBackward(a);
          return performAction(function(target){
            return runtime.automator.scrollBackward(target);
        }, arguments);
    }

    automator.setText = function(a, b){
        if(arguments.length == 1){
            return runtime.automator.setText(runtime.automator.editable(-1), a);
        }else{
            return runtime.automator.setText(runtime.automator.editable(a), b);
        }
    }

    automator.input = function(a, b){
        if(arguments.length == 1){
            return runtime.automator.appendText(runtime.automator.editable(-1), a);
        }else{
            return runtime.automator.appendText(runtime.automator.editable(a), b);
        }
    }

    var modes = {
        "normal": 0,
        "fast": 1
    }

    const flagsMap = {
        "findOnUiThread": 1,
        "useUsageStats": 2,
        "useShell": 4
    };

    var auto = function(mode){
        if(mode){
            global.auto.setMode(mode);
        }
        runtime.accessibilityBridge.ensureServiceEnabled();
    }

    auto.waitFor = function(){
        runtime.accessibilityBridge.waitForServiceEnabled();
    }

    auto.setMode = function(modeStr){
        if(typeof(modeStr) !== "string"){
            throw new TypeError("mode should be a string");
        }
        let mode = modes[modeStr];
        if(mode == undefined){
            throw new Error("unknown mode for auto.setMode(): " + modeStr)
        }
        runtime.accessibilityBridge.setMode(mode);
    }

    auto.setFlags = function(flags){
        let flagStrings;
        if(Array.isArray(flags)){
            flagStrings = flags;
        } else if(typeof(flags) == "string"){
            flagStrings = [flags];
        } else {
            throw new TypeError("flags = " + flags);
        }
        let flagsInt = 0;
        for(let i = 0; i < flagStrings.length; i++){
            let flag = flagsMap[flagStrings[i]];
            if(flag == undefined){
                throw new Error("unknown flag for auto.setFlags(): " + flagStrings[i]);
            }
            flagsInt |= flag;
        }
        runtime.accessibilityBridge.setFlags(flagsInt);
    }

    auto.__defineGetter__("service", function() {
        return runtime.accessibilityBridge.getService();
    });

    auto.__defineGetter__("windows", function() {
        var service = auto.service;
        return service == null ? [] : util.java.toJsArray(service.getWindows(), true);
    });

    auto.__defineGetter__("root", function() {
        var root = runtime.accessibilityBridge.getRootInCurrentWindow();
        if(root == null){
            return null;
        }
        return com.stardust.automator.UiObject.Companion.createRoot(root);
    });

    auto.__defineGetter__("rootInActiveWindow", function() {
        var root = runtime.accessibilityBridge.getRootInActiveWindow();
        if(root == null){
            return null;
        }
        return com.stardust.automator.UiObject.Companion.createRoot(root);
    });

    auto.__defineGetter__("windowRoots", function() {
        return util.java.toJsArray(runtime.accessibilityBridge.windowRoots(), false)
            .map(root => com.stardust.automator.UiObject.Companion.createRoot(root));
    });


    auto.setWindowFilter = function(filter){
        runtime.accessibilityBridge.setWindowFilter(new com.stardust.autojs.core.accessibility.AccessibilityBridge.WindowFilter(filter));
    }

    global.auto = auto;

    global.__asGlobal__(runtime.automator, ['back', 'home', 'powerDialog', 'notifications', 'quickSettings', 'recents', 'splitScreen']);
    global.__asGlobal__(automator, ['click', 'longClick', 'press', 'swipe', 'gesture', 'gestures', 'gestureAsync', 'gesturesAsync', 'scrollDown', 'scrollUp', 'input', 'setText']);

    return automator;
}


