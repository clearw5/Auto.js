"auto";

launchApp("QQ");
toast("请打开一个聊天窗口，将会循环发送第一张图片100次");

while(!id("ivTitleBtnRightImage").exists());

var listParent = className("AbsListView").findOne().parent();
var icons = listParent.child(listParent.childCount() - 2);
icons.child(1).click();
sleep(200);

for(i = 0; i < 100;i++){
 className("CheckBox").untilFindOne().click();
 sleep(200);
 id("send_btn").click();
}