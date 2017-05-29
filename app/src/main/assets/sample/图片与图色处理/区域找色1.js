if(!requestScreenCapture()){
    toast("请求截图失败");
    stop();
}
var img = captureScreen();
//0xffffff为白色
toastLog("开始找色");
//指定在位置(90, 220)宽高为900*1000的区域找色。
//0xff00cc是编辑器的深粉红色字体(字符串)颜色
var point = findColorInRegion(img, "#ff00cc", 90, 220, 900, 1000);
if(point){
    toastLog("x = " + point.x + ", y = " + point.y);
}else{
    toastLog("没有找到");
}


