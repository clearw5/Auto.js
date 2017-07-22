"auto";

events.observeKey();

var interval = 5000;
var task = task1;

events.onKeyDown("volume_up", function(event){
    if(task == task1){
        task = task2;
    }else{
        task = task1;
    }
    toast("任务已切换");
});

events.onKeyDown("volume_down", function(event){
    toast("程序结束");
    exit();
});

task();

loop();

function task1(){
    toast("任务1运行中，音量下键结束，音量上键切换任务");
    setTimeout(task, interval);
}

function task2(){
    toast("任务2运行中，音量下键结束，音量上键切换任务");
    setTimeout(task, interval);
}