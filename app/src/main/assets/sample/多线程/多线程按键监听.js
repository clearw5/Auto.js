auto();

threads.start(function(){
    //在子线程中调用observeKey()从而使按键事件处理在子线程执行
    events.observeKey();
    events.on("key_down", function(keyCode, events){
        //音量键关闭脚本
        if(keyCode == keys.volume_up){
            exit();
        }
    });
});

toast("音量上键关闭脚本");

events.on("exit", function(){
    toast("脚本已结束");
});

while(true){
    log("脚本运行中...");
    sleep(2000);
}