
assert = console.assert.bind(console);

function testApp(){
    print('正在测试模块：app');
    assert("com.tencent.mm" == app.getPackageName("微信"));
    assert("com.tencent.mobileqq" == app.getPackageName("QQ"));

}