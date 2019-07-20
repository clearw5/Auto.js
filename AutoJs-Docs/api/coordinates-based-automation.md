
# 基于坐标的触摸模拟

> Stability: 2 - Stable

本章节介绍了一些使用坐标进行点击、滑动的函数。这些函数有的需要安卓7.0以上，有的需要root权限。

要获取要点击的位置的坐标，可以在开发者选项中开启"指针位置"。

基于坐标的脚本通常会有分辨率的问题，这时可以通过`setScreenMetrics()`函数来进行自动坐标放缩。这个函数会影响本章节的所有点击、长按、滑动等函数。通过设定脚本设计时的分辨率，使得脚本在其他分辨率下自动放缩坐标。

控件和坐标也可以相互结合。一些控件是无法点击的(clickable为false), 无法通过`.click()`函数来点击，这时如果安卓版本在7.0以上或者有root权限，就可以通过以下方式来点击：
```
//获取这个控件
var widget = id("xxx").findOne();
//获取其中心位置并点击
click(widget.bounds().centerX(), widget.bounds().centerY());
//如果用root权限则用Tap
```

## setScreenMetrics(width, height)

* width {number} 屏幕宽度，单位像素
* height {number} 屏幕高度，单位像素

设置脚本坐标点击所适合的屏幕宽高。如果脚本运行时，屏幕宽度不一致会自动放缩坐标。

例如在1920*1080的设备中，某个操作的代码为
```
setScreenMetrics(1080, 1920);
click(800, 200);
longClick(300, 500);
```
那么在其他设备上AutoJs会自动放缩坐标以便脚本仍然有效。例如在540 * 960的屏幕中`click(800, 200)`实际上会点击位置(400, 100)。

# 安卓7.0以上的触摸和手势模拟

> Stability: 2 - Stable

**注意以下命令只有Android7.0及以上才有效**

## click(x, y)
* `x` {number} 要点击的坐标的x值
* `y` {number} 要点击的坐标的y值

模拟点击坐标(x, y)，并返回是否点击成功。只有在点击执行完成后脚本才继续执行。

一般而言，只有点击过程(大约150毫秒)中被其他事件中断(例如用户自行点击)才会点击失败。

使用该函数模拟连续点击时可能有点击速度过慢的问题，这时可以用`press()`函数代替。

## longClick(x, y)
* `x` {number} 要长按的坐标的x值
* `y` {number} 要长按的坐标的y值

模拟长按坐标(x, y), 并返回是否成功。只有在长按执行完成（大约600毫秒）时脚本才会继续执行。

一般而言，只有长按过程中被其他事件中断(例如用户自行点击)才会长按失败。

## press(x, y, duration)

* `x` {number} 要按住的坐标的x值
* `y` {number} 要按住的坐标的y值
* `duration` {number} 按住时长，单位毫秒

模拟按住坐标(x, y), 并返回是否成功。只有按住操作执行完成时脚本才会继续执行。

如果按住时间过短，那么会被系统认为是点击；如果时长超过500毫秒，则认为是长按。

一般而言，只有按住过程中被其他事件中断才会操作失败。

一个连点器的例子如下：
```
//循环100次
for(var i = 0; i < 100; i++){
  //点击位置(500, 1000), 每次用时1毫秒
  press(500, 1000, 1);
}
```

## swipe(x1, y1, x2, y2, duration)

* `x1` {number} 滑动的起始坐标的x值
* `y1` {number} 滑动的起始坐标的y值
* `x2` {number} 滑动的结束坐标的x值
* `y2` {number} 滑动的结束坐标的y值
* `duration` {number} 滑动时长，单位毫秒

模拟从坐标(x1, y1)滑动到坐标(x2, y2)，并返回是否成功。只有滑动操作执行完成时脚本才会继续执行。

一般而言，只有滑动过程中被其他事件中断才会滑动失败。

## gesture(duration, [x1, y1], [x2, y2], ...)

* `duration` {number} 手势的时长
* [x, y] ... 手势滑动路径的一系列坐标

模拟手势操作。例如`gesture(1000, [0, 0], [500, 500], [500, 1000])`为模拟一个从(0, 0)到(500, 500)到(500, 100)的手势操作，时长为2秒。

## gestures([delay1, duration1, [x1, y1], [x2, y2], ...], [delay2, duration2, [x3, y3], [x4, y4], ...], ...)

同时模拟多个手势。每个手势的参数为\[delay, duration, 坐标\], delay为延迟多久(毫秒)才执行该手势；duration为手势执行时长；坐标为手势经过的点的坐标。其中delay参数可以省略，默认为0。

例如手指捏合：
```
gestures([0, 500, [800, 300], [500, 1000]],
         [0, 500, [300, 1500], [500, 1000]]);
```

# RootAutomator

> Stability: 2 - Stable

RootAutomator是一个使用root权限来模拟触摸的对象，用它可以完成触摸与多点触摸，并且这些动作的执行没有延迟。

一个脚本中最好只存在一个RootAutomator，并且保证脚本结束退出他。可以在exit事件中退出RootAutomator，例如：
```
var ra = new RootAutomator();
events.on('exit', function(){
  ra.exit();
});
//执行一些点击操作
...

```

**注意以下命令需要root权限**

## RootAutomator.tap(x, y[, id])
* `x` {number} 横坐标
* `y` {number} 纵坐标
* `id` {number} 多点触摸id，可选，默认为1，可以通过setDefaultId指定。

点击位置(x, y)。其中id是一个整数值，用于区分多点触摸，不同的id表示不同的"手指"，例如：
```
var ra = new RootAutomator();
//让"手指1"点击位置(100, 100)
ra.tap(100, 100, 1);
//让"手指2"点击位置(200, 200);
ra.tap(200, 200, 2);
ra.exit();
```
如果不需要多点触摸，则不需要id这个参数。
多点触摸通常用于手势或游戏操作，例如模拟双指捏合、双指上滑等。

某些情况下可能存在tap点击无反应的情况，这时可以用`RootAutomator.press()`函数代替。

## RootAutomator.swipe(x1, x2, y1, y2[, duration, id])
* `x1` {number} 滑动起点横坐标
* `y1` {number} 滑动起点纵坐标
* `x2` {number} 滑动终点横坐标
* `y2` {number} 滑动终点纵坐标
* `duration` {number} 滑动时长，单位毫秒，默认值为300
* `id` {number} 多点触摸id，可选，默认为1

模拟一次从(x1, y1)到(x2, y2)的时间为duration毫秒的滑动。

## RootAutomator.press(x, y, duration[, id])
* `x` {number} 横坐标
* `y` {number} 纵坐标
* `duration` {number} 按下时长
* `id` {number} 多点触摸id，可选，默认为1

模拟按下位置(x, y)，时长为duration毫秒。

## RootAutomator.longPress(x, y[\, id\])
* `x` {number} 横坐标
* `y` {number} 纵坐标
* `duration` {number} 按下时长
* `id` {number} 多点触摸id，可选，默认为1

模拟长按位置(x, y)。

以上为简单模拟触摸操作的函数。如果要模拟一些复杂的手势，需要更底层的函数。

## RootAutomator.touchDown(x, y[, id])
* `x` {number} 横坐标
* `y` {number} 纵坐标
* `id` {number} 多点触摸id，可选，默认为1

模拟手指按下位置(x, y)。

## RootAutomator.touchMove(x, y[, id])
* `x` {number} 横坐标
* `y` {number} 纵坐标
* `id` {number} 多点触摸id，可选，默认为1

模拟移动手指到位置(x, y)。

## RootAutomator.touchUp([id])
* `id` {number} 多点触摸id，可选，默认为1

模拟手指弹起。

# 使用root权限点击和滑动的简单命令

> Stability: 1 - Experimental 

注意：本章节的函数在后续版本很可能有改动！请勿过分依赖本章节函数的副作用。推荐使用`RootAutomator`代替本章节的触摸函数。

以下函数均需要root权限，可以实现任意位置的点击、滑动等。

* 这些函数通常首字母大写以表示其特殊的权限。  
* 这些函数均不返回任何值。  
* 并且，这些函数的执行是异步的、非阻塞的，在不同机型上所用的时间不同。脚本不会等待动作执行完成才继续执行。因此最好在每个函数之后加上适当的sleep来达到期望的效果。


例如:
```
Tap(100, 100);
sleep(500);
```

注意，动作的执行可能无法被停止，例如：
```
for(var i = 0; i < 100; i++){
  Tap(100, 100);
}
```
这段代码执行后可能会出现在任务管理中停止脚本后点击仍然继续的情况。
因此，强烈建议在每个动作后加上延时：
```
for(var i = 0; i < 100; i++){
  Tap(100, 100);
  sleep(500);
}
```

## Tap(x, y)
* x, y {number} 要点击的坐标。

点击位置(x, y), 您可以通过"开发者选项"开启指针位置来确定点击坐标。

## Swipe(x1, y1, x2, y2, \[duration\])
* x1, y1 {number} 滑动起点的坐标
* x2, y2 {number} 滑动终点的坐标
* duration {number} 滑动动作所用的时间

滑动。从(x1, y1)位置滑动到(x2, y2)位置。