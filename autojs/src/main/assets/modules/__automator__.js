
module.exports = function(__runtime__, scope){
    var automator = {};

    function performAction(action, args){
        if(args.length == 4){
            return action(__runtime__.automator.bounds(args[0], args[1], args[2], args[3]));
        }else if(args.length == 2){
            return action(__runtime__.automator.text(args[0], args[1]));
        }else {
            return action(__runtime__.automator.text(args[0], -1));
        }
    }

    automator.click = function(){
        if(arguments.length == 2 && typeof(arguments[0]) == 'number' && typeof(arguments[1]) == 'number'){
            return __runtime__.automator.click(arguments[0], arguments[1]);
        }
        return performAction(function(target){
            return __runtime__.automator.click(target);
        }, arguments);
    }

    automator.longClick = function(a, b, c, d){
        if(arguments.length == 2 && typeof(arguments[0]) == 'number' && typeof(arguments[1]) == 'number'){
            return  __runtime__.automator.longClick(arguments[0], arguments[1]);
        }
        return performAction(function(target){
            return __runtime__.automator.longClick(target);
        }, arguments);
    }

     automator.press = __runtime__.automator.press.bind(__runtime__.automator);
     automator.gesture = __runtime__.automator.gesture.bind(__runtime__.automator, 0);
     automator.gestureAsync = __runtime__.automator.gestureAsync.bind(__runtime__.automator, 0);
     automator.swipe = __runtime__.automator.swipe.bind(__runtime__.automator);
     automator.gestures  = function(){
        return __runtime__.automator.gestures(toStrokes(arguments));
     }

     automator.gesturesAsync = function(){
         __runtime__.automator.gesturesAsync(toStrokes(arguments));
     }

     function toStrokes(args){
        var screenMetrics = __runtime__.getScreenMetrics();
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
            return __runtime__.automator.scrollMaxForward();
        if(arguments.length == 1 && typeof a === 'number')
            return __runtime__.automator.scrollForward(a);
        return performAction(function(target){
            return __runtime__.automator.scrollForward(target);
        }, arguments);
    }

    automator.scrollUp = function(a, b, c, d){
         if(arguments.length == 0)
            return __runtime__.automator.scrollMaxBackward();
         if(arguments.length == 1 && typeof a === 'number')
            return __runtime__.automator.scrollBackward(a);
          return performAction(function(target){
            return __runtime__.automator.scrollBackward(target);
        }, arguments);
    }

    automator.setText = function(a, b){
        if(arguments.length == 1){
            return __runtime__.automator.setText(__runtime__.automator.editable(-1), a);
        }else{
            return __runtime__.automator.setText(__runtime__.automator.editable(a), b);
        }
    }

    automator.input = function(a, b){
        if(arguments.length == 1){
            return __runtime__.automator.appendText(__runtime__.automator.editable(-1), a);
        }else{
            return __runtime__.automator.appendText(__runtime__.automator.editable(a), b);
        }
    }

    var modes = {
        "normal": 0,
        "fast": 1
    }

    scope.auto = function(mode){
        if(mode){
            if(typeof(mode) !== "string"){
                throw new TypeError("mode should be a string");
            }
            mode = modes[mode.toLowerCase()];
        }
        mode = mode || 0;
        __runtime__.accessibilityBridge.setMode(mode);
        __runtime__.accessibilityBridge.ensureServiceEnabled();
    }

    scope.__asGlobal__(__runtime__.automator, ['back', 'home', 'powerDialog', 'notifications', 'quickSettings', 'recents', 'splitScreen']);
    scope.__asGlobal__(automator, ['click', 'longClick', 'press', 'swipe', 'gesture', 'gestures', 'gestureAsync', 'gesturesAsync', 'scrollDown', 'scrollUp', 'input', 'setText']);

    return automator;
}


