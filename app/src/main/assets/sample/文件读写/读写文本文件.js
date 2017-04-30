//以写入模式打开SD卡根目录文件1.txt
var file = open("/sdcard/1.txt", "w")
//写入aaaa
file.write("aaaa");
//写入bbbbb后换行
file.writeline("bbbbb");
//写入ccc与ddd两行
file.writelines(["ccc", "ddd"]);
//关闭文件
file.close();

//以附加模式打开文件
file = open("/sdcard/1.txt", "a");
//附加一行"啦啦啦啦"
file.writeline("啦啦啦啦");
//附加一行"哈哈哈哈"
file.writeline("哈哈哈哈");
//附加两行ccc, ddd
file.writelines(["ccc", "ddd"]);
//输出缓冲区
file.flush();
//关闭文件
file.close();


//以读取模式打开文件
file = open("/sdcard/test.txt", "r")
//读取一行并打印
print(file.readline());
//读取剩余所有行并打印
for each(line in file.readlines()){
  print(line)
}
file.close()

//显示控制台
console.show()