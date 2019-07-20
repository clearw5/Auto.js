# Engines

> Stability: 2 - Stable

engines模块包含了一些与脚本环境、脚本运行、脚本引擎有关的函数，包括运行其他脚本，关闭脚本等。

例如，获取脚本所在目录：
```
toast(engines.myEngine().cwd());
```

## engines.execScript(name, script[, config])
* `name` {string} 要运行的脚本名称。这个名称和文件名称无关，只是在任务管理中显示的名称。
* `script` {string} 要运行的脚本内容。
* `config` {Object} 运行配置项
    * `delay` {number} 延迟执行的毫秒数，默认为0
    * `loopTimes` {number} 循环运行次数，默认为1。0为无限循环。
    * `interval` {number} 循环运行时两次运行之间的时间间隔，默认为0
    * `path` {Array} | {string} 指定脚本运行的目录。这些路径会用于require时寻找模块文件。

在新的脚本环境中运行脚本script。返回一个[ScriptExectuion](#engines_scriptexecution)对象。

所谓新的脚本环境，指定是，脚本中的变量和原脚本的变量是不共享的，并且，脚本会在新的线程中运行。

最简单的例子如下：
```
engines.execScript("hello world", "toast('hello world')");
```

如果要循环运行，则：
```
//每隔3秒运行一次脚本，循环10次
engines.execScript("hello world", "toast('hello world')", {
    loopTimes: 10,
    interval: 3000
});
```

用字符串来编写脚本非常不方便，可以结合 `Function.toString()`的方法来执行特定函数:

```
function helloWorld(){
    //注意，这里的变量和脚本主体的变量并不共享
    toast("hello world");
}
engines.execScript("hello world", "helloWorld();\n" + helloWorld.toString());
```

如果要传递变量，则可以把这些封装成一个函数：
```
function exec(action, args){
    args = args || {};
    engines.execScript(action.name, action + "(" + JSON.stringify(args) + ");\n" + action.toString());
}

//要执行的函数，是一个简单的加法
function add(args){
    toast(args.a + args.b);
}

//在新的脚本环境中执行 1 + 2
exec(add, {a: 1, b:2});
```

## engines.execScriptFile(path[, config])
* `path` {string} 要运行的脚本路径。
* `config` {Object} 运行配置项
    * `delay` {number} 延迟执行的毫秒数，默认为0
    * `loopTimes` {number} 循环运行次数，默认为1。0为无限循环。
    * `interval` {number} 循环运行时两次运行之间的时间间隔，默认为0
    * `path` {Array} | {string} 指定脚本运行的目录。这些路径会用于require时寻找模块文件。

在新的脚本环境中运行脚本文件path。返回一个[ScriptExecution](#ScriptExecution)对象。

```
engines.execScriptFile("/sdcard/脚本/1.js");
```

## engines.execAutoFile(path[, config])
* `path` {string} 要运行的录制文件路径。
* `config` {Object} 运行配置项
    * `delay` {number} 延迟执行的毫秒数，默认为0
    * `loopTimes` {number} 循环运行次数，默认为1。0为无限循环。
    * `interval` {number} 循环运行时两次运行之间的时间间隔，默认为0
    * `path` {Array} | {string} 指定脚本运行的目录。这些路径会用于require时寻找模块文件。

在新的脚本环境中运行录制文件path。返回一个[ScriptExecution](#ScriptExecution)对象。

```
engines.execAutoFile("/sdcard/脚本/1.auto");
```

## engines.stopAll()

停止所有正在运行的脚本。包括当前脚本自身。

## engines.stopAllAndToast()

停止所有正在运行的脚本并显示停止的脚本数量。包括当前脚本自身。

## engines.myEngine()

返回当前脚本的脚本引擎对象([ScriptEngine](#engines_scriptengine))

**[v4.1.0新增]**
特别的，该对象可以通过`execArgv`来获取他的运行参数，包括外部参数、intent等。例如：
```
log(engines.myEngine().execArgv);
```

普通脚本的运行参数通常为空，通过定时任务的广播启动的则可以获取到启动的intent。

## engines.all()
* 返回 {Array}

返回当前所有正在运行的脚本的脚本引擎[ScriptEngine](#engines_scriptengine)的数组。

```
log(engines.all());
```

# ScriptExecution

执行脚本时返回的对象，可以通过他获取执行的引擎、配置等，也可以停止这个执行。

要停止这个脚本的执行，使用`exectuion.getEngine().forceStop()`.

## ScriptExecution.getEngine()

返回执行该脚本的脚本引擎对象([ScriptEngine](#engines_scriptengine))

## ScriptExecution.getConfig()

返回该脚本的运行配置([ScriptConfig](#engines_scriptconfig))

# ScriptEngine

脚本引擎对象。

## ScriptEngine.forceStop()

停止脚本引擎的执行。

## ScriptEngine.cwd()
* 返回 {string}

返回脚本执行的路径。对于一个脚本文件而言为这个脚本所在的文件夹；对于其他脚本，例如字符串脚本，则为`null`或者执行时的设置值。

## ScriptEngine.getSource()
* 返回 [ScriptSource](#engines_scriptsource)

返回当前脚本引擎正在执行的脚本对象。

```
log(engines.myEngine().getSource());
```

## ScriptEngine.emit(eventName[, ...args])
* `eventName` {string} 事件名称
* `...args` {any} 事件参数

向该脚本引擎发送一个事件，该事件可以在该脚本引擎对应的脚本的events模块监听到并在脚本主线程执行事件处理。

例如脚本receiver.js的内容如下：

```
//监听say事件
events.on("say", function(words){
    toastLog(words);
});
//保持脚本运行
setInterval(()=>{}, 1000);
```

同一目录另一脚本可以启动他并发送该事件：
```
//运行脚本
var e = engines.execScriptFile("./receiver.js");
//等待脚本启动
sleep(2000);
//向该脚本发送事件
e.getEngine().emit("say", "你好");
```

# ScriptConfig
脚本执行时的配置。

## delay
* {number}

延迟执行的毫秒数

## interval
* {number}

循环运行时两次运行之间的时间间隔

## loopTimes
* {number}

循环运行次数

## getPath()
* 返回 {Array}

返回一个字符串数组表示脚本运行时模块寻找的路径。



