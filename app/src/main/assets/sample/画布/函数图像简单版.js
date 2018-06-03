"ui";
//ui布局为一块画布
ui.layout(
    <frame>
        <canvas id="board" w="*" h="*"/>
    </frame>
);

//要绘制的函数，这里是一个一元二次函数
var f = function(x){
    return x * x + 3 * x - 4;
}

//绘制区间
var minX = -5;
var maxX = 5;
var minY = -10;

//画笔
var paint = new Paint();

ui.board.on("draw", function(canvas){
    var w = canvas.getWidth();
    var h = canvas.getHeight();
    //计算y轴区间上限
    var maxY = minY + (maxX - minX) * h / w;
    //设置画笔颜色为黑色
    paint.setColor(colors.parseColor("#000000"));
    //绘制两个坐标轴
    canvas.drawLine(w / 2, 0, w / 2, h, paint);
    canvas.drawLine(0, h / 2, w, h / 2, paint);
    //设置画笔颜色为红色
    paint.setColor(colors.parseColor("#ff0000"));
    //绘制图像
    for(var i = 0; i < w; i++){
        var x = minX + i / w * (maxX - minX);
        var y = f(x);
        var j = h - (y - minY) / (maxY - minY) * h;
        canvas.drawPoint(i, j, paint);
    }
});
