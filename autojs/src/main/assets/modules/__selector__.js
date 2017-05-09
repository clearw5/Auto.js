
module.exports = function(__runtime__, scope){

    var __selector__ = __runtime__.selector(scope.__engine__);
    var __obj__ = new java.lang.Object();

    for(var method in __selector__){
        if(!__obj__[method] && !scope[method]){
            scope[method] = (function(method) {
                return function(){
                    var s = selector();
                    //这里不知道怎么写。尴尬。只能写成这样。
                    if(arguments.length == 0){
                       return s[method]();
                    }else if(arguments.length == 1){
                       return s[method](arguments[0]);
                    }else if(arguments.length == 2){
                       return s[method](arguments[0], arguments[1]);
                    }else if(arguments.length == 3){
                       return s[method](arguments[0], arguments[1], arguments[2]);
                    }else if(arguments.length == 4){
                       return s[method](arguments[0], arguments[1], arguments[2], arguments[3]);
                    }else{
                       return s[method].call(s, Array.prototype.slice.call(arguments));
                    }
                };
            })(method);
        }
    }

    return function(){
        return __runtime__.selector(scope.__engine__);
    };
}


