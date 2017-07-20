
module.exports = function(__runtime__, scope){
    var events = Object.create(__runtime__.events);

    var caller = function(func, args){
         var arr = [];
         var len = args.length;
         for(var i = 0; i < len; i++){
            arr.push(args[i]);
         }
         return func.apply(null, arr);
    };

    events.setFunctionCaller(caller);

    events.emitter = function(){
        var e = new com.stardust.autojs.runtime.api.EventEmitter();
        e.setFunctionCaller(caller);
        return e;
    }

    return events;
}

