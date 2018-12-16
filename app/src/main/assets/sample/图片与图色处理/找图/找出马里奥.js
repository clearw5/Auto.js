
var superMario = images.read("./super_mario.jpg");
var mario = images.read("./mario.png");
var point = findImage(superMario, mario);
toastLog(point);

superMario.recycle();
mario.recycle();