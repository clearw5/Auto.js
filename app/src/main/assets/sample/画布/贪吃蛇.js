"ui";

ui.layout(
    <vertical>
        <canvas id="board" layout_weight="1"/>
        <relative h="120">
            <button id="up" text="↑" w="60" h="60" layout_alignParentTop="true" layout_centerHorizontal="true"/>
            <button id="left" text="←" w="60" h="60" layout_alignParentBottom="true" layout_toLeftOf="@id/down"/>
            <button id="down" text="↓" w="60" h="60" layout_alignParentBottom="true" layout_centerHorizontal="true"/>
            <button id="right" text="→" w="60" h="60" layout_alignParentBottom="true" layout_toRightOf="@id/down"/>
        </relative>
    </vertical>
);

//蛇的颜色
const SNAKE_COLOR = colors.parseColor("#4caf50");
//背景色
const BG_COLOR = colors.parseColor("#ffffff");
//苹果颜色
const APPLE_COLOR = colors.parseColor("#f44336");
//墙的颜色
const WALL_COLOR = colors.parseColor("#607d8b");
//文本颜色
const TEXT_COLOR =  colors.parseColor("#03a9f4");

//蛇自动移动的时间间隔，调小可以增加难度
const MOVE_INTERVAL = 500;
//方块宽度
const BLOCK_WIDTH = 40;
//游戏区域宽高
const GAME_BOARD_HEIGHT = 20;
const GAME_BOARD_WIDTH = 15;

//蛇的四个移动方向
const DIRECTION_LEFT = {x: -1, y: 0};
const DIRECTION_RIGHT = {x: 1, y: 0};
const DIRECTION_UP = {x: 0, y: -1};
const DIRECTION_DOWN = {x: 0, y: 1};

//蛇，是一个蛇身的坐标的数组
var snake = [{x: 4, y: 2}, {x: 3, y: 2}, {x: 2, y: 2}];
//苹果的坐标
var apple = generateApple();
//当前蛇的移动方向
var direction = DIRECTION_RIGHT;
//标记游戏是否结束
var isGameOver = false;
//分数
var score = 0;

var paint = new Paint();
ui.board.on("draw", function(canvas){
    //绘制背景色
    canvas.drawColor(BG_COLOR);
    //绘制分数
    paint.setColor(TEXT_COLOR);
    paint.setTextSize(50);
    canvas.drawText("分数: " + score, 30, 70, paint);
    //如果游戏结束则绘制游戏结束字样
    if(isGameOver){
        canvas.drawText("游戏结束！", canvas.getWidth() - 280, 70, paint);
    }
    //计算坐标偏移，是的游戏区域绘制在画面的水平居中位置
    var offset = {
        x: (canvas.getWidth() - (GAME_BOARD_WIDTH + 2) * BLOCK_WIDTH) / 2,
        y: 100
    };
    //偏移坐标
    canvas.translate(offset.x, offset.y);
    //绘制围墙
    paint.setColor(WALL_COLOR);
    for(var i = 0; i <= GAME_BOARD_WIDTH + 1; i++){
        //上围墙
        drawBlock(canvas, paint, i, 0);
        //下围墙
        drawBlock(canvas, paint, i, GAME_BOARD_HEIGHT + 1);
    }
    for(var i = 0; i <= GAME_BOARD_HEIGHT + 1; i++){
        //左围墙
        drawBlock(canvas, paint, 0, i);
        //右围墙
        drawBlock(canvas, paint, GAME_BOARD_WIDTH + 1, i);
    }
    //绘制蛇身
    paint.setColor(SNAKE_COLOR);
    for(var i = 0; i < snake.length; i++){
        drawBlock(canvas, paint, snake[i].x, snake[i].y);
    }
    //绘制苹果
    paint.setColor(APPLE_COLOR);
    drawBlock(canvas, paint, apple.x, apple.y);
});

//启动游戏线程
var gameThread = threads.start(game);

//按键点击时改变蛇的移动方向
ui.left.on("click", ()=> direction = DIRECTION_LEFT);
ui.right.on("click", ()=> direction = DIRECTION_RIGHT);
ui.up.on("click", ()=> direction = DIRECTION_UP);
ui.down.on("click", ()=> direction = DIRECTION_DOWN);


function game(){
    //每隔一段时间让蛇自动前进
    setInterval(()=>{
        move(direction.x, direction.y);
    }, MOVE_INTERVAL);
}

function move(dx, dy){
    log("move: %d, %d", dx, dy);
    direction.x = dx;
    direction.y = dy;
    //蛇前进时把一个新的方块添加到蛇头前面
    var head = snake[0];
    snake.splice(0, 0, {
        x: head.x + dx,
        y: head.y + dy
    });
    //如果蛇头吃到了苹果
    if(snakeEatsApple()){
        //添加分数和重新生成苹果
        score += 5;
        apple = generateApple();
    }else{
        //没有吃到苹果的情况下把蛇尾去掉保持蛇身长度不变
        snake.pop();
    }
    //碰撞检测
    collisionTest();
}

function snakeEatsApple(){
    return snake[0].x == apple.x && snake[0].y == apple.y;
}

function generateApple(){
    //循环生成苹果直至苹果不会生成在蛇身上
    var x, y;
    do{
        x = random(1, GAME_BOARD_WIDTH);
        y = random(1, GAME_BOARD_HEIGHT);
    }while(!isAppleValid(x, y));
    return {x: x, y: y};
}

function isAppleValid(x, y){
    for (var i = 0; i < snake.length; i++) {
        if (snake[i].x == x && snake[i].y == y) {
            return false;
        }
    }
    return true;
}

function collisionTest(){
    //检测蛇有没有撞到墙上
    var head = snake[0];
    if(head.x < 1 || head.x > GAME_BOARD_WIDTH
        || head.y < 1 || head.y > GAME_BOARD_HEIGHT){
            gameOver();
            return;
    }
    //检测蛇有没有撞到自己
    for(var i = 1; i < snake.length; i++){
        if(snake[i].x == head && snake[i].y == head){
            gameOver();
            return;
        }
    }
}

function gameOver(){
    gameThread.interrupt();
    isGameOver = true;
}

function drawBlock(canvas, paint, x, y){
    x *= BLOCK_WIDTH;
    y *= BLOCK_WIDTH;
    canvas.drawRect(x, y, x + BLOCK_WIDTH, y + BLOCK_WIDTH, paint);
}