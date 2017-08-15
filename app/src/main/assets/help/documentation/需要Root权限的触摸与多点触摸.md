# RootAutomator

RootAutomator是一个使用Root权限来模拟触摸的对象，用它可以完成触摸与多点触摸，并且这些动作的执行没有延迟。

构造RootAutomator需要一个context参数。
```
var ra = RootAutomator(context);
```

### RootAutomator.tap(x, y\[, id\])
* x \<Number\> 横坐标
* y \<Number\> 纵坐标
* id \<Number\> 多点触摸id，可选，默认为1，可以通过setDefaultId指定。

点击位置(x, y)。其中id是一个整数值，用于区分多点触摸，不同的id表示不同的"手指"，例如：
```
var ra = RootAutomator(context);
//让"手指1"点击位置(100, 100)
ra.tap(100, 100, 1);
//让"手指2"点击位置(200, 200);
ra.tap(200, 200, 2);
ra.exit();
```
如果不需要多点触摸，则不需要id这个参数。
多点触摸通常用于手势或游戏操作，例如模拟双指捏合、双指上滑等。
### RootAutomator.swipe(x1, x2, y1, y2\[, duration, id\])
* x1 \<Number\> 滑动起点横坐标
* y1 \<Number\> 滑动起点纵坐标
* x2 \<Number\> 滑动终点横坐标
* y2 \<Number\> 滑动终点纵坐标
* duration \<Number\> 滑动时长，单位毫秒，默认值为300
* id \<Number\> 多点触摸id，可选，默认为1

模拟一次从(x1, y1)到(x2, y2)的时间为duration毫秒的滑动。

### RootAutomator.press(x, y, duration[\, id\])
* x \<Number\> 横坐标
* y \<Number\> 纵坐标
* duration \<Number\> 按下时长
* id \<Number\> 多点触摸id，可选，默认为1

模拟按下位置(x, y)，时长为duration毫秒。

### RootAutomator.longPress(x, y[\, id\])
* x \<Number\> 横坐标
* y \<Number\> 纵坐标
* duration \<Number\> 按下时长
* id \<Number\> 多点触摸id，可选，默认为1

模拟长按位置(x, y)。

以上为简单模拟触摸操作的函数。如果要模拟一些复杂的手势，需要更底层的函数。

### RootAutomator.touchDown(x, y[\, id\])
* x \<Number\> 横坐标
* y \<Number\> 纵坐标
* id \<Number\> 多点触摸id，可选，默认为1

模拟手指按下位置(x, y)。

### RootAutomator.touchMove(x, y[\, id\])
* x \<Number\> 横坐标
* y \<Number\> 纵坐标
* id \<Number\> 多点触摸id，可选，默认为1

模拟移动手指到位置(x, y)。

### RootAutomator.touchUp(\[id\])
* id \<Number\> 多点触摸id，可选，默认为1

模拟手指弹起。

