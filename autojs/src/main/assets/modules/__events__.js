
module.exports = function(__runtime__, scope){
    importClass(android.view.KeyEvent);
    var events = Object.create(__runtime__.events);

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

