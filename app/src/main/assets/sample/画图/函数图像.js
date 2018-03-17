"ui";
ui.layout(
    <frame>
        <canvas id="graphic" w="*" h="*"/>
    </frame>
);

var f = function(x){
    return x * x + 3 * x - 4;
}

var paint = new Paint();
paint.setStrokeWidth(2);
var start = -5;
var end = 5;

ui.graphic.on("draw", function(canvas){
    var w = canvas.getWidth();
    for (var i = 0; i < w; i++) {
        var x = (end - start) / w * i + start;
        var y = f(x);
        canvas.drawPoint(x, y, paint);
    }
});