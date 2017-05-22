//减少截图分辨率以提高速度
if(!requestScreenCapture(640, 960)){
    toast("请求截图失败");
    stop();
}
var img = captureScreen();
toastLog("开始找色");
//0x02b902为输入法绿色字体的颜色
var point = findColor(img, 0x02b902, {
    //指定用8个线程找色
    threads: 8
});
if(point){
    toastLog("x = " + point.x + ", y = " + point.y);
}else{
    toastLog("没有找到");
}


