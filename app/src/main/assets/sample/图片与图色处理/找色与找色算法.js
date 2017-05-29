if(!requestScreenCapture()){
    toast("请求截图失败");
    stop();
}
var img = captureScreen();
//0xffffff为白色
toastLog("开始找色");
var point = findColor(img, 0xffffff, {
    //指定算法为rgb+,比默认算法rgb更准确,但时间更久
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

point = findColor(img, 0xffffff, {
    //指定算法为颜色差值，比默认算法rgb更快
    algorithm: "diff",
    //指定r, b, b分别都在范围ff±20内
    threshold: 0x202020,
});
if(point){
    toastLog("x = " + point.x + ", y = " + point.y);
}else{
    toastLog("没有找到");
}
