//文件路径
var path = "/sdcard/1.txt";
//要写入的文件内容
var text = "Hello, AutoJs";
//以写入模式打开文件
var file = open(path, "w");
//写入文件
file.write(text);
//关闭文件
file.close();