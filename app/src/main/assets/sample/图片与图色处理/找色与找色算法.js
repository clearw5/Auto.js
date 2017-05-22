if(!requestScreenCapture()){
    toast("请求截图失败");
    stop();
}
var img = captureScreen();
//0xffffff为白色
toastLog("开始找色");
var point = findColor(img, 0xffffff, {
    //指定算法为rgb+,更默认算法rgb更准确,但时间更久
    algorithm: "rgb+",
    //指定颜色临界值为16
    threshold: 16,
    //指定用8个线程找色
    threads: 8
});
if(point){
    toastLog("x = " + point.x + ", y = " + point.y);
}else{
    toastLog("没有找到");
}


