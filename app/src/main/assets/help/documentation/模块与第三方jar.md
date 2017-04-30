### loadJar(path)
* path \<String\> jar文件路径

载入jar文件。之后改文件的所有类均可直接使用。这在使用第三方库时十分方便。 

### require(path)
* path \<String\> js文件的相对路径

载入js文件并返回该模块。模块文件应该有module.exports语句指定模块的输出对象。

例如test.js:
```
function sayHello(name){
    print("Hello, " + name);
}
module.exports = sayHello;
```
同一个目录下的test_module.js:
```
var sayHello = require("test");
sayHello("AutoJs");
```
运行这个脚本将在控制台输出"Hello, AutoJs"