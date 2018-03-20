"ui";

const SNAKE_COLOR = colors.parseColor("#990000");
const SNAKE_WIDTH =
var apple = {
   x:
}
var snake = [];

ui.board.on("draw", function(){
    //绘制背景色
    canvas.drawColor(BG_COLOR);
    //绘制蛇身
    paint.setColor(SNAKE_COLOR);
    for(var i = 0; i < snake.length; i++){
        var x = snake[i].x * SNAKE_WIDTH;
        var y = snake[i].y * SNAKE_WIDTH;
        canvas.drawRect(x, y, x + SNAKE_WIDTH, y + SNAKE_WIDTH, paint);
    }
    //绘制苹果
    paint.setColor(APPLE_COLOR);
});