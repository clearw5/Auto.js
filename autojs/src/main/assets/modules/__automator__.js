
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
        return performAction(function(target){
            return __runtime__.automator.click(target);
        }, arguments);
    }

    automator.longClick = function(a, b, c, d){
        return performAction(function(target){
            return __runtime__.automator.longClick(target);
        }, arguments);
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

    automator.input = function(a, b){
        if(arguments.length == 1){
            return __runtime__.automator.setText(__runtime__.automator.editable(-1), a);
        }else{
            return __runtime__.automator.setText(__runtime__.automator.editable(a), b);
        }
    }

    scope.__asGlobal__(__runtime__.automator, ['back', 'home', 'powerDialog', 'notifications', 'quickSettings', 'recents', 'splitScreen']);
    scope.__asGlobal__(automator, ['click', 'longClick', 'scrollDown', 'scrollUp', 'input']);

    return automator;
}


