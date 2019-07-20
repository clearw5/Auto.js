# 关于本文档

<!-- type=misc -->

本文档为Auto.js的文档，解释了Auto.js各个模块的API的使用方法、作用和例子。

文档借助Node.js的文档构建工具生成，并在github上开源(https://github.com/hyb1996/AutoJs-Docs )，目前由开发者维护。

## API稳定性

由于Auto.js处于活跃的更新和开发状态，API可能随时有变动，我们用Stability来标记模块、函数的稳定性。这些标记包括：

```txt
Stability: 0 - Deprecated

弃用的函数、模块或特性，在未来的更新中将很快会被移除或更改。应该在脚本中移除对这些函数的使用，以免后续出现意料之外的问题。
```

```txt
Stability: 1 - Experimental

实验性的函数、模块或特性，在未来的更新中可能会更改或移除。应该谨慎使用这些函数或模块，或者仅用作临时或试验用途。
```

```txt
Stability: 2 - Stable

稳定的函数、模块或特性，在未来的更新中这些模块已有的函数一般不会被更改，会保证后向兼容性。
```

## 如何阅读本文档

先看一个例子，下面是[基于控件的操作模拟](coordinates-based-automation.html)的章节中input函数的部分说明。

## input([i, ]text)
* `i` {number} 表示要输入的为第i + 1个输入框
* `text` {string} 要输入的文本


input表示函数名，括号内的`[i, ]text`为函数的参数。下面是参数列表，"number"表示参数i的类型为数值，"string"表示参数text的类型为字符串。

例如input(1, "啦啦啦")，执行这个语句会在屏幕上的第2个输入框处输入"啦啦啦"。

方括号[ ]表示参数为可选参数。也就是说，可以省略i直接调用input。例如input("嘿嘿嘿")，按照文档，这个语句会在屏幕上所有输入框输入"嘿嘿嘿"。

调用有可选参数的函数时请不要写上方括号。

我们再看第二个例子。图片和图色处理中detectsColor函数的部分说明。

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

同样地，`[, threshold = 16, algorithm = "rgb"]`为可选参数，并且，等于号=后面的值为参数的默认值。也就是如果不指定该参数，则该参数将会为这个值。

例如 `images.detectsColor(captureScreen(), "#112233", 100, 200)` 相当于 `images.detectsColor(captureScreen(), "#112233", 100, 200, 16, "rgb")`，
而`images.detectsColor(captureScreen(), "#112233", 100, 200, 64)` 相当于`images.detectsColor(captureScreen(), "#112233", 100, 200, 64, "rgb")`。

调用有可选参数及默认值的函数时请不要写上方括号和等于号。