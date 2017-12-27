
//启动一个线程
threads.start(function(){
    //在线程中每隔1秒打印"线程1"
    while(true){
        log("线程1");
        sleep(1000);
    }
});

//启动另一个线程
threads.start(function(){
    //在线程中每隔2秒打印"线程1"
    while(true){
        log("线程2");
        sleep(2000);
    }
});

//在主线程中每隔3秒打印"主线程"
for(var i = 0; i < 10; i++){
    log("主线程");
    sleep(3000);
}
//打印100次后退出所有线程
threads.shutDownAll();
