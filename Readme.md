# Auto.js
## 简介
一个支持无障碍服务的Android平台上的JavaScript IDE，其发展目标是~JsBox和Workflow~ 但是因为灰产原因原作者 @[hyb1996](https://github.com/hyb1996) 已经选择闭源开发 AutoJS Pro。

同时有[VS Code 插件](https://github.com/hyb1996/Auto.js-VSCode-Extension)可提供基础的在桌面开发的功能。本修改版能继续使用原作者开发的插件，个人开发脚本的习惯不依赖于插件，所以没有开发新的插件。

历史版本下载和更新日志：[Releases](https://github.com/TonyJiangWJ/Auto.js/releases)

官方文档：https://hyb1996.github.io/AutoJs-Docs/

### 本项目所做的修改包括但不限于如下内容

- 修复了大量内存泄露问题，持续运行大量脚本后也能保持稳定的较低内存占用率。但是部分情况还是会触发native异常导致闪退，能力有限无法修复。
- 变更默认包名为 `org.autojs.autojs.modify` 打包为64位和32位，修复对Android12的支持，目前MIUI13可能存在一些问题。
- 替换了定时任务的调度代码增加了WorkManager和AlarmManager的选项，默认的Android-job已弃用。
- 修改了截图权限逻辑，多个脚本同时运行时可以共享AutoJS的截图权限而且不会互相抢占。
- 更新opencv版本为4.5.5 支持SIFT找图等特性。
- 更新了内置rhino版本为1.7.14 支持字符串模板等JS特性。
- 增加PaddleOCR 封装为 `$ocr` 具体使用见示例文件 无文档。目前存在低概率的模型初始化失败导致无法进行文字识别的问题，跟踪修复中。
- 增加了tess-two 使用可以参考 [TesserOcrUtil](https://github.com/TonyJiangWJ/AutoScriptBase/blob/master/lib/prototype/TesserOcrUtil.js)，需要训练好的 `traineddata` 进行支持。使用比较麻烦 建议直接使用PaddleOCR。
- 增加p7zip 提交作者为 @[Aioure](https://github.com/Aioure) 封装为 `$zip` ，具体使用见示例文件 无文档。
- 增加了tts支持 提交作者为 @[syhyz](https://github.com/syhyz) 封装为 `$speech` ，具体使用见示例文件 无文档。
- 重新整理了部分代码结构，更新了gradle到7.x, 更新了其他依赖包版本不详细罗列。
- 修复了其他的不痛不痒的缺陷。
- 如果脚本需要防止类似淘宝的无障碍检测，请使用 `AutoJs.fakeIdlefish`。可以规避无障碍检测，将包名直接改成闲鱼的包名，加了个.x的后缀 `com.taobao.idlefish.x`。代码分支为 [fake_idlefish](https://github.com/TonyJiangWJ/Auto.js/tree/fake_idlefish)

### 不支持的功能包括但不限于

- 脚本打包功能因为没有开发对应插件，因此无法使用打包功能。[#4](https://github.com/TonyJiangWJ/Auto.js/issues/4)
- 内置编辑器为原版。说实在并不好用，后续有想法进行更新替换，但是目前没有着手开发，所以可能存在一些问题并未修复。

## 声明

- 如果是别的地方过来的，建议使用他们的版本，而不是使用这个项目，你所使用的脚本可能无法兼容。本项目主要是针对我自己的脚本，如蚂蚁森林和蚂蚁庄园等，所以不太会去适应所有开发者。
- 第一我不是做安卓开发的所以能力有限，第二是我本人时间也不充裕，所有的优化都是为了能够运行稳定，而不是增加新功能。
- 如果真拿来开发脚本的话 很多功能都是欠缺的，比如编辑器，比如脚本打包，比如USB连接VS插件等等。
- 当然反馈的这些问题我也会尝试去修复，也欢迎提交PR，不过我有代码洁癖请保持commit简洁明晰，一个PR一个功能，如果不能满足的话建议还是不要提交了，免得浪费你我时间。
- 如果为了更丰富的功能 还是建议使用其他开源版本 比如AutoX。
- 另外如果引用我这边的代码，请在引用代码的类或者方法名的注释中注明原作者信息以及来源是本项目。

## 关于编译问题的说明

- 默认gradle设置了代理，请自行修改关闭或者本机开启代理。文件为 `gradle.properties`，编译所需JDK版本需要大于等于JDK11，使用Android Studio内置的OpenJDK即可。
- 代码是没问题的，针对原作者的开源协议代码纯开源的毫无保留。具体编译打包有安卓开发基础就行，毕竟我这个外行也能打包编译。
- 编译问题其实我真的不想管，我自己也不是正经搞安卓的也是一步一步摸索过来的，请自行探索或者学一下这些基础知识，而不是拿到日志了，自己不分析，就跟伸手党一样问别人应该怎么做。你我都是陌生人，你能不能编译不是我的义务，更何况我甚至不知道你是否是拿来开发灰产，也就是原作者选择闭源开发新版本AutoJS Pro的原因。
- 另外置顶ISSUE也说的很明确，单纯自用。个人时间有限，我不想花时间在这些类似于教学的方面，对我来说没有什么益处。

## 以下为原始特性描述等信息

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
