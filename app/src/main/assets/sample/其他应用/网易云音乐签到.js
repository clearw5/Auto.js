"auto";

launchApp("网易云音乐");
sleep(500);
classNameEndsWith("DrawerLayout").findOne().child(0).child(1).child(0).child(0).click();
text("签到").className("TextView").click();