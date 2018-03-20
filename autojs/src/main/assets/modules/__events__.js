
module.exports = function(__runtime__, scope){
    importClass(android.view.KeyEvent);
    var events = Object.create(__runtime__.events);

    events.__asEmitter__ = function(obj, thread){
        var emitter = thread ? events.emitter(thread) : events.emitter();
        for(var key in emitter){
            if(obj[key] == undefined && typeof(emitter[key]) == 'function'){
                obj[key] = emitter[key].bind(emitter);
            }
        }
        return obj;
    }
    var keys = {
        "home": KeyEvent.KEYCODE_HOME,
        "menu": KeyEvent.KEYCODE_MENU,
        "back": KeyEvent.KEYCODE_BACK,
        "volume_up": KeyEvent.KEYCODE_VOLUME_UP,
        "volume_down": KeyEvent.KEYCODE_VOLUME_DOWN
    }

    scope.keys = keys;



    return events;
}

