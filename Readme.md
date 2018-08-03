# Auto.js
## 简介
一个支持无障碍服务的Android平台上的JavaScript IDE，其发展目标是JsBox和Workflow。

同时有[VS Code 插件](https://github.com/hyb1996/Auto.js-VSCode-Extension)可提供基础的在桌面开发的功能。

下载地址：[酷安](http://www.coolapk.com/apk/org.autojs.autojs)

Alpha版本：[Releases](https://github.com/hyb1996/NoRootScriptDroid/releases)

官方文档：https://hyb1996.github.io/AutoJs-Docs/

### 特性
1. 由无障碍服务实现的简单易用的自动操作函数
2. 悬浮窗录制和运行
3. 更专业&强大的选择器API，提供对屏幕上的控件的寻找、遍历、获取信息、操作等。类似于Google的UI测试框架UiAutomator，您也可以把他当做移动版UI测试框架使用
4. 采用JavaScript为脚本语言，并支持代码补全、变量重命名、代码格式化、查找替换等功能，可以作为一个JavaScript IDE使用
5. 支持使用e4x编写界面，并可以将JavaScript打包为apk文件，您可以用它来开发小工具应用
6. 支持使用Root权限以提供更强大的屏幕点击、滑动、录制功能和运行shell命令。录制录制可产生js文件或二进制文件，录制动作的回放比较流畅
7. 提供截取屏幕、保存截图、图片找色、找图等函数
8. 可作为Tasker插件使用，结合Tasker可胜任日常工作流
9. 带有界面分析工具，类似Android Studio的LayoutInspector，可以分析界面层次和范围、获取界面上的控件信息

本软件与按键精灵等软件不同，主要区别是：
1. Auto.js主要以自动化、工作流为目标，更多地是方便日常生活工作，例如启动游戏时自动屏蔽通知、一键与特定联系人微信视频（知乎上出现过该问题，老人难以进行复杂的操作和子女进行微信视频）等
2. Auto.js兼容性更好。以坐标为基础的按键精灵、脚本精灵很容易出现分辨率问题，而以控件为基础的Auto.js则没有这个问题
3. Auto.js执行大部分任务不需要root权限。只有需要精确坐标点击、滑动的相关函数才需要root权限
4. Auto.js可以提供界面编写等功能，不仅仅是作为一个脚本软件而存在


### 信息
* 官方论坛： [autojs.org](http://www.autojs.org)
* 文档：可在[这里](https://hyb1996.github.io/AutoJs-Docs/)查看在线文档。目前文档仍然不完善。
* 示例：可在[这里](https://github.com/hyb1996/NoRootScriptDroid/tree/master/app/src/main/assets/sample)查看一些示例，或者直接在应用内查看和运行。

### 截图

![screen-capture2](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss02.png)

![screen-capture5](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss05.png)

![screen-capture5](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss07.png)

![screen-capture5](https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/screen-captures/ss08.png)

## License
基于[Mozilla Public License Version 2.0](https://github.com/hyb1996/NoRootScriptDroid/blob/master/LICENSE.md)并附加以下条款：
* **非商业性使用** — 不得将此项目及其衍生的项目的源代码和二进制产品用于任何商业和盈利用途
