以下内容来自[Rhino:Scripting Java](https://developer.mozilla.org/zh-CN/docs/Mozilla/Projects/Rhino/Scripting_Java)。

这个章节描述了如何在rhino中使用java。使用脚本调用Java有很多用途，它使得我们可以利用Java中现有的库，来帮助我们构建强大的脚本。我们可以通过编写脚本，来对Java程序进行测试。可以通过脚本来进行探索式编程，辅助Java的开发，所谓探索式编程，就是通过快速地编程调用库或API来探索这些库或API可以做什么，显而易见，脚本语言很适合探索式编程。

这里注意，ECMA标准并没有包含和Java(或者其他任何对象系统)交互的标准。本文所描述的所有内容，应该被认为是一个扩展。

目录:
* [访问Java的包和类](#访问Java的包和类)
* [使用Java的类](#使用Java的类)
* [实现Java接口](#实现Java接口)
* [创建Java数组](#创建Java数组)
* [Java字符串与JavaScript字符串](#Java字符串与JavaScript字符串)
* [Java异常](#Java异常)
* [JavaImporter](#JavaImporter)

## 访问Java的包和类

Java的每段代码都是类的一部分，每一个JAVA类都是包的一部分。在Javascript中，脚本不属于任何package。我们可以访问Java包中的类么？

Rhino定义了一个顶层的变量Packages。Packages的所有属性都是Java中顶层的包，比如java和com。比如我们可以访问java包：
```
Packages.java.util.Vector
```
还有一种更方便的方式，Rhino定义了一个顶层的变量java，等价于Packages.java。所以上面的例子可以更简介地写成：
```
java.util.Vector
```
如果你的脚本需要访问很多的Java类，每次都附带完整的包名会使得编程很麻烦。Rhino提供了一个顶层的方法importPackage，它的功能和Java的import一样。比如，我们可以导入java.io包中的所有类，然后直接通过类名File来访问java.io.File:
```
importPackage(java.io)
```
这里importPackage(java.io)使得java.io包中的所有类(例如File)可以在顶层被访问。这和Java中的java.io.*;等价。

要注意Java会默认导入java.lang.*，但是Rhino不会。因为JavaScript的顶层对象Boolean、Math、Number、Object和String和java.lang包中同名的类并不相同。因为这种冲突，建议不要用importPackage来导入java.lang包。

有一点要注意的，就是Rhino对于指定包名或类名时是如何处理错误的。如果java.Myclass是可访问的，Rhino会试图加载名为java.MyClass的类，如果加载失败，它会假设java.MyClass是一个包名，不会报错。只有在你试图将这个对象当作类使用时，才会报错。

## 使用Java的类

现在我们可以访问Java类，下一步就是要创建一个对象。方法就和在Java中一样， 用new来创建对象：
```
new java.util.Date();
```
如果我们将创建的对象存放在JavaScript变量中，我们可以调用它的方法：
```
var f = new java.io.File("test.text");
log(f.exists());
log(f.getName());
```
静态方法和属性可以直接通过类对象来访问：
```
log(java.lang.Math.PI);
```
不像Java，在JavaScript里，方法就是一个对象。它可以被评估，也可以被调用。如果我们去查看这个方法，我们可以看到这个方法所有重载的形式：
```
log(f.listFiles);
```
输出：
```
function listFiles() {/*
java.io.File[] listFiles()
java.io.File[] listFiles(java.io.FilenameFilter)
java.io.File[] listFiles(java.io.FileFilter)
*/}
```
输出告诉我们，File类有listFiles方法的三种重载：一种不包含参数的，另一种包含一个FilenameFilter类型的参数，第三个包含一个FileFilter类型的参数。所有的方法都返回一个File对象数组。可以观察到Java方法的参数和返回类型在探索式编程中是非常有用的，尤其是在对一个方法的参数和返回对象不确定的时候。
另一个有助于探索式编程的特性，是可以看到对象中定义的所有方法和属性。用JavaScript的for..in , 我们可以打印这些值:
```
for (i in f) { log(i) }
```
注意这里不仅列出了File类中的所有方法，也列出了从基类java.lang.Object中继承的方法，例如wait。这使得我们可以更好地处理那些有复杂继承关系的对象，因为我们可以看到对象中所有可用的方法。

Rhino可以通过属性名来方便地访问JavaBean的属性。一个JavaBean的属性foo被方法getFoo和setFoo定义，另外，一个也叫foo的boolean类型的属性，可以被isFoo来定义。比如, 下面的代码实际上调用了File对象的getName和isDirectory方法。

## 实现Java接口

```
 obj = { run: function () { log("\nrunning"); } };
 r = new java.lang.Runnable(obj);
 t = new java.lang.Thread(r);
 t.start();
```
若一个接口只有一个方法，也可以用直接用函数代替：
```
t = java.lang.Thread(function () { print("\nrunning"); });
t.start();
```

## 创建Java数组、

Rhino没有提供特定的语法创建Java的数组，只能通过反射来创建。
```
a = java.lang.reflect.Array.newInstance(java.lang.Character.TYPE, 2);
b = java.lang.reflect.Array.newInstance(java.lang.String, 5);
```
之后可以像JavaScript数组一样使用
```
a[0] = 'a';
a[1] = 'b';
```

## Java字符串与JavaScript字符串

Java字符串与JavaScript字符串并不等同，但在某种程度上两者可以通用。例如，你可以传递一个JavaScript的字符串给一个需要Java字符串参数的函数。
```
 javaString = new java.lang.String("Java");
 jsString = "JavaScript";
 log(javaString.length());
 log(jsString.length);
```
也可以对Java字符串调用了一个在java.lang.String中没有定义的JavaScript方法，例如：
```
log(javaString.match(/a.*/));
```

## Java异常

例如：
```
try { 
    java.lang.Class.forName("NonExistingClass"); 
} catch (e) {
    if (e.javaException instanceof java.lang.ClassNotFoundException) {
       print("Class not found");
    }
}
```
或者
```
function classForName(name) {
    try {
        return java.lang.Class.forName(name);
    } catch (e if e.javaException instanceof java.lang.ClassNotFoundException) {
        print("Class " + name + " not found");
    } catch (e if e.javaException instanceof java.lang.NullPointerException) {
        print("Class name is null");
    }
}

classForName("NonExistingClass");
classForName(null);
```

## JavaImporter 

```
var SwingGui = JavaImporter(Packages.javax.swing,
                            Packages.javax.swing.event,
                            Packages.javax.swing.border,
                            java.awt.event,
                            java.awt.Point,
                            java.awt.Rectangle,
                            java.awt.Dimension);
...

with (SwingGui) {
    var mybutton = new JButton(test);
    var mypoint = new Point(10, 10);
    var myframe = new JFrame();
...
}
```