"ui";

var url = "https://www.autojs.org/assets/uploads/profile/3-profileavatar.png";
var logo = null;

ui.layout(
    <vertical>
        <img id="img" w="150" h="150" url="{{url}}" />
        <button id="grayscale" text="灰度化" />
        <button id="binary" text="二值化" />
        <button id="hsv" text="RGB转HSV" />
        <button id="blur" text="模糊" />
        <button id="medianBlur" text="中值滤波" />
        <button id="gaussianBlur" text="高斯模糊" />
    </vertical>
);

//把一张图片设置到图片控件中
function setImage(img) {
    ui.run(() => {
        ui.img.setImageBitmap(img.bitmap);
    });
}

//启动一个处理图片的线程
var imgProcess = threads.start(function () {
    setInterval(() => { }, 1000);
});

//处理图片的函数，把任务交给图片处理线程处理
function processImg(process) {
    imgProcess.setTimeout(() => {
        if (logo == null) {
            logo = images.load(url);
        }
        //处理图片
        var result = process(logo);
        //把处理后的图片设置到图片控件中
        setImage(result);
    }, 0);
}

ui.grayscale.on("click", () => {
    processImg(img => {
        //灰度化
        return images.grayscale(img);
    });
});

ui.binary.on("click", () => {
    processImg(img => {
        //二值化，取灰度为30到200之间的图片
        return images.threshold(images.grayscale(img), 100, 200);
    });
});

ui.hsv.on("click", () => {
    processImg(img => {
        //RGB转HSV
        return images.cvtColor(img, "BGR2HSV");
    });
});

ui.blur.on("click", () => {
    processImg(img => {
        //RGB转HSV
        return images.blur(img, [10, 10]);
    });
});

ui.medianBlur.on("click", () => {
    processImg(img => {
        //RGB转HSV
        return images.medianBlur(img, 10);
    });
});
ui.gaussianBlur.on("click", () => {
    processImg(img => {
        //RGB转HSV
        return images.gaussianBlur(img, [5, 5]);
    });
});