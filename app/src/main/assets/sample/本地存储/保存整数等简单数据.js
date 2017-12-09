var storage = storages.create("Auto.js例子:简单数据");
var a = 1234;
var b = true;
var str = "hello";
//保存
storage.put("a", a);
storage.put("b", b);
storage.put("str", str);

console.show();
//取出
log("a = " + storage.get("a"));
log("b = " + storage.get("b"));
log("str = " + storage.get("str"));