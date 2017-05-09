__importClass__(com.stardust.scriptdroid.autojs.api.Shell);


var dialogs =  {};

dialogs.rawInput = function(title, prefill){
   prefill = prefill || "";
   return __runtime__.dialogs.rawInput(title, prefill);
};

dialogs.input = function(title, prefill){
    return eval(dialogs.rawInput(title, prefill) + "");
}

dialogs.prompt = dialogs.rawInput;

dialogs.alert = function(title, prefill){
    prefill = prefill || "";
    return __runtime__.dialogs.alert(title, prefill);
}

dialogs.confirm = function(title, prefill){
    prefill = prefill || "";
    return __runtime__.dialogs.confirm(title, prefill);
}

dialogs.select = function(title, items){
    return __runtime__.dialogs.select(title, [].slice.call(arguments, 1));
}

dialogs.singleChoice = function(title, items, index){
    index = index || 0;
    return __runtime__.dialogs.singleChoice(title, index, items);
}

dialogs.multiChoice = function(title, items, index){
    index = index || [];
    var javaArray = __runtime__.dialogs.multiChoice(title, index, items);
    var jsArray = [];
    var len = javaArray.length;
    for (var i = 0;i < len;i++){
        jsArray.push(javaArray[i]);
    }
    return jsArray;
}

var rawInput = function(title, prefill){
    return dialogs.rawInput(title, prefill);
}

var alert = function(title, prefill){
    dialogs.alert(title, prefill);
}

var confirm = function(title, prefill){
    return dialogs.confirm(title, prefill);
}

var prompt = function(title, prefill){
    return dialogs.prompt(title, prefill);
}
