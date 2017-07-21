"auto";

events.observeKey();

var keyNames = {
    "KEYCODE_VOLUME_UP": "音量上键",
    "KEYCODE_VOLUME_DOWN": "音量下键",
    "KEYCODE_HOME": "Home键",
    "KEYCODE_BACK": "返回键",
    "KEYCODE_MENU": "菜单键",
    "KEYCODE_POWER": "电源键",
};

events.on("key", function(code, event){
    var keyName = getKeyName(code, event);
    if(event.getAction() == event.ACTION_DOWN){
        toast(keyName + "被按下");
    }else if(event.getAction() == event.ACTION_UP){
        toast(keyName + "弹起");
    }
});

loop();



function getKeyName(code, event){
    var keyCodeStr = event.keyCodeToString(code);
    var keyName = keyNames[keyCodeStr];
    if(!keyName){
        return keyCodeStr;
    }
    return keyName;
}