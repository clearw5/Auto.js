
module.exports = function(__runtime__, scope){
    var timers = Object.create(__runtime__.timers);

    scope.__asGlobal__(timers, ['setTimeout', 'clearTimeout', 'setInterval', 'clearInterval', 'setImmediate', 'clearImmediate']);

    scope.loop = function(){
        console.warn("loop() has been deprecated and has no effect. Remove it from your code.");
    }

    return timers;
}

