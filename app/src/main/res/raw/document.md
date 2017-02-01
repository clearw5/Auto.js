
###一、语法
本软件使用JavaScript语言([ECMAscript E5/E5.1](http://www.ecma-international.org/ecma-262/5.1/))，基于[Duktape](http://www.duktape.org/)引擎拓展一些自动操作(点击、长按、滑动等)函数。因而语法参见JavaScript(例如[w3cschool教程](http://www.w3school.com.cn/js/))。
###二、自动操作函数
**自动操作的函数都需要开启"自动操作服务"才能执行，否则执行到相应函数时脚本会停止运行。**
* `click(text)` 点击文本text所在的区域，并返回是否点击成功。当前界面没有出现该文本或者该文本所在区域不可点击时返回false。例如`click("发现")`，是点击"发现"。如果要点击"发现"直至点击成功可以用`while(!click("发现"))`。
> 文本所在区域指的是，从文本处向上寻找，直至发现一个可点击的部件为止。
* `click(left, top, bottom, right)` 点击与长方形范围严格匹配的区域，并返回是否点击成功。其中left为长方形左边与屏幕左边的像素距离，top为上边与屏幕上边的像素距离，right为右边与**屏幕左边**的像素距离, bottom为下边与**屏幕下边**的距离。区域严格匹配，至于要确定要点击的区域在屏幕上的左边位置，可以在侧拉菜单开启"脚本辅助"（或者安卓7.0以上在通知栏点击"修改"添加脚本辅助快捷设定图标)，之后每次点击或长按事件都会提示这次点击或长按的区域并自动保存，可以在编辑器中插入。
> 以下的longClick、select、scrollUp、scrollDown的参数均与click类似，不再赘述。
* `longClick` 长按
* `select` 选择
* `scrollUp` 上滑。不加参数时会寻找"最大"的可滑动的控件下滑，例如微信消息列表等。
* `scrollDown` 下滑。不加参数时与scrollUp类似。
* `input(string)` 把所有输入框的文本都置为string。
###三、其他函数
* `sleep(n)` 暂停执行n**毫秒**时间。
* `toast(string)` 显示提示文本。
* `launch(packageName)` 运行包名为packageName的应用。例如launch("com.tencent.mm")是运行微信，应用包名可以通过一些工具获取；获取通过函数launchApp代替
* `launch(packageName, className)` 运行包名为packageName，类名(Activity)为className的应用。
* `launchApp(appName)` 运存应用名称为appName的应用。例如launchApp("QQ")。不同应用名称可能相同，这时只运行其中某一个应用。
###四、在脚本中调用安卓
使用importClass来引入要使用的库，例如:
```javascript
importClass("android.view.View.OnClickListener")
view.setOnClickListener(new OnClickListener(function(){
    Toast.makeText(activity, "Button1 Clicked", Toast.LENGTH_SHORT).show();
    var intent = new Intent(activity, "com.furture.react.activity.DetailActivity");
    activity.startActivity(intent);
}));

view2.setOnClickListener(new OnClickListener({
    onClick: function(){
        Toast.makeText(activity, "Button2 Clicked", Toast.LENGTH_SHORT).show();
    }
}));
```
详细文档参见[DuktapeJava Java Docs](http://gubaojian.github.io/DuktapeJava/javadoc/)。
