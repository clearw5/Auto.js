# colors

> Stability: 2 - Stable

在Auto.js有两种方式表示一个颜色。

一种是使用一个字符串"#AARRGGBB"或"#RRGGBB"，其中 AA 是Alpha通道(透明度)的值，RR 是R通道(红色)的值，GG 是G通道(绿色)的值，BB是B通道(蓝色)的值。例如"#ffffff"表示白色, "#7F000000"表示半透明的黑色。

另一种是使用一个16进制的"32位整数" 0xAARRGGBB 来表示一个颜色，例如 `0xFF112233`表示颜色"#112233", `0x11223344`表示颜色"#11223344"。

可以通过`colors.toString()`把颜色整数转换为字符串，通过`colors.parseColor()`把颜色字符串解析为颜色整数。

## colors.toString(color)
* `color` {number} 整数RGB颜色值
* 返回 {string}

返回颜色值的字符串，格式为 "#AARRGGBB"。

## colors.red(color)
* `color` {number} | {string} 颜色值
* 返回 {number}

返回颜色color的R通道的值，范围0~255.

## colors.green(color)
* `color` {number} | {string} 颜色值
* 返回 {number}

返回颜色color的G通道的值，范围0~255.

## colors.blue(color)
* `color` {number} | {string} 颜色值
* 返回 {number}

返回颜色color的B通道的值，范围0~255.

## colors.alpha(color)
* `color` {number} | {string} 颜色值
* 返回 {number}

返回颜色color的Alpha通道的值，范围0~255.

## colors.rgb(red, green, blue)
* red {number} 颜色的R通道的值
* blue {number} 颜色的G通道的值
* green {number} 颜色的B通道的值
* 返回 {number}

返回这些颜色通道构成的整数颜色值。Alpha通道将是255（不透明）。

## colors.argb(alpha, red, green, blue)
* `alpha` {number} 颜色的Alpha通道的值
* `red` {number}  颜色的R通道的值
* `green` {number} 颜色的G通道的值
* `blue` {number} 颜色的B通道的值
* 返回 {number}

返回这些颜色通道构成的整数颜色值。

## colors.parseColor(colorStr)
* `colorStr` {string} 表示颜色的字符串，例如"#112233"
* 返回 {number}

返回颜色的整数值。

## colors.isSimilar(color2, color2[, threshold, algorithm])
* `color1` {number} | {string} 颜色值1
* `color1` {number} | {string} 颜色值2
* `threshold` {number} 颜色相似度临界值，默认为4。取值范围为0~255。这个值越大表示允许的相似程度越小，如果这个值为0，则两个颜色相等时该函数才会返回true。
* `algorithm` {string} 颜色匹配算法，默认为"diff", 包括:
    * "diff": 差值匹配。与给定颜色的R、G、B差的绝对值之和小于threshold时匹配。
    * "rgb": rgb欧拉距离相似度。与给定颜色color的rgb欧拉距离小于等于threshold时匹配。
    * "rgb+": 加权rgb欧拉距离匹配([LAB Delta E](https://en.wikipedia.org/wiki/Color_difference))。
    * "hs": hs欧拉距离匹配。hs为HSV空间的色调值。
* 返回 {Boolean}

返回两个颜色是否相似。

## colors.equals(color1, color2)
* `color1` {number} | {string} 颜色值1
* `color1` {number} | {string} 颜色值2
* 返回 {Boolean}

返回两个颜色是否相等。**注意该函数会忽略Alpha通道的值进行比较*。

```
log(colors.equals("#112233", "#112234"));
log(colors.equals(0xFF112233, 0xFF223344));
```

# colors.BLACK

黑色，颜色值 #FF000000

# colors.DKGRAY  

深灰色，颜色值 #FF444444

# colors.GRAY  

灰色，颜色值 #FF888888

# colors.LTGRAY  

亮灰色，颜色值 #FFCCCCCC

# colors.WHITE  

白色，颜色值 #FFFFFFFF

# colors.RED  

红色，颜色值 #FFFF0000

# colors.GREEN  

绿色，颜色值 #FF00FF00

# colors.BLUE  

蓝色，颜色值 #FF0000FF

# colors.YELLOW  

黄色，颜色值 #FFFFFF00

# colors.CYAN  

青色，颜色值 #FF00FFFF

# colors.MAGENTA  

品红色，颜色值 #FFFF00FF

# colors.TRANSPARENT  

透明，颜色值 #00000000

# Images

> Stability: 2 - Stable

images模块提供了一些手机设备中常见的图片处理函数，包括截图、读写图片、图片剪裁、旋转、二值化、找色找图等。

该模块分为两个部分，找图找色部分和图片处理部分。

需要注意的是，image对象创建后尽量在不使用时进行回收，同时避免循环创建大量图片。因为图片是一种占用内存比较大的资源，尽管Auto.js通过各种方式（比如图片缓存机制、垃圾回收时回收图片、脚本结束时回收所有图片）尽量降低图片资源的泄漏和内存占用，但是糟糕的代码仍然可以占用大量内存。

Image对象通过调用`recycle()`函数来回收。例如：
```
// 读取图片
var img = images.read("./1.png");
//对图片进行操作
... 
// 回收图片
img.recycle();
```

例外的是，`caputerScreen()`返回的图片不需要回收。

## 图片处理

## images.read(path)
* `path` {string} 图片路径

读取在路径path的图片文件并返回一个Image对象。如果文件不存在或者文件无法解码则返回null。

## images.load(url)
* `url` {string} 图片URL地址

加载在地址URL的网络图片并返回一个Image对象。如果地址不存在或者图片无法解码则返回null。

## images.copy(img)
* `img` {Image} 图片
* 返回 {Image}

复制一张图片并返回新的副本。该函数会完全复制img对象的数据。

## images.save(image, path[, format = "png", quality = 100])
* `image` {Image} 图片
* `path` {string} 路径
* `format` {string} 图片格式，可选的值为:
    * `png`
    * `jpeg`/`jpg`
    * `webp`
* `quality` {number} 图片质量，为0~100的整数值

把图片image以PNG格式保存到path中。如果文件不存在会被创建；文件存在会被覆盖。

```
//把图片压缩为原来的一半质量并保存
var img = images.read("/sdcard/1.png");
images.save(img, "/sdcard/1.jpg", "jpg", 50);
app.viewFile("/sdcard/1.jpg");
```

## images.fromBase64(base64)
* `base64` {string} 图片的Base64数据
* 返回 {Image}

解码Base64数据并返回解码后的图片Image对象。如果base64无法解码则返回`null`。

## images.toBase64(img[, format = "png", quality = 100])
* `image` {image} 图片
* `format` {string} 图片格式，可选的值为:
    * `png`
    * `jpeg`/`jpg`
    * `webp`
* `quality` {number} 图片质量，为0~100的整数值
* 返回 {string}

把图片编码为base64数据并返回。

## images.fromBytes(bytes)
* `bytes` {byte[]} 字节数组

解码字节数组bytes并返回解码后的图片Image对象。如果bytes无法解码则返回`null`。

## images.toBytes(img[, format = "png", quality = 100])
* `image` {image} 图片
* `format` {string} 图片格式，可选的值为:
    * `png`
    * `jpeg`/`jpg`
    * `webp`
* `quality` {number} 图片质量，为0~100的整数值
* 返回 {byte[]}

把图片编码为字节数组并返回。

## images.clip(img, x, y, w, h)
* `img` {Image} 图片
* `x` {number} 剪切区域的左上角横坐标
* `y` {number} 剪切区域的左上角纵坐标
* `w` {number} 剪切区域的宽度
* `h` {number} 剪切区域的高度
* 返回 {Image}

从图片img的位置(x, y)处剪切大小为w * h的区域，并返回该剪切区域的新图片。

```
var src = images.read("/sdcard/1.png");
var clip = images.clip(src, 100, 100, 400, 400);
images.save(clip, "/sdcard/clip.png");
```

## images.resize(img, size[, interpolation])
**[v4.1.0新增]**
* `img` {Image} 图片
* `size` {Array} 两个元素的数组[w, h]，分别表示宽度和高度；如果只有一个元素，则宽度和高度相等
* `interpolation` {string} 插值方法，可选，默认为"LINEAR"（线性插值），可选的值有：
    * `NEAREST` 最近邻插值
    * `LINEAR` 线性插值（默认）
    * `AREA` 区域插值
    * `CUBIC` 三次样条插值
    * `LANCZOS4` Lanczos插值
    参见[InterpolationFlags](https://docs.opencv.org/3.4.4/da/d54/group__imgproc__transform.html#ga5bb5a1fea74ea38e1a5445ca803ff121)

* 返回 {Image}

调整图片大小，并返回调整后的图片。例如把图片放缩为200*300：`images.resize(img, [200, 300])`。

参见[Imgproc.resize](https://docs.opencv.org/3.4.4/da/d54/group__imgproc__transform.html#ga47a974309e9102f5f08231edc7e7529d)。

## images.scale(img, fx, fy[, interpolation])
**[v4.1.0新增]**
* `img` {Image} 图片
* `fx` {number} 宽度放缩倍数
* `fy` {number} 高度放缩倍数
* `interpolation` {string} 插值方法，可选，默认为"LINEAR"（线性插值），可选的值有：
    * `NEAREST` 最近邻插值
    * `LINEAR` 线性插值（默认）
    * `AREA` 区域插值
    * `CUBIC` 三次样条插值
    * `LANCZOS4` Lanczos插值
    参见[InterpolationFlags](https://docs.opencv.org/3.4.4/da/d54/group__imgproc__transform.html#ga5bb5a1fea74ea38e1a5445ca803ff121)

* 返回 {Image}

放缩图片，并返回放缩后的图片。例如把图片变成原来的一半：`images.scale(img, 0.5, 0.5)`。

参见[Imgproc.resize](https://docs.opencv.org/3.4.4/da/d54/group__imgproc__transform.html#ga47a974309e9102f5f08231edc7e7529d)。

## images.rotate(img, degress[, x, y])
**[v4.1.0新增]**
* `img` {Image} 图片
* `degress` {number} 旋转角度。
* `x` {number} 旋转中心x坐标，默认为图片中点
* `y` {number} 旋转中心y坐标，默认为图片中点
* 返回 {Image}

将图片逆时针旋转degress度，返回旋转后的图片对象。

例如逆时针旋转90度为`images.rotate(img, 90)`。

## images.concat(img1, image2[, direction])
**[v4.1.0新增]**
* `img1` {Image} 图片1
* `img2` {Image} 图片2
* direction {string} 连接方向，默认为"RIGHT"，可选的值有：
    * `LEFT` 将图片2接到图片1左边
    * `RIGHT` 将图片2接到图片1右边
    * `TOP` 将图片2接到图片1上边
    * `BOTTOM` 将图片2接到图片1下边
* 返回 {Image}

连接两张图片，并返回连接后的图像。如果两张图片大小不一致，小的那张将适当居中。

## images.grayscale(img)
**[v4.1.0新增]**
* `img` {Image} 图片
* 返回 {Image}

灰度化图片，并返回灰度化后的图片。

## image.threshold(img, threshold, maxVal[, type])
**[v4.1.0新增]**
* `img` {Image} 图片
* `threshold` {number} 阈值
* `maxVal` {number} 最大值
* `type` {string} 阈值化类型，默认为"BINARY"，参见[ThresholdTypes](https://docs.opencv.org/3.4.4/d7/d1b/group__imgproc__misc.html#gaa9e58d2860d4afa658ef70a9b1115576), 可选的值:
    * `BINARY` 
    * `BINARY_INV` 
    * `TRUNC`
    * `TOZERO`
    * `TOZERO_INV`
    * `OTSU`
    * `TRIANGLE` 
    
* 返回 {Image}

将图片阈值化，并返回处理后的图像。可以用这个函数进行图片二值化。例如：`images.threshold(img, 100, 255, "BINARY")`，这个代码将图片中大于100的值全部变成255，其余变成0，从而达到二值化的效果。如果img是一张灰度化图片，这个代码将会得到一张黑白图片。

可以参考有关博客（比如[threshold函数的使用](https://blog.csdn.net/u012566751/article/details/77046445)）或者OpenCV文档[threshold](https://docs.opencv.org/3.4.4/d7/d1b/group__imgproc__misc.html#gae8a4a146d1ca78c626a53577199e9c57)。

## images.adaptiveThreshold(img, maxValue, adaptiveMethod, thresholdType, blockSize, C)
**[v4.1.0新增]**
* `img` {Image} 图片
* `maxValue` {number} 最大值
* `adaptiveMethod` {string} 在一个邻域内计算阈值所采用的算法，可选的值有：
    * `MEAN_C` 计算出领域的平均值再减去参数C的值
    * `GAUSSIAN_C` 计算出领域的高斯均值再减去参数C的值
* `thresholdType` {string} 阈值化类型，可选的值有：
    * `BINARY`
    * `BINARY_INV` 
* `blockSize` {number} 邻域块大小
* `C` {number} 偏移值调整量
* 返回 {Image}

对图片进行自适应阈值化处理，并返回处理后的图像。

可以参考有关博客（比如[threshold与adaptiveThreshold](https://blog.csdn.net/guduruyu/article/details/68059450)）或者OpenCV文档[adaptiveThreshold](https://docs.opencv.org/3.4.4/d7/d1b/group__imgproc__misc.html#ga72b913f352e4a1b1b397736707afcde3
)。

## images.cvtColor(img, code[, dstCn])
**[v4.1.0新增]**
* `img` {Image} 图片
* `code` {string} 颜色空间转换的类型，可选的值有一共有205个（参见[ColorConversionCodes](https://docs.opencv.org/3.4.4/d8/d01/group__imgproc__color__conversions.html#ga4e0972be5de079fed4e3a10e24ef5ef0)），这里只列出几个：
    * `BGR2GRAY` BGR转换为灰度
    * `BGR2HSV ` BGR转换为HSV 
    * ``
* `dstCn` {number} 目标图像的颜色通道数量，如果不填写则根据其他参数自动决定。
* 返回 {Image}

对图像进行颜色空间转换，并返回转换后的图像。

可以参考有关博客（比如[颜色空间转换](https://blog.csdn.net/u011574296/article/details/70896811?locationNum=14&fps=1)）或者OpenCV文档[cvtColor](https://docs.opencv.org/3.4.4/d8/d01/group__imgproc__color__conversions.html#ga397ae87e1288a81d2363b61574eb8cab)。


## images.inRange(img, lowerBound, upperBound)
**[v4.1.0新增]**
* `img` {Image} 图片
* `lowerBound` {string} | {number} 颜色下界
* `upperBound` {string} | {number} 颜色下界
* 返回 {Image}

将图片二值化，在lowerBound~upperBound范围以外的颜色都变成0，在范围以内的颜色都变成255。

例如`images.inRange(img, "#000000", "#222222")`。

## images.interval(img, color, interval)
**[v4.1.0新增]**
* `img` {Image} 图片
* `color` {string} | {number} 颜色值
* `interval` {number} 每个通道的范围间隔
* 返回 {Image}

将图片二值化，在color-interval ~ color+interval范围以外的颜色都变成0，在范围以内的颜色都变成255。这里对color的加减是对每个通道而言的。

例如`images.interval(img, "#888888", 16)`，每个通道的颜色值均为0x88，加减16后的范围是[0x78, 0x98]，因此这个代码将把#787878~#989898的颜色变成#FFFFFF，而把这个范围以外的变成#000000。

## images.blur(img, size[, anchor, type])
**[v4.1.0新增]**
* `img` {Image} 图片
* `size` {Array} 定义滤波器的大小，如[3, 3]
* `anchor` {Array} 指定锚点位置(被平滑点)，默认为图像中心
* `type` {string} 推断边缘像素类型，默认为"DEFAULT"，可选的值有：
    * `CONSTANT` iiiiii|abcdefgh|iiiiiii with some specified i
    * `REPLICATE` aaaaaa|abcdefgh|hhhhhhh
    * `REFLECT` fedcba|abcdefgh|hgfedcb
    * `WRAP` cdefgh|abcdefgh|abcdefg
    * `REFLECT_101` gfedcb|abcdefgh|gfedcba
    * `TRANSPARENT` uvwxyz|abcdefgh|ijklmno
    * `REFLECT101` same as BORDER_REFLECT_101
    * `DEFAULT` same as BORDER_REFLECT_101
    * `ISOLATED` do not look outside of ROI
* 返回 {Image}

对图像进行模糊（平滑处理），返回处理后的图像。

可以参考有关博客（比如[实现图像平滑处理](https://www.cnblogs.com/denny402/p/3848316.html)）或者OpenCV文档[blur](https://docs.opencv.org/3.4.4/d4/d86/group__imgproc__filter.html#ga8c45db9afe636703801b0b2e440fce37)。

## images.medianBlur(img, size)
**[v4.1.0新增]**
* `img` {Image} 图片
* `size` {Array} 定义滤波器的大小，如[3, 3]
* 返回 {Image}

对图像进行中值滤波，返回处理后的图像。

可以参考有关博客（比如[实现图像平滑处理](https://www.cnblogs.com/denny402/p/3848316.html)）或者OpenCV文档[blur](https://docs.opencv.org/3.4.4/d4/d86/group__imgproc__filter.html#ga564869aa33e58769b4469101aac458f9)。

## images.gaussianBlur(img, size[, sigmaX, sigmaY, type])
**[v4.1.0新增]**
* `img` {Image} 图片
* `size` {Array} 定义滤波器的大小，如[3, 3]
* `sigmaX` {number} x方向的标准方差，不填写则自动计算
* `sigmaY` {number} y方向的标准方差，不填写则自动计算
* `type` {string} 推断边缘像素类型，默认为"DEFAULT"，参见`images.blur`
* 返回 {Image}

对图像进行高斯模糊，返回处理后的图像。

可以参考有关博客（比如[实现图像平滑处理](https://www.cnblogs.com/denny402/p/3848316.html)）或者OpenCV文档[GaussianBlur](https://docs.opencv.org/3.4.4/d4/d86/group__imgproc__filter.html#gaabe8c836e97159a9193fb0b11ac52cf1)。

## images.matToImage(mat)
**[v4.1.0新增]**
* `mat` {Mat} OpenCV的Mat对象
* 返回 {Image}

把Mat对象转换为Image对象。

## 找图找色

## images.requestScreenCapture([landscape])
* `landscape` {boolean} 布尔值， 表示将要执行的截屏是否为横屏。如果landscape为false, 则表示竖屏截图; true为横屏截图。

向系统申请屏幕截图权限，返回是否请求成功。

第一次使用该函数会弹出截图权限请求，建议选择“总是允许”。

这个函数只是申请截图权限，并不会真正执行截图，真正的截图函数是`captureScreen()`。

该函数在截图脚本中只需执行一次，而无需每次调用`captureScreen()`都调用一次。

**如果不指定landscape值，则截图方向由当前设备屏幕方向决定**，因此务必注意执行该函数时的屏幕方向。

建议在本软件界面运行该函数，在其他软件界面运行时容易出现一闪而过的黑屏现象。  

示例:
```
//请求截图
if(!requestScreenCapture()){
    toast("请求截图失败");
    exit();
}
//连续截图10张图片(间隔1秒)并保存到存储卡目录
for(var i = 0; i < 10; i++){
    captureScreen("/sdcard/screencapture" + i + ".png");
    sleep(1000);
}

```

该函数也可以作为全局函数使用。

## images.captureScreen()

截取当前屏幕并返回一个Image对象。

没有截图权限时执行该函数会抛出SecurityException。

该函数不会返回null，两次调用可能返回相同的Image对象。这是因为设备截图的更新需要一定的时间，短时间内（一般来说是16ms）连续调用则会返回同一张截图。

截图需要转换为Bitmap格式，从而该函数执行需要一定的时间(0~20ms)。

另外在requestScreenCapture()执行成功后需要一定时间后才有截图可用，因此如果立即调用captureScreen()，会等待一定时间后(一般为几百ms)才返回截图。

例子:

```
//请求横屏截图
requestScreenCapture(true);
//截图
var img = captureScreen();
//获取在点(100, 100)的颜色值
var color = images.pixel(img, 100, 100);
//显示该颜色值
toast(colors.toString(color));
```

该函数也可以作为全局函数使用。

## images.captureScreen(path)
* `path` {string} 截图保存路径

截取当前屏幕并以PNG格式保存到path中。如果文件不存在会被创建；文件存在会被覆盖。

该函数不会返回任何值。该函数也可以作为全局函数使用。

## images.pixel(image, x, y)
* `image` {Image} 图片
* `x` {number} 要获取的像素的横坐标。
* `y` {number} 要获取的像素的纵坐标。

返回图片image在点(x, y)处的像素的ARGB值。  

该值的格式为0xAARRGGBB，是一个"32位整数"(虽然JavaScript中并不区分整数类型和其他数值类型)。

坐标系以图片左上角为原点。以图片左侧边为y轴，上侧边为x轴。

## images.findColor(image, color, options)
* `image` {Image} 图片
* `color` {number} | {string} 要寻找的颜色的RGB值。如果是一个整数，则以0xRRGGBB的形式代表RGB值（A通道会被忽略）；如果是字符串，则以"#RRGGBB"代表其RGB值。
* `options` {Object} 选项

在图片中寻找颜色color。找到时返回找到的点Point，找不到时返回null。

选项包括：
* `region` {Array} 找色区域。是一个两个或四个元素的数组。(region[0], region[1])表示找色区域的左上角；region[2]*region[3]表示找色区域的宽高。如果只有region只有两个元素，则找色区域为(region[0], region[1])到屏幕右下角。如果不指定region选项，则找色区域为整张图片。
* `threshold` {number} 找色时颜色相似度的临界值，范围为0~255（越小越相似，0为颜色相等，255为任何颜色都能匹配）。默认为4。threshold和浮点数相似度(0.0~1.0)的换算为 similarity = (255 - threshold) / 255.

该函数也可以作为全局函数使用。

一个循环找色的例子如下：
```
requestScreenCapture();

//循环找色，找到红色(#ff0000)时停止并报告坐标
while(true){
    var img = captureScreen();
    var point = findColor(img, "#ff0000");
    if(point){
        toast("找到红色，坐标为(" + point.x + ", " + point.y + ")");
    }
}

```

一个区域找色的例子如下：
```
//读取本地图片/sdcard/1.png
var img = images.read("/sdcard/1.png");
//判断图片是否加载成功
if(!img){
    toast("没有该图片");
    exit();
}
//在该图片中找色，指定找色区域为在位置(400, 500)的宽为300长为200的区域，指定找色临界值为4
var point = findColor(img, "#00ff00", {
     region: [400, 500, 300, 200],
     threshold: 4
 });
if(point){
    toast("找到啦:" + point);
}else{
    toast("没找到");
}
```

## images.findColorInRegion(img, color, x, y[, width, height, threshold])

区域找色的简便方法。

相当于
```
images.findColor(img, color, {
     region: [x, y, width, height],
     threshold: threshold
});
```

该函数也可以作为全局函数使用。

## images.findColorEquals(img, color[, x, y, width, height])
* `img` {Image} 图片
* `color` {number} | {string} 要寻找的颜色
* `x` {number} 找色区域的左上角横坐标
* `y` {number} 找色区域的左上角纵坐标
* `width` {number} 找色区域的宽度
* `height` {number} 找色区域的高度
* 返回 {Point}

在图片img指定区域中找到颜色和color完全相等的某个点，并返回该点的左边；如果没有找到，则返回`null`。

找色区域通过`x`, `y`, `width`, `height`指定，如果不指定找色区域，则在整张图片中寻找。

该函数也可以作为全局函数使用。

示例：
(通过找QQ红点的颜色来判断是否有未读消息)
```
requestScreenCapture();
launchApp("QQ");
sleep(1200);
var p = findColorEquals(captureScreen(), "#f64d30");
if(p){
    toast("有未读消息");
}else{
    toast("没有未读消息");
}
```

## images.findMultiColors(img, firstColor, colors[, options])
* `img` {Image} 要找色的图片
* `firstColor` {number} | {string} 第一个点的颜色
* `colors` {Array} 表示剩下的点相对于第一个点的位置和颜色的数组，数组的每个元素为[x, y, color]
* `options` {Object} 选项，包括：
    * `region` {Array} 找色区域。是一个两个或四个元素的数组。(region[0], region[1])表示找色区域的左上角；region[2]*region[3]表示找色区域的宽高。如果只有region只有两个元素，则找色区域为(region[0], region[1])到屏幕右下角。如果不指定region选项，则找色区域为整张图片。
    * `threshold` {number} 找色时颜色相似度的临界值，范围为0~255（越小越相似，0为颜色相等，255为任何颜色都能匹配）。默认为4。threshold和浮点数相似度(0.0~1.0)的换算为 similarity = (255 - threshold) / 255.


多点找色，类似于按键精灵的多点找色，其过程如下：
1. 在图片img中找到颜色firstColor的位置(x0, y0)
2. 对于数组colors的每个元素[x, y, color]，检查图片img在位置(x + x0, y + y0)上的像素是否是颜色color，是的话返回(x0, y0)，否则继续寻找firstColor的位置，重新执行第1步
3. 整张图片都找不到时返回`null`

例如，对于代码`images.findMultiColors(img, "#123456", [[10, 20, "#ffffff"], [30, 40, "#000000"]])`，假设图片在(100, 200)的位置的颜色为#123456, 这时如果(110, 220)的位置的颜色为#fffff且(130, 240)的位置的颜色为#000000，则函数返回点(100, 200)。

如果要指定找色区域，则在options中指定，例如:
```
var p = images.findMultiColors(img, "#123456", [[10, 20, "#ffffff"], [30, 40, "#000000"]], {
    region: [0, 960, 1080, 960]
});
```

## images.detectsColor(image, color, x, y[, threshold = 16, algorithm = "diff"])
* `image` {Image} 图片
* `color` {number} | {string} 要检测的颜色
* `x` {number} 要检测的位置横坐标
* `y` {number} 要检测的位置纵坐标
* `threshold` {number} 颜色相似度临界值，默认为16。取值范围为0~255。
* `algorithm` {string} 颜色匹配算法，包括:
    * "equal": 相等匹配，只有与给定颜色color完全相等时才匹配。
    * "diff": 差值匹配。与给定颜色的R、G、B差的绝对值之和小于threshold时匹配。
    * "rgb": rgb欧拉距离相似度。与给定颜色color的rgb欧拉距离小于等于threshold时匹配。
 
    * "rgb+": 加权rgb欧拉距离匹配([LAB Delta E](https://en.wikipedia.org/wiki/Color_difference))。
    * "hs": hs欧拉距离匹配。hs为HSV空间的色调值。

返回图片image在位置(x, y)处是否匹配到颜色color。用于检测图片中某个位置是否是特定颜色。


一个判断微博客户端的某个微博是否被点赞过的例子：
```
requestScreenCapture();
//找到点赞控件
var like = id("ly_feed_like_icon").findOne();
//获取该控件中点坐标
var x = like.bounds().centerX();
var y = like.bounds().centerY();
//截图
var img = captureScreen();
//判断在该坐标的颜色是否为橙红色
if(images.detectsColor(img, "#fed9a8", x, y)){
    //是的话则已经是点赞过的了，不做任何动作
}else{
    //否则点击点赞按钮
    like.click();
}
```

## images.findImage(img, template[, options])
* `img` {Image} 大图片
* `template` {Image} 小图片（模板）
* `options` {Object} 找图选项

找图。在大图片img中查找小图片template的位置（模块匹配），找到时返回位置坐标(Point)，找不到时返回null。

选项包括：
* `threshold` {number} 图片相似度。取值范围为0~1的浮点数。默认值为0.9。
* `region` {Array} 找图区域。参见findColor函数关于region的说明。
* `level` {number} **一般而言不必修改此参数**。不加此参数时该参数会根据图片大小自动调整。找图算法是采用图像金字塔进行的, level参数表示金字塔的层次, level越大可能带来越高的找图效率，但也可能造成找图失败（图片因过度缩小而无法分辨）或返回错误位置。因此，除非您清楚该参数的意义并需要进行性能调优，否则不需要用到该参数。

该函数也可以作为全局函数使用。

一个最简单的找图例子如下：
```
var img = images.read("/sdcard/大图.png");
var templ = images.read("/sdcard/小图.png");
var p = findImage(img, templ);
if(p){
    toast("找到啦:" + p);
}else{
    toast("没找到");
}
```

稍微复杂点的区域找图例子如下：
```
auto();
requestScreenCapture();
var wx = images.read("/sdcard/微信图标.png");
//返回桌面
home();
//截图并找图
var p = findImage(captureScreen(), wx, {
    region: [0, 50],
    threshold: 0.8
});
if(p){
    toast("在桌面找到了微信图标啦: " + p);
}else{
    toast("在桌面没有找到微信图标");
}
```
## images.findImageInRegion(img, template, x, y[, width, height, threshold])

区域找图的简便方法。相当于：
```
images.findImage(img, template, {
    region: [x, y, width, height],
    threshold: threshold
})
```

该函数也可以作为全局函数使用。

## images.matchTemplate(img, template, options)
**[v4.1.0新增]**
* `img` {Image} 大图片
* `template` {Image} 小图片（模板）
* `options` {Object} 找图选项：
    * `threshold` {number} 图片相似度。取值范围为0~1的浮点数。默认值为0.9。
    * `region` {Array} 找图区域。参见findColor函数关于region的说明。
    * `max` {number} 找图结果最大数量，默认为5
    * `level` {number} **一般而言不必修改此参数**。不加此参数时该参数会根据图片大小自动调整。找图算法是采用图像金字塔进行的, level参数表示金字塔的层次, level越大可能带来越高的找图效率，但也可能造成找图失败（图片因过度缩小而无法分辨）或返回错误位置。因此，除非您清楚该参数的意义并需要进行性能调优，否则不需要用到该参数。
* 返回 {MatchingResult}

在大图片中搜索小图片，并返回搜索结果MatchingResult。该函数可以用于找图时找出多个位置，可以通过max参数控制最大的结果数量。也可以对匹配结果进行排序、求最值等操作。

# MatchingResult
**[v4.1.0新增]**
## matches
* {Array} 匹配结果的数组。

数组的元素是一个Match对象：
* `point` {Point} 匹配位置
* `similarity` {number} 相似度

例如: 
```
var result = images.matchTemplate(img, template, {
    max: 100
});
result.matches.forEach(match => {
    log("point = " + match.point + ", similarity = " + match.similarity);
});
```

## points
* {Array} 匹配位置的数组。

## first()
* 返回 {Match}

第一个匹配结果。如果没有任何匹配，则返回`null`。

## last()
* 返回 {Match}

最后一个匹配结果。如果没有任何匹配，则返回`null`。

## leftmost()
* 返回 {Match}

位于大图片最左边的匹配结果。如果没有任何匹配，则返回`null`。

## topmost()
* 返回 {Match}

位于大图片最上边的匹配结果。如果没有任何匹配，则返回`null`。

## rightmost()
* 返回 {Match}

位于大图片最右边的匹配结果。如果没有任何匹配，则返回`null`。

## bottommost()
* 返回 {Match}

位于大图片最下边的匹配结果。如果没有任何匹配，则返回`null`。

## best()
* 返回 {Match}

相似度最高的匹配结果。如果没有任何匹配，则返回`null`。

## worst()
* 返回 {Match}

相似度最低的匹配结果。如果没有任何匹配，则返回`null`。

## sortBy(cmp)
* cmp {Function}|{string} 比较函数，或者是一个字符串表示排序方向。例如"left"表示将匹配结果按匹配位置从左往右排序、"top"表示将匹配结果按匹配位置从上往下排序，"left-top"表示将匹配结果按匹配位置从左往右、从上往下排序。方向包括`left`（左）, `top` （上）, `right` （右）, `bottom`（下）。
* {MatchingResult}

对匹配结果进行排序，并返回排序后的结果。

```
var result = images.matchTemplate(img, template, {
    max: 100
});
log(result.sortBy("top-right"));
```

# Image

表示一张图片，可以是截图的图片，或者本地读取的图片，或者从网络获取的图片。

## Image.getWidth()

返回以像素为单位图片宽度。

## Image.getHeight()

返回以像素为单位的图片高度。

## Image.saveTo(path)
* `path` {string} 路径

把图片保存到路径path。（如果文件存在则覆盖）

## Image.pixel(x, y)
* `x` {number} 横坐标
* `y` {number} 纵坐标

返回图片image在点(x, y)处的像素的ARGB值。  

该值的格式为0xAARRGGBB，是一个"32位整数"(虽然JavaScript中并不区分整数类型和其他数值类型)。

坐标系以图片左上角为原点。以图片左侧边为y轴，上侧边为x轴。

##

# Point

findColor, findImage返回的对象。表示一个点（坐标）。

## Point.x 

横坐标。

## Point.y

纵坐标。
