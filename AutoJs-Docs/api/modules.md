# module (模块)

> Stability: 2 - Stable

Auto.js 有一个简单的模块加载系统。 在 Auto.js 中，文件和模块是一一对应的（每个文件被视为一个独立的模块）。

例子，假设有一个名为 foo.js 的文件：
```
var circle = require('circle.js');
console.log("半径为 4 的圆的面积是 %d", circle.area(4));
```
在第一行中，foo.js 加载了同一目录下的 circle.js 模块。

circle.js 文件的内容为：
```
const PI = Math.PI;

var circle = {};

circle.area = function (r) {
  return PI * r * r;
};

circle.circumference = (r) => 2 * PI * r;

module.exports = circle;
```
circle.js 模块导出了 area() 和 circumference() 两个函数。 通过在特殊的 exports 对象上指定额外的属性，函数和对象可以被添加到模块的根部。

模块内的本地变量是私有的。 在这个例子中，变量 PI 是 circle.js 私有的，不会影响到加载他的脚本的变量环境。

module.exports属性可以被赋予一个新的值（例如函数或对象）。

如下，bar.js 会用到 square 模块，square 导出一个构造函数：
```
const square = require('square.js');
const mySquare = square(2);
console.log("正方形的面积是 %d", mySquare.area());
square 模块定义在 square.js 中：

// 赋值给 `exports` 不会修改模块，必须使用 `module.exports`
module.exports = function(width) {
  return {
    area: () => width ** 2
  };
};
```
