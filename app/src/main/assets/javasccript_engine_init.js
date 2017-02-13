
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

function performAction(action, args){
    if(args.length == 4){
        return action(droid.bounds(args[0], args[1], args[2], args[3]));
    }else{
        return action(droid.text(args[0]));
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

var scrollUp = function(a, b, c, d){
    if(arguments.length == 0)
        return droid.scrollAllUp();
    if(arguments.length == 1 && typeof a === 'number')
        return droid.scrollUp(a);
    return performAction(function(target){
        return droid.scrollUp(target);
    }, arguments);
}

var scrollDown = function(a, b, c, d){
     if(arguments.length == 0)
        return droid.scrollAllDown();
     if(arguments.length == 1 && typeof a === 'number')
        return droid.scrollDown(a);
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

var isStopped = function(){
    return droid.isStopped();
}

var notStopped = function(){
    return !isStopped();
}

var log = function(str){
    droid.log(str);
}

var err = function(e){
   droid.err(e);
}

var openConsole = function(){
    droid.console();
}

var clearConsole = function(){
    droid.clearConsole();
}

var shell = function(cmd, root){
    root = root ? 1 : 0;
    droid.shell(cmd, root);
}