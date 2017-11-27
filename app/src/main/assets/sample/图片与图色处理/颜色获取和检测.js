if(!requestScreenCapture()){
   toast("请求截图失败");
   exit
}
sleep(2000);
var x = 760;
var y = 180;
//获取在点(x, y)处的颜色
var c = images.pixel(captureScreen(), x, y);
//显示该颜色
var msg = "";
msg += "在位置(" + x + ", " + y + ")处的颜色为" + colors.toString(c);
msg += "\nR = " + colors.red(c) + ", G = " + colors.green(c) + ", B = " + colors.blue(c);
//检测在点(x, y)处是否有颜色#73bdb6 (模糊比较)
var isDetected = images.detectsColor(captureScreen(), "#73bdb6", x, y);
msg += "\n该位置是否匹配到颜色#73bdb6: " + isDetected;
alert(msg);