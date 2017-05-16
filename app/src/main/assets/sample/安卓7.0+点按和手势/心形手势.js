"auto";

toast("开启开发者选项-指针位置或者在画画软件才能查看效果");

setScreenMetrics(1080, 1920);

var points = [10000];
var interval = 0.1;
var x0 = 600;
var y0 = 1000;
var a = 120;

for(var t = 0; t < 2 * Math.PI; t += interval){
    var x = x0 + a * (2 * Math.cos(t) - Math.cos(2 * t));
    var y = y0 + a * (2 * Math.sin(t) - Math.sin(2 * t));
    points.push([parseInt(x), parseInt(y)]);
}

gesture.apply(null, points);
