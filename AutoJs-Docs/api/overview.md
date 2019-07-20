# 综述

Auto.js使用[JavaScript](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript)作为脚本语言，目前使用[Rhino 1.7.7.2](https://developer.mozilla.org/zh-CN/docs/Mozilla/Projects/Rhino)作为脚本引擎，支持ES5与部分ES6特性。

* 因为Auto.js是基于JavaScript的，学习Auto.js的API之前建议先学习JavaScript的基本语法和内置对象，可以使用教程前面的两个JavaScript教程链接来学习。
* 如果您想要使用TypeScript来开发，目前已经有开发者公布了一个可以把使用TypeScript进行Auto.js开发的工具，参见[Auto.js DevTools](https://github.com/pboymt/autojs-dev)。
* 如果想要在电脑而不是手机上开发Auto.js，可以使用VS Code以及相应的Auto.js插件使得在电脑上编辑的脚本能推送到手机运行，参见[Auto.js-VSCode-Extension](https://github.com/hyb1996/Auto.js-VSCode-Extension)。

本文档的章节大致上是以模块来分的，总体上可以分成"自动操作"类模块(控件操作、触摸模拟、按键模拟等)和其他类模块(设备、应用、界面等)。

"自动操作"的部分又可以大致分为基于控件和基于坐标的操作。基于坐标的操作是传统按键精灵、触摸精灵等脚本软件采用的方式，通过屏幕坐标来点击、长按指定位置模拟操作，从而到达目的。例如`click(100, 200)`, `press(100, 200, 500)`等。这种方式在游戏类脚本中比较有可行性，结合找图找色、坐标放缩功能也能达到较好的兼容性。但是，这种方式对一般软件脚本却难以达到想要的效果，而且这种方式需要安卓7.0版本以上或者root权限才能执行。所以对于一般软件脚本(例如批量添加联系人、自动提取短信验证码等等)，我们采用基于控件的模拟操作方式，结合通知事情、按键事情等达成更好的工作流。这些部分的文档参见[基于控件的操作](widgets-based-automation.html)和[基于坐标的操作](coordinates-based-automation.html)。

其他部分主要包括：
* app: 应用。启动应用，卸载应用，使用应用查看、编辑文件、访问网页，发送应用间广播等。
* console: 控制台。记录运行的日志、错误、信息等。
* device: 设备。获取设备屏幕宽高、系统版本等信息，控制设备音量、亮度等。
* engines: 脚本引擎。用于启动其他脚本。
* events: 事件与监听。按键监听，通知监听，触摸监听等。
* floaty: 悬浮窗。用于显示自定义的悬浮窗。
* files: 文件系统。文件创建、获取信息、读写。
* http: HTTP。发送HTTP请求，例如GET, POST等。
* images, colors: 图片和图色处理。截图，剪切图片，找图找色，读取保存图片等。
* keys: 按键模拟。比如音量键、Home键模拟等。
* shell: Shell命令。
* threads: 多线程支持。
* ui: UI界面。用于显示自定义的UI界面，和用户交互。

除此之外，Auto.js内置了对[Promise](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise)。