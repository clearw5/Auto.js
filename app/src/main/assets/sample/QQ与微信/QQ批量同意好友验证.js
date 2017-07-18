"auto";

launchApp("QQ");
toast("请打开新朋友界面并自行下滑");

while(true){
  text("同意").clickable().findOne().click();
  sleep(1500);
  click("完成");
  sleep(1000);
}