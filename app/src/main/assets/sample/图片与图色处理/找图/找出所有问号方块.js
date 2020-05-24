
var superMario = images.read("./super_mario.jpg");
var block = images.read("./block.png");

var result = images.matchTemplate(superMario, block, {
    threshold: 0.8
}).matches;
toastLog(result);

superMario.recycle();
block.recycle();