
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
                    resolve.apply(null, javaArrayToJsArray(r));
                });
            });
        }
        if(callback){
            return rtDialogs().multiChoice(title, index, items, function(r){
                callback(javaArrayToJsArray(r));
            });
        }
        return javaArrayToJsArray(rtDialogs().multiChoice(title, index, items, null));

    }

    var propertySetters = {
        "title": null,
        "titleColor": {adapter: parseColor},
        "buttonRippleColor": {adapter: parseColor},
        "icon": null,
        "content": null,
        "contentColor": {adapter: parseColor},
        "contentLineSpacing": null,
        "items": null,
        "itemsColor": {adapter: parseColor},
        "positive": {method: "positiveText"},
        "positiveColor": {adapter: parseColor},
        "neutral": {method: "neutralText"},
        "neutralColor": {adapter: parseColor},
        "negative": {method: "negativeText"},
        "negativeColor": {adapter: parseColor},
        "cancelable": null,
        "canceledOnTouchOutside": null,
        autoDismiss: null
    };

    dialogs.build = function(properties){
        var builder = Object.create(__runtime__.dialogs.newBuilder());
        builder.thread = threads.currentThread();
        for(var name in properties){
            if(!properties.hasOwnProperty(name)){
                continue;
            }
            applyDialogProperty(builder, name, properties[name]);
        }
        applyOtherDialogProperties(builder, properties);
        return ui.run(()=> builder.buildDialog());
    }

    function applyDialogProperty(builder, name, value){
        if(!propertySetters.hasOwnProperty(name)){
            return;
        }
        var propertySetter = propertySetters[name] || {};
        if(propertySetter.method == undefined){
            propertySetter.method = name;
        }
        if(propertySetter.adapter){
            value = propertySetter.adapter(value);
        }
        builder[propertySetter.method].call(builder, value);
    }

    function applyOtherDialogProperties(builder, properties){
        if(properties.inputHint != undefined || properties.inputPrefill != undefined){
            builder.input(wrapNonNullString(properties.inputHint), wrapNonNullString(properties.inputPrefill), 
                function(dialog, input){
                    input = input.toString();
                    builder.dialog.emit("input_change", builder.dialog, input);
                })
                   .alwaysCallInputCallback();
        }
        if(properties.items != undefined){
            var itemsSelectMode = properties.itemsSelectMode;
            if(itemsSelectMode == undefined || itemsSelectMode == 'select'){
                builder.itemsCallback(function(dialog, view, position, text){
                    builder.dialog.emit("item_select", position, text.toString(), builder.dialog);
                });
            }else if(itemsSelectMode == 'single'){
                builder.itemsCallbackSingleChoice(properties.itemsSelectedIndex == undefined ? -1 : properties.itemsSelectedIndex, 
                    function(dialog, view, which, text){
                        builder.dialog.emit("single_choice", which, text.toString(), builder.dialog);
                        return true;
                    });
            }else if(itemsSelectMode == 'multi'){
                builder.itemsCallbackMultiChoice(properties.itemsSelectedIndex == undefined ? null : properties.itemsSelectedIndex, 
                    function(dialog, view, indices, texts){
                        builder.dialog.emit("multi_choice", toJsArray(indices, (l, i)=> parseInt(l.get(i)),
                            toJsArray(texts, (l, i)=> l.get(i).toString())), builder.dialog);
                            return true;                        
                    });
            }else{
                throw new Error("unknown itemsSelectMode " + itemsSelectMode);
            }
        }
        if(properties.progress != undefined){
            var progress = properties.progress;
            var indeterminate = (progress.max == -1);
            builder.progress(indeterminate, progress.max, !!progress.showMinMax);
            builder.progressIndeterminateStyle(!!progress.horizontal);
        }
        if(properties.checkBoxPrompt != undefined || properties.checkBoxChecked != undefined){
            builder.checkBoxPrompt(wrapNonNullString(properties.checkBoxPrompt),  !!properties.checkBoxChecked, 
                function(view, checked){
                    builder.getDialog().emit("check", checked, builder.getDialog());
                });
        }
        if(properties.customView != undefined) {
            let customView = properties.customView;
            if(typeof(customView) == 'xml' || typeof(customView) == 'string') {
                customView = ui.run(() => ui.inflate(customView));
            }
            let wrapInScrollView = (properties.wrapInScrollView === undefined) ? true : properties.wrapInScrollView;
            builder.customView(customView, wrapInScrollView);
        }
    }

    function wrapNonNullString(str){
        if(str == null || str == undefined){
            return "";
        }
        return str;
    }

    function javaArrayToJsArray(javaArray){
        var jsArray = [];
        var len = javaArray.length;
        for (var i = 0;i < len;i++){
            jsArray.push(javaArray[i]);
        }
        return jsArray;
    }

    function toJsArray(object, adapter){
        var jsArray = [];
        var len = javaArray.length;
        for (var i = 0;i < len;i++){
            jsArray.push(adapter(object, i));
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

    function parseColor(c){
        if(typeof(c) == 'string'){
            return colors.parseColor(c);
        }
        return c;
    }

    scope.rawInput = dialogs.rawInput.bind(dialogs);

    scope.alert = dialogs.alert.bind(dialogs);

    scope.confirm = dialogs.confirm.bind(dialogs);

    scope.prompt = dialogs.prompt.bind(dialogs);

    return dialogs;
}