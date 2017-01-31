
var toast = function(text){
    droid.toast(text);
}

var launchPackage = function(package){
    droid.launchPackage(package);
}

var launch = function(a, b){
    if(arguments.length == 2){
        droid.launch(a, b);
    }else{
        droid.launchPackage(a);
    }
}

var launchApp = function(appName){
    droid.launchApp(appName);
}


var text = function(str){
    return droid.text(str);
}

var bounds = function(left, top, right, bottom){
    return droid.bounds(left, top, right, bottom);
}


function performAction(action, args){
    if(args.length == 4){
        return action(bounds(args[0], args[1], args[2], args[3]));
    }else{
        return action(text(args[0]));
    }
}

var click = function(){
    return performAction(function(target){
        return droid.click(target);
    }, arguments);
}

var longClick = function(a, b, c, d){
    return performAction(function(target){
        return droid.longClick(target);
    }, arguments);
}

var scrollAllUp = function(){
    droid.scrollAllUp();
}

var scrollAllDown = function(){
    droid.scrollAllDown();
}

var scrollUp = function(a, b, c, d){
    if(arguments.length == 0)
        return scrollAllUp();
    return performAction(function(target){
        return droid.scrollUp(target);
    }, arguments);
}

var scrollDown = function(a, b, c, d){
     if(arguments.length == 0)
            return scrollAllDown();
       return performAction(function(target){
        return droid.scrollDown(target);
    }, arguments);
}

var select = function(a, b, c, d){
    return performAction(function(target){
        return droid.select(target);
    }, arguments);
}

var focus = function(a, b, c, d){
    return performAction(function(target){
        return droid.focus(target);
    }, arguments);
}

var paste = function(a, b, c, d){
    return performAction(function(target){
        return droid.paste(target);
    }, arguments);
}

var editable = function(i){
    return droid.editable(i);
}

var setText = function(target, str){
    return droid.setText(target, str);
}

var input = function(a, b){
    if(arguments.length == 1){
        return droid.setText(editable(-1), a);
    }else{
        return droid.setText(editable(a), b);
    }
}

var sleep = function(millis){
    droid.sleep(millis);
}


importClass("java.lang.Runnable");

var ui = function(action){
    droid.ui(new Runnable(action));
}