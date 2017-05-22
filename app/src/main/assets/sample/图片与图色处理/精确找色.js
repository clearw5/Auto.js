if(!requestScreenCapture()){
    toast("请求截图失败");
    stop();
}
var img = captureScreen();
toastLog("开始找色");
//0x006699为编辑器蓝色字体(var)的颜色
//找到颜色与0x006699完全相等的颜色
var point = findColorEquals(img, 0x006699);
if(point){
    toastLog("x = " + point.x + ", y = " + point.y);
}else{
    toastLog("没有找到");
}


