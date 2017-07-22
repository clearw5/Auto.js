
events.observeTouch();

events.setTouchEventTimeout(30);

toast("请在日志中查看触摸的点的坐标");

events.on("touch", function(point){
    log(point);
});

loop();