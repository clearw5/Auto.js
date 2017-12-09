if(!requestScreenCapture()){
    toast("请求截图失败");
    exit();
}
var img = captureScreen();
//0x9966ff为编辑器紫色字体的颜色
toastLog("开始找色");
var point = findColor(img, 0x9966ff);
if(point){
    toastLog("x = " + point.x + ", y = " + point.y);
}else{
    toastLog("没有找到");
}


