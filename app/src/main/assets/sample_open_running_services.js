
function openRunningServices(){
    launch("com.android.settings");
    var i = 0;
    while(!click("开发者选项")){
        i++;
        if(i == 10){
            toast("开发者选项未开启");
            return;
        }
        scrollDown();
    }
    while(!click("正在运行的服务"));
}


importClass("android.os.Build.VERSION");

if(VERSION.SDK_INT < 23){
    toast("本代码只适用于Android6.0以上");
}else{
    openRunningServices();
}