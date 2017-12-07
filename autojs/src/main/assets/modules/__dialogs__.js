
module.exports = function(__runtime__, scope){
    var dialogs =  {};

    dialogs.rawInput = function(title, prefill, callback){
       prefill = prefill || "";
       if(isUiThread() && !callback){
           return new Promise(function(resolve, reject){
               rtDialogs().rawInput(title, prefill, function(){
                   resolve.apply(null, Array.prototype.slice.call(arguments));
               });
           });
       }
       return rtDialogs().rawInput(title, prefill, callback ? callback : null);
    };

    dialogs.input = function(title, prefill, callback){
        prefill = prefill || "";
        if(isUiThread() && !callback){
            return new Promise(function(resolve, reject){
               rtDialogs().rawInput(title, prefill, function(str){
                   resolve(eval(str));
               });
            });
        }
        if(callback){
            dialogs.rawInput(title, prefill, function(str){
                callback(eval(str));
            });
            return;
        }
        return eval(dialogs.rawInput(title, prefill), callback ? callback : null);
    }

    dialogs.prompt = dialogs.rawInput;

    dialogs.alert = function(title, prefill, callback){
        prefill = prefill || "";
        if(isUiThread() && !callback){
            return new Promise(function(resolve, reject){
                rtDialogs().alert(title, prefill, function(){
                    resolve.apply(null, Array.prototype.slice.call(arguments));
                });
            });
        }
        return rtDialogs().alert(title, prefill, callback ? callback : null);
    }

    dialogs.confirm = function(title, prefill, callback){
        prefill = prefill || "";
         if(isUiThread() && !callback){
            return new Promise(function(resolve, reject){
                rtDialogs().confirm(title, prefill, function(){
                    resolve.apply(null, Array.prototype.slice.call(arguments));
                });
            });
        }
        return rtDialogs().confirm(title, prefill, callback ? callback : null);
    }

    dialogs.select = function(title, items, callback){
        if(items instanceof Array){
             if(isUiThread() && !callback){
                return new Promise(function(resolve, reject){
                    rtDialogs().select(title, items, function(){
                        resolve.apply(null, Array.prototype.slice.call(arguments));
                    });
                });
            }
            return rtDialogs().select(title, items, callback ? callback : null);
        }
        return rtDialogs().select(title, [].slice.call(arguments, 1), null);
    }

    dialogs.singleChoice = function(title, items, index, callback){
        index = index || 0;
        if(isUiThread() && !callback){
            return new Promise(function(resolve, reject){
                rtDialogs().singleChoice(title, index, items, function(){
                    resolve.apply(null, Array.prototype.slice.call(arguments));
                });
            });
        }
        return rtDialogs().singleChoice(title, index, items, callback ? callback : null);
    }

    dialogs.multiChoice = function(title, items, index, callback){
        index = index || [];
        if(isUiThread() && !callback){
            return new Promise(function(resolve, reject){
                rtDialogs().singleChoice(title, index, items, function(r){
                    resolve.apply(null, toJsArray(r));
                });
            });
        }
        if(callback){
            return rtDialogs().multiChoice(title, index, items, function(r){
                callback(toJsArray(r));
            });
        }
        return toJsArray(rtDialogs().multiChoice(title, index, items, null));

    }

    function toJsArray(javaArray){
        var jsArray = [];
        var len = javaArray.length;
        for (var i = 0;i < len;i++){
            jsArray.push(javaArray[i]);
        }
        return jsArray;
    }

    function rtDialogs(){
        var d = __runtime__.dialogs;
        if(!isUiThread()){
            return d.nonUiDialogs;
        }else{
            return d;
        }
    }

    function isUiThread(){
        return android.os.Looper.myLooper() == android.os.Looper.getMainLooper();
    }

    scope.rawInput = dialogs.rawInput.bind(dialogs);

    scope.alert = dialogs.alert.bind(dialogs);

    scope.confirm = dialogs.confirm.bind(dialogs);

    scope.prompt = dialogs.prompt.bind(dialogs);

    return dialogs;
}