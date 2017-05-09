//以UTF-8编码打开SD卡上的1.txt文件
var f = open("/sdcard/1.txt", "r", "utf-8");
//读取文件所有内容
var text = f.read();
//关闭文件
f.close();
//以gbk编码打开SD卡上的2.txt文件
var out = open("/sdcard/2.txt", "w", "gbk");
//写入内容
out.write(text);
//关闭文件
out.close();

