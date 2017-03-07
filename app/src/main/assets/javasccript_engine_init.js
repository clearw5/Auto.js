
if(__engine__ == "rhino"){
  __importClassOld__ = importClass;
  var importClass = function(pack){
    if(typeof(pack) == "string"){
        __importClassOld__(Packages[pack]);
    }else{
        __importClassOld__(pack);
    }
  }
}

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
    }else if(args.length == 2){
        return action(droid.text(args[0], args[1]));
    }else {
        return action(droid.text(args[0], -1));
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

var clickId = function(id){
    return droid.click(droid.id(id));
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
    return droid.shell(cmd, root);
}

var getTexts = function(){
    return droid.getTexts();
}

var getPackageName = function(){
    return droid.getPackageName();
}

var getActivityName = function(){
    return droid.getActivityName();
}

var setClip = function(text){
    droid.setClip(text);
}

var Tap = function(x, y){
    return shell("input tap " + x + " " + y, true).code == 1;
}

var Swipe = function(x1, y1, x2, y2){
    return shell("input swipe " + x1 + " " + y1 + " " + x2 + " " + y2, true).code == 1;
}

var KeyCode = function(keyCode){
    return shell("input keyevent " + keyCode, true).code == 1;
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
     return shell("input text " + text, true).code == 1;
}

/*
importClass("com.stardust.scriptdroid.service.AccessibilityDelegate");

var addAccessibilityDelegate = function(delegate){
    droid.addAccessibilityDelegate(delegate);
}
*/