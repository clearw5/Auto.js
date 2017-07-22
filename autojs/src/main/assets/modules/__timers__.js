
module.exports = function(__runtime__, scope){
    var timers = Object.create(__runtime__.timers);

    scope.__asGlobal__(timers, ['loop', 'setTimeout', 'clearTimeout', 'setInterval', 'clearInterval', 'setImmediate', 'clearImmediate']);

    return timers;
}

