"auto";

launchApp("QQ");

toast("在好友列表中点谁删谁");

while(true){
    while(!click("更多"));
    while(!click("删除好友"));
    sleep(300);
    while(!click("删除好友"));
}