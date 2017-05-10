"auto";
launchApp("微信");
enterOfficialAccounts();
sleep(200);
toast("请点击要取关的公众号");
while(true){
    //点击右上角小人图标
    desc("聊天信息").findOne().click();
    //获取微信号并记录
    var weChatId = textContains("微信号").findOne();
    console.log("取关: " + weChatId.text());
    //点击右上角三个小点儿
    desc("更多").findOne().click();
    while(!click("不再关注"));
    sleep(200);
    while(!click("不再关注"));
    sleep(200);
    enterOfficialAccounts();
}

//进入公众号界面
function enterOfficialAccounts(){
    while(!click("通讯录"));
    while(!click("公众号"));
}