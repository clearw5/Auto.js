if(!requestScreenCapture()){
    toast("请求截图失败");
    stop();
}
var img = captureScreen();
images.saveImage(img, "/sdcard/1.png");