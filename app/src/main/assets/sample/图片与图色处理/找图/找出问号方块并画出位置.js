
var superMario = images.read("./super_mario.jpg");
var block = images.read("./block.png");
var points = images.matchTemplate(superMario, block, {
    threshold: 0.8
}).points;

toastLog(points);

var canvas = new Canvas(superMario);
var paint = new Paint();
paint.setColor(colors.parseColor("#2196F3"));
points.forEach(point => {
    canvas.drawRect(point.x, point.y, point.x + block.width, point.y + block.height, paint);
});
var image = canvas.toImage();
images.save(image, "/sdcard/tmp.png");

app.viewFile("/sdcard/tmp.png");

superMario.recycle();
block.recycle();
image.recycle();
