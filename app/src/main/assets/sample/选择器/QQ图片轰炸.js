launchApp("QQ");
toast("打开一个聊天窗口，并点击下面的图片按钮");
for(i = 0; i < 100;i++){
 className("CheckBox").untilFindOne().click();
 id("send_btn").click();
}