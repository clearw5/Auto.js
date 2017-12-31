
//启动一个无限循环的线程
var thread = threads.start(function(){
    while(true){
        log("子线程运行中...");
        sleep(1000);
    }
});


//5秒后关闭线程
sleep(5000);
thread.interrupt();

