# Auto.js

[README in English](https://github.com/hyb1996/Auto.js/blob/master/README-en.md)

# 简介

Auto.js (Pro)是Android上基于JavaScript的面向自动化、工作流、小工具、小应用的代码开发平台。自2017年1月诞生以来，已从1.0版本发展到最新的9.0版本。

## 软件下载

官网: https://pro.autojs.org

官方文档: https://pro.autojs.org/docs

官方博客: https://blog.autojs.org

## Auto.js Pro可以做什么

使用JavaScript和Node.js实现你想实现的一切。

### 开发小应用

Auto.js Pro本身具有开发完整应用的功能，**可由JavaScript项目生成独立分发的apk包**。你既可以沿用Web知识开发界面，也可以使用Auto.js提供的控件开发简单的界面，甚至可以使用原生控件开发优美界面。

Auto.js Pro可安装npm包、加载dex、jar、动态库等，连接Android/Node.js的生态，更让Auto.js不仅局限于小应用，使用Auto.js开发坦克大战游戏、愤怒的小鸟游戏、http服务器等的例子也不少见。

![官方界面Demo](https://pro.autojs.org/docs/assets/m3.49f15e97.jpg)

### 开发自动化、工作流小工具

Auto.js Pro可以将日程繁琐重复的工作用JavaScript代码实现，让机器代替你的操作。代码可以在获得你的授权的情况下，发起网络请求、操作手机、处理文件等，比如定时给恋人发送消息、在早晨解锁屏幕时自动签到、批量音视频文件处理等。

结合Tasker等自动化软件，Auto.js可以提供高度定制的自动化任务提高你的工作、生活效率。

![使用Auto.js Pro开发的律己](https://pro.autojs.org/docs/assets/lvji.5ba37521.jpg)

### 学习JavaScript与验证想法

Auto.js Pro本身带有多Tab编辑器、调试器等专业开发工具，也允许你使用VSCode来编写和运行代码。对于有兴趣学习编程的学生来说是一个不错的编程学习工具，代码不再仅局限于理论；对于想从事编程行业的人也是不错的低门槛入门工具，你可以从Auto.js走向Node.js全栈开发和Android开发；对于成熟的开发人员来说也是随手验证想法，摸鱼偷懒时的有趣玩具，也是辅助平时开发的好工具。

## Auto.js Pro对比其他版本有什么优势

* Node.js引擎

Pro 9新增Node.js 16.x引擎，性能是原引擎的100倍以上，支持ES2021

* 全分辨率找图

全分辨率找图（特征匹配）支持，另外极大提升了找色效率、优化截图性能

* 内置OCR

内置PaddleOCR并优化了准确率，同时提供速度更快的MLKit OCR插件
    
* 插件商店与免安装

插件打包时可被合并到apk，无需再单独安装；插件商店上线，多个插件任你选择

* 加密增强

Node.js引擎加密目前未被还原代码，即将推出在线优化进一步增强加密

* 完美VSCode调试体验

远程单步调试、自动补全，9.3版本更全面优化了文件同步效率、管理手机文件等功能

* Npm生态支持

可安装和使用npm包，包括ws、express、koa等200万个npm包

* 代码商店

近千个免费在线代码与项目随意下载，也可与其他用户分享你

* 打包自定义

打包时可自定义签名、CPU架构、启动图、权限，优化应用大小，混淆组件等

* React/Vue/Web

官方支持使用web编写界面，甚至可以使用React/Vue等框架，并提供了web交互API

* 多Tab编辑器工作区

多Tab文件编辑、树状文件管理，编辑器基于LSP智能补全、语法错误提示等

* UI可视化设计

由浩然实现的可视化UI设计，为小白设计UI提供了更方便简单的设计界面

* API增强

新增WebSocket、数据库、原生界面、任务、设置、OCR等多个模块，无障碍截图、切换输入法、adb权限执行命令等多个API

* Bug修复与优化

3年200个版本，近500个Bug修复，近200个优化，200多个新功能，还在用千疮百孔的免费版吗？

* 更多新特性

参见更新日志，更多功能持续更新中

## Auto.js Pro不能做什么

虽然Auto.js Pro无所不能，但不能用于非法游戏外挂、读写内存、黑灰产等违法违规行为。

* Auto.js Pro官方永久不会提供读写其他程序内存的功能。
* Auto.js Pro官方永久不会提供抓取和修改其他应用网络数据的功能。
* Auto.js Pro不能用于根据有关法律法规、有关部门条例文书、有关法院判决判例等相关规定不能使用的其他情形。

## 开源说明

本仓库为Auto.js 4.1的开源代码，从Auto.js Pro开始不再开源。

为了避免之前的开源代码继续传播，现删除本仓库所有源代码。

## License
基于[Mozilla Public License Version 2.0](https://github.com/hyb1996/NoRootScriptDroid/blob/master/LICENSE.md)并附加以下条款：
* **非商业性使用** — 不得将此项目及其衍生的项目的源代码和二进制产品用于任何商业和盈利用途
