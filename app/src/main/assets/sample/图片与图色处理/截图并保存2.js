//指定截图分辨率为 640×960
if(!requestScreenCapture(640, 960)){
    toast("请求截图失败");
    stop();
}
captureScreen("/sdcard/1.png");
