shell即Unix Shell，在类Unix系统提供与操作系统交互的一系列命令。

很多程序可以用来执行shell命令，例如终端模拟器。

在Auto.js大致等同于用adb执行命令"adb shell"。其实现包括两种方式：

* 通过`java.lang.Runtime.exec`执行(shell, Tap, Home等函数)
* 通过内嵌终端模拟器执行(RootAutomator, Shell等对象)

# shell函数

> Stability: 2 - Stable

## shell(cmd[, root])
* cmd {string} 要执行的命令
* root {Boolean} 是否以root权限运行，默认为false。

一次性执行命令cmd, 并返回命令的执行结果。返回对象的其属性如下:
* code {number} 返回码。执行成功时为0，失败时为非0的数字。
* result {string} 运行结果(stdout输出结果)
* error {string} 运行的错误信息(stderr输出结果)。例如执行需要root权限的命令但没有授予root权限会返回错误信息"Permission denied"。
    
示例(强制停止微信) ： 
```
var result = shell("am force-stop com.tencent.mm", true);
log(result);
console.show();
if(result.code == 0){
  toast("执行成功");
}else{
  toast("执行失败！请到控制台查看错误信息");
}
```

# Shell

> Stability: 2 - Stable

shell函数通过用来一次性执行单条命令并获取结果。如果有多条命令需要执行，用Shell对象的效率更高。这是因为，每次运行shell函数都会打开一个单独的shell进程并在运行结束后关闭他，这个过程需要一定的时间；而Shell对象自始至终使用同一个shell进程。

## new Shell(root)
* root {Boolean} 是否以root权限运行一个shell进程，默认为false。这将会影响其后使用该Shell对象执行的命令的权限

Shell对象的"构造函数"。
```
var sh = new Shell(true);
//强制停止微信
sh.exec("am force-stop com.tencent.mm");
sh.exit();
```

## Shell.exec(cmd)
* `cmd` {string} 要执行的命令

执行命令cmd。该函数不会返回任何值。

注意，命令执行是"异步"的、非阻塞的。也就是不会等待命令完成后才继续向下执行。

尽管这样的设计使用起来有很多不便之处，但受限于终端模拟器，暂时没有解决方式；如果后续能找到解决方案，则将提供`Shell.execAndWaitFor`函数。

## Shell.exit()

直接退出shell。正在执行的命令会被强制退出。

## Shell.exitAndWaitFor()

执行"exit"命令并等待执行命令执行完成、退出shell。

此函数会执行exit命令来正常退出shell。

## Shell.setCallback(callback)
* callback {Object} 回调函数

设置该Shell的回调函数，以便监听Shell的输出。可以包括以下属性：
* onOutput {Function} 每当shell有新的输出时便会调用该函数。其参数是一个字符串。
* onNewLine {Function} 每当shell有新的一行输出时便会调用该函数。其参数是一个字符串(不包括最后的换行符)。

例如:
```
var sh = new Shell();
sh.setCallback({
	onNewLine: function(line){
		//有新的一行输出时打印到控制台
		log(line);
	}
})
while(true){
	//循环输入命令
	var cmd = dialogs.rawInput("请输入要执行的命令，输入exit退出");
	if(cmd == "exit"){
		break;
	}
	//执行命令
	sh.exec(cmd);
}
sh.exit();
```

# 附录: shell命令简介

以下关于shell命令的资料来自[AndroidStudio用户指南：Shell命令](https://developer.android.com/studio/command-line/adb.html#shellcommands)。

## am命令

am命令即Activity Manager命令，用于管理应用程序活动、服务等。

**以下命令均以"am "开头，例如`shell('am start -p com.tencent.mm');`(启动微信)**

### start [options] intent  
启动 intent 指定的 Activity(应用程序活动)。  
请参阅 [intent 参数的规范](#shell_intent)。    

选项包括：

* -D：启用调试。
* -W：等待启动完成。
* --start-profiler file：启动分析器并将结果发送到 file。
* -P file：类似于 --start-profiler，但当应用进入空闲状态时分析停止。
* -R count：重复 Activity 启动 count 次数。在每次重复前，将完成顶部 Activity。
* -S：启动 Activity 前强行停止目标应用。
* --opengl-trace：启用 OpenGL 函数的跟踪。
* --user user_id | current：指定要作为哪个用户运行；如果未指定，则作为当前用户运行。

### startservice [options] intent
启动 intent 指定的 Service(服务)。  
请参阅 [intent 参数的规范](#shell_intent)。  
选项包括：

* --user user_id | current：指定要作为哪个用户运行；如果未指定，则作为当前用户运行。

### force-stop package	
强行停止与 package（[应用包名](#应用包名)）关联的所有应用。  

### kill [options] package	
终止与 package（[应用包名](#应用包名)）关联的所有进程。此命令仅终止可安全终止且不会影响用户体验的进程。  
选项包括：

* --user user_id | all | current：指定将终止其进程的用户；如果未指定，则终止所有用户的进程。

### kill-all
终止所有后台进程。

### broadcast [options] intent	
发出广播 intent。
请参阅 [intent 参数的规范](#shell_intent)。     

选项包括：

* [--user user_id | all | current]：指定要发送到的用户；如果未指定，则发送到所有用户。

### instrument [options] component	  
使用 Instrumentation 实例启动监控。通常，目标 component 是表单 test_package/runner_class。  
选项包括：

* -r：输出原始结果（否则对 report_key_streamresult 进行解码）。与 [-e perf true] 结合使用以生成性能测量的原始输出。
* -e name value：将参数 name 设为 value。对于测试运行器，通用表单为 -e testrunner_flag value[,value...]。
* -p file：将分析数据写入 file。
* -w：先等待仪器完成，然后再返回。测试运行器需要使用此选项。
* --no-window-animation：运行时关闭窗口动画。
* --user user_id | current：指定仪器在哪个用户中运行；如果未指定，则在当前用户中运行。
* profile start process file	启动 process 的分析器，将结果写入 file。
* profile stop process	停止 process 的分析器。

### dumpheap [options] process file	
转储 process 的堆，写入 file。

选项包括：

* --user [user_id|current]：提供进程名称时，指定要转储的进程用户；如果未指定，则使用当前用户。
* -n：转储原生堆，而非托管堆。
* set-debug-app [options] package	将应用 package 设为调试。

选项包括：

* -w：应用启动时等待调试程序。
* --persistent：保留此值。
* clear-debug-app	使用 set-debug-app 清除以前针对调试用途设置的软件包。

### monitor [options]	启动对崩溃或 ANR 的监控。  
选项包括：

* --gdb：在崩溃/ANR 时在给定端口上启动 gdbserv。
### screen-compat {on|off} package	
控制 package 的屏幕兼容性模式。

### display-size [reset|widthxheight]	
替换模拟器/设备显示尺寸。此命令对于在不同尺寸的屏幕上测试您的应用非常有用，它支持使用大屏设备模仿小屏幕分辨率（反之亦然）。  
示例：
```
shell("am display-size 1280x800", true);
	
```
### display-density dpi 
替换模拟器/设备显示密度。此命令对于在不同密度的屏幕上测试您的应用非常有用，它支持使用低密度屏幕在高密度环境环境上进行测试（反之亦然）。  
示例：
```
shell("am display-density 480", true);
```
### to-uri intent	
将给定的 intent 规范以 URI 的形式输出。
请参阅  [intent 参数的规范](#shell_intent)。

### to-intent-uri intent	
将给定的 intent 规范以 intent:URI 的形式输出。
请参阅 intent 参数的规范。

### intent参数的规范
对于采用 intent 参数的 am 命令，您可以使用以下选项指定 intent：

* -a action  
指定 intent 操作，如“android.intent.action.VIEW”。此指定只能声明一次。
* -d data_uri  
指定 intent 数据 URI，如“content://contacts/people/1”。此指定只能声明一次。
* -t mime_type  
指定 intent MIME 类型，如“image/png”。此指定只能声明一次。
* -c category  
指定 intent 类别，如“android.intent.category.APP_CONTACTS”。
* -n component  
指定带有软件包名称前缀的组件名称以创建显式 intent，如“com.example.app/.ExampleActivity”。
* -f flags  
将标志添加到 setFlags() 支持的 intent。
* --esn extra_key  
添加一个 null extra。URI intent 不支持此选项。
* -e|--es extra_key extra_string_value  
添加字符串数据作为键值对。
* --ez extra_key extra_boolean_value  
添加布尔型数据作为键值对。
* --ei extra_key extra_int_value  
添加整数型数据作为键值对。
* --el extra_key extra_long_value  
添加长整型数据作为键值对。
* --ef extra_key extra_float_value  
添加浮点型数据作为键值对。
* --eu extra_key extra_uri_value  
添加 URI 数据作为键值对。
* --ecn extra_key extra_component_name_value  
添加组件名称，将其作为 ComponentName 对象进行转换和传递。
* --eia extra_key extra_int_value[,extra_int_value...]  
添加整数数组。
* --ela extra_key extra_long_value[,extra_long_value...]  
添加长整型数组。
* --efa extra_key extra_float_value[,extra_float_value...]  
添加浮点型数组。
* --grant-read-uri-permission  
包含标志 FLAG_GRANT_READ_URI_PERMISSION。
* --grant-write-uri-permission  
包含标志 FLAG_GRANT_WRITE_URI_PERMISSION。
* --debug-log-resolution  
包含标志 FLAG_DEBUG_LOG_RESOLUTION。
* --exclude-stopped-packages  
包含标志 FLAG_EXCLUDE_STOPPED_PACKAGES。
* --include-stopped-packages  
包含标志 FLAG_INCLUDE_STOPPED_PACKAGES。
* --activity-brought-to-front  
包含标志 FLAG_ACTIVITY_BROUGHT_TO_FRONT。
* --activity-clear-top  
包含标志 FLAG_ACTIVITY_CLEAR_TOP。
* --activity-clear-when-task-reset  
包含标志 FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET。
* --activity-exclude-from-recents  
包含标志 FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS。
* --activity-launched-from-history  
包含标志 FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY。
* --activity-multiple-task  
包含标志 FLAG_ACTIVITY_MULTIPLE_TASK。
* --activity-no-animation  
包含标志 FLAG_ACTIVITY_NO_ANIMATION。
* --activity-no-history  
包含标志 FLAG_ACTIVITY_NO_HISTORY。
* --activity-no-user-action  
包含标志 FLAG_ACTIVITY_NO_USER_ACTION。
* --activity-previous-is-top  
包含标志 FLAG_ACTIVITY_PREVIOUS_IS_TOP。
* --activity-reorder-to-front  
包含标志 FLAG_ACTIVITY_REORDER_TO_FRONT。
* --activity-reset-task-if-needed  
包含标志 FLAG_ACTIVITY_RESET_TASK_IF_NEEDED。
* --activity-single-top  
包含标志 FLAG_ACTIVITY_SINGLE_TOP。
* --activity-clear-task  
包含标志 FLAG_ACTIVITY_CLEAR_TASK。
* --activity-task-on-home  
包含标志 FLAG_ACTIVITY_TASK_ON_HOME。
* --receiver-registered-only  
包含标志 FLAG_RECEIVER_REGISTERED_ONLY。
* --receiver-replace-pending  
包含标志 FLAG_RECEIVER_REPLACE_PENDING。
* --selector  
需要使用 -d 和 -t 选项以设置 intent 数据和类型。
#### URI component package
如果不受上述某一选项的限制，您可以直接指定 URI、软件包名称和组件名称。当参数不受限制时，如果参数包含一个“:”（冒号），则此工具假定参数是一个 URI；如果参数包含一个“/”（正斜杠），则此工具假定参数是一个组件名称；否则，此工具假定参数是一个软件包名称。

## 应用包名
所谓应用包名，是唯一确定应用的标识。例如微信的包名是"com.tencent.mm", QQ的包名是"com.tencent.mobileqq"。  
要获取一个应用的包名，可以通过函数`getPackageName(appName)`获取。参见帮助->其他一般函数。


## pm命令

pm命令用于管理应用程序，例如卸载应用、冻结应用等。  
**以下命令均以"pm "开头，例如"shell(\"pm disable com.tencent.mm\");"(冻结微信)**

### list packages [options] filter	
输出所有软件包，或者，仅输出包名称包含 filter 中的文本的软件包。  
选项：

* -f：查看它们的关联文件。
* -d：进行过滤以仅显示已停用的软件包。
* -e：进行过滤以仅显示已启用的软件包。
* -s：进行过滤以仅显示系统软件包。
* -3：进行过滤以仅显示第三方软件包。
* -i：查看软件包的安装程序。
* -u：也包括卸载的软件包。
* --user user_id：要查询的用户空间。

### list permission-groups	
输出所有已知的权限组。
### list permissions [options] group	
输出所有已知权限，或者，仅输出 group 中的权限。  
选项：

* -g：按组加以组织。
* -f：输出所有信息。
* -s：简短摘要。
* -d：仅列出危险权限。
* -u：仅列出用户将看到的权限。

### list instrumentation [options]	
列出所有测试软件包。  
选项：

* -f：列出用于测试软件包的 APK 文件。
* target_package：列出仅用于此应用的测试软件包。

### list features	
输出系统的所有功能。
### list libraries	
输出当前设备支持的所有库。
### list users	
输出系统上的所有用户。
### path package	
输出给定 package 的 APK 的路径。  
### install [options] path	
将软件包（通过 path 指定）安装到系统。  
选项：

* -l：安装具有转发锁定功能的软件包。
* -r：重新安装现有应用，保留其数据。
* -t：允许安装测试 APK。
* -i installer_package_name：指定安装程序软件包名称。
* -s：在共享的大容量存储（如 sdcard）上安装软件包。
* -f：在内部系统内存上安装软件包。
* -d：允许版本代码降级。
* -g：授予应用清单文件中列出的所有权限。

### uninstall [options] package	

从系统中卸载软件包。  
选项：

* -k：移除软件包后保留数据和缓存目录。
### clear package	
删除与软件包关联的所有数据。

### enable package_or_component	
启用给定软件包或组件（作为“package/class”写入）。

### disable package_or_component	
停用给定软件包或组件（作为“package/class”写入）。

### disable-user [options] package_or_component	

选项：

* --user user_id：要停用的用户。
### grant package_name permission
向应用授予权限。在运行 Android 6.0（API 级别 23）及更高版本的设备上，可以是应用清单中声明的任何权限。在运行 Android 5.1（API 级别 22）和更低版本的设备上，必须是应用定义的可选权限。

### revoke package_name permission
从应用中撤销权限。在运行 Android 6.0（API 级别 23）及更高版本的设备上，可以是应用清单中声明的任何权限。在运行 Android 5.1（API 级别 22）和更低版本的设备上，必须是应用定义的可选权限。

### set-install-location location
更改默认安装位置。位置值：  
* 0：自动—让系统决定最佳位置。
* 1：内部—安装在内部设备存储上。
* 2：外部—安装在外部介质上。

> 注：此命令仅用于调试目的；使用此命令会导致应用中断和其他意外行为。

### get-install-location	
返回当前安装位置。返回值：
* 0 [auto]：让系统决定最佳位置。
* 1 [internal]：安装在内部设备存储上
* 2 [external]：安装在外部介质上

### set-permission-enforced permission [true|false]	
指定是否应强制执行给定的权限。

### trim-caches desired_free_space	
减少缓存文件以达到给定的可用空间。

### create-user user_name	
使用给定的 user_name 创建新用户，输出新用户的标识符。

### remove-user user_id	
移除具有给定的 user_id 的用户，删除与该用户关联的所有数据。

### get-max-users	
输出设备支持的最大用户数。

## 其他命令

### 进行屏幕截图
screencap 命令是一个用于对设备显示屏进行屏幕截图的 shell 实用程序。在 shell 中，此语法为：
```
screencap filename
```

例如：
```
$ shell("screencap /sdcard/screen.png");
```

### 列表文件
```
ls filepath
```
例如:
```
log(shell("ls /system/bin").result);
```
