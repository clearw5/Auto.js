

function test(){
    //testInput();
    ///sleep(500);
    //testLongClick();
    //sleep(500);
    testScrollAndShow();
}

Function.prototype.getName = function(){
    return this.name || this.toString().match(/function\s*([^(]*)\(/)[1]
}

assert = function(){
    if(arguments.length == 1){
        if(!arguments[0]){
           throw new Error("Assertion fail");
        }
        return;
    }
    if(arguments[0] != arguments[1]){
        var msg = "Assertion fail: " + arguments[0] + " != " + arguments[1];
        throw new Error(msg);
    }
}


function testInput(){
    log("testInput...");
    id("add").findOne().click();
    while(!click("新建文件"));
    for(var i = 0; i < 9; i++){
        input(i);
    }
    sleep(200);
    assert('请输入名称012345678', editable().findOne().text());
    sleep(400);
    back();
    sleep(200);
    back();
    sleep(200);
    back();
}

function random(){
    return ("" + Math.random()).substring(2);
}

function testLongClick(){
   log("testLongClick...");
   id("script_list_recycler_view").findOne().child(0).longClick();
   while(!click("重命名"));
   sleep(200);
   var r = random();
   editable().setText(r);
   sleep(200);
   while(!click("确定"));
   sleep(200);
   assert(text(r).exists());
}

function newFile(name){
    name = name + random();
    id("add").findOne().click();
    sleep(400);
    while(!click("新建文件"));
    sleep(400);
    editable().setText(name);
    sleep(400);
    while(!click("确定"));
    sleep(500);
    back();
    sleep(200);
    back();
    return name;
}

function testScrollAndShow(){
    var name1 = newFile("zzz");
    for(var i = 0; i < 10; i++){
        id("script_list_recycler_view").scrollForward();
    }
    assert(text(name1).findOne().visibleToUser());
    sleep(500);
    var name2 = newFile("000");
    sleep(200);
    for(var i = 0; i < 10; i++){
        id("script_list_recycler_view").scrollForward();
    }
    sleep(500);
    text(name2).findOne().show();
    assert(text(name2).findOne().visibleToUser());

}

function testApp(){
    before();
    assert("com.tencent.mm" == app.getPackageName("微信"));
    assert("com.tencent.mobileqq" == app.getPackageName("QQ"));
}

module.exports = test;