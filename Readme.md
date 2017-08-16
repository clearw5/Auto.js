# Auto.js
## 简介
一个主要由无障碍服务实现的**不需要Root权限**的类似按键精灵的自动操作软件，可以实现自动点击、滑动、输入文字、打开应用等。

同时有[Sublime Text 插件](https://github.com/hyb1996/AutoJs-Sublime-Plugin)可提供基础的在桌面开发的功能。

下载地址：[酷安](http://www.coolapk.com/apk/com.stardust.scriptdroid)

### 特性
* 简单易用的自动操作函数
* 悬浮窗录制和运行
* 更专业&强大的选择器API，提供对屏幕上的控件的寻找、遍历、获取信息、操作等。类似于Google的UI测试框架UiAutomator，您也可以把他当做移动版UI测试框架使用
* 采用JavaScript为脚本语言，支持简单的代码补全。您也可以把他当作简便的JavaScript IDE使用
* 带有界面分析工具，类似Android Studio的LayoutInspector，可以分析界面层次和范围、获取界面上的控件信息
* 支持使用Root权限以提供更强大的屏幕点击、滑动、录制功能和运行shell命令。录制录制可产生js文件或二进制文件，录制动作的回放比较流畅
* 提供截取屏幕、保存截图、图片找色等函数，可进行简单的游戏脚本制作；未来将加入找图功能
* 方便地文件处理API，以及更多日常工具函数
* 可以用e4x编写简单的界面，并且未来将加入打包为独立应用功能，可用于制作简单的应用
* 可作为Tasker插件使用，结合Tasker可胜任日常工作流

与脚本精灵、按键精灵等软件的区别是：
* Auto.js主要以自动化、工作流为目标，更多地是方便日常生活工作，例如启动游戏时自动屏蔽通知、一键与特定联系人微信视频（知乎上出现过该问题，老人难以进行复杂的操作和子女进行微信视频）等
* Auto.js兼容性更好。以坐标为基础的按键精灵、脚本精灵很容易出现分辨率问题，而以控件为基础的Auto.js则没有这个问题
* Auto.js执行大部分任务不需要root权限。只有需要精确坐标点击、滑动的相关函数才需要root权限

尽管如此，Auto.js的大部分用户仍然是用来点赞、签到、刷游戏的:)

### 文档
可在[这里](https://github.com/hyb1996/NoRootScriptDroid/tree/master/app/src/main/assets/help/documentation)查看在线文档，或者在应用内帮助页面查看。

目前文档很不完善。

### 示例
可在[这里](https://github.com/hyb1996/NoRootScriptDroid/tree/master/app/src/main/assets/sample)查看一些示例，或者直接在应用内查看和运行。

### 截图

![screen-capture1](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss01.png)

![screen-capture2](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss02.png)

![screen-capture3](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss03.png)

![screen-capture4](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss04.png)

![screen-capture5](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss05.png)

![screen-capture5](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss06.png)

### Todo

* 脚本社区或脚本市场
* 更方便地悬浮窗编辑、运行脚本
* 更方便地脚本编辑，在编辑器界面可搜索、查看函数
* 智能生成选择器代码
* 更详细的文档和向导(Developer Guide)
* 全新的界面
* 找图功能以便更好地支持游戏脚本的制作
* 脚本打包为独立应用功能

## License
基于[Mozilla Public License Version 2.0](https://github.com/hyb1996/NoRootScriptDroid/blob/master/LICENSE.md)并附加以下条款：
* **非商业性使用** — 不得将此项目及其衍生的项目的源代码和二进制产品用于任何商业和盈利用途
