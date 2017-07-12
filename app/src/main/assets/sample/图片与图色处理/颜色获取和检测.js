if(!requestScreenCapture()){
  toast("请求截图失败");
}
sleep(2000);
var x = 760;
var y = 180;
//获取在点(x, y)处的颜色
var c = images.pixel(captureScreen(), x, y);
//显示该颜色
toast((c >>> 0).toString(16));
//检测在点(x, y)处是否有颜色0x73bdb6 (模糊比较)
toast(images.detectsColor(captureScreen(), x, y, 0x73bdb6))