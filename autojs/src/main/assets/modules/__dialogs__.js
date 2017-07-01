
module.exports = function(__runtime__, scope){
    var dialogs =  {};

    dialogs.rawInput = function(title, prefill){
       prefill = prefill || "";
       var s = __runtime__.dialogs.rawInput(title, prefill);
       return s ? String(s) : null;
    };

    dialogs.input = function(title, prefill){
        return eval(dialogs.rawInput(title, prefill));
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
        if(items instanceof Array){
            return __runtime__.dialogs.select(title, items);
        }
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

    scope.rawInput = function(title, prefill){
        return dialogs.rawInput(title, prefill);
    }

    scope.alert = function(title, prefill){
        dialogs.alert(title, prefill);
    }

    scope.confirm = function(title, prefill){
        return dialogs.confirm(title, prefill);
    }

    scope.prompt = function(title, prefill){
        return dialogs.prompt(title, prefill);
    }

    return dialogs;
}