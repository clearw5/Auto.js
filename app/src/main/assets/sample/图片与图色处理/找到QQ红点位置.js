if(!requestScreenCapture()){
    toast("请求截图失败");
    exit();
}
launchApp("QQ");
sleep(2000);
var img = captureScreen();
toastLog("开始找色");
var point = findColor(img, "#f64d30");
if(point){
    toastLog("x = " + point.x + ", y = " + point.y);
}else{
    toastLog("没有找到");
}


