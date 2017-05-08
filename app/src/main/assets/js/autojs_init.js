__importClass__(com.stardust.scriptdroid.autojs.api.Shell);


var dialogs =  {};

dialogs.rawInput = function(title, prefill){
   prefill = prefill || "";
   return __runtime__.dialogs.rawInput(title, prefill);
};

dialogs.input = function(title, prefill){
    return eval(rawInput(title, prefill));
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

dialogs.singleChoice = function(title, index, items){
    return __runtime__.dialogs.singleChoice(title, index, [].slice.call(arguments, 2));
}

dialogs.multiChoice = function(title, index, items){
    var javaArray = __runtime__.dialogs.multiChoice(title, index, [].slice.call(arguments, 2));
    var jsArray = {};
    for each(i in javaArray){
        jsArray.push(i);
    }
    return jsArray;
}

var rawInput = function(title, prefill){
    return dialogs.rawInput(title, prefill);
}

var input = function(title, prefill){
    return dialogs.input(title, prefill);
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
/*

dialogs.rawInput = function(title, prefill){
    prefill = prefill || "";
    return __runtime__.dialogs.rawInput(title, prefill);
}

dialogs.input = function(title, prefill){
    return eval(rawInput(title, prefill));
}

dialogs.alert = function(title, content){
    __runtime__.dialogs.alert(title, content);
}

dialogs.select = function(title, content){
    __runtime__.dialogs.alert(title, content);
}

*/