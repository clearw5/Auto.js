if(!requestScreenCapture()){
    toast("请求截图失败");
    exit();
}
var img = captureScreen();
toastLog("开始找色");
//指定在位置(100, 220)宽高为400*400的区域找色。
//#75438a是编辑器默认主题的棕红色字体(数字)颜色，位置大约在第5行的"2000"，坐标大约为(283, 465)
var point = findColorInRegion(img, "#75438a", 90, 220, 900, 1000);
if(point){
    toastLog("x = " + point.x + ", y = " + point.y);
}else{
    toastLog("没有找到");
}


