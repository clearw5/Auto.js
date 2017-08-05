auto("fast");


toast("请打开一个聊天窗口");
sleep(500);

while(true){
    if(currentPackage() == "com.stardust.scriptdroid"){
        sleep(300);
        continue;
    }
    setText("我喜欢你");
    click("发送");
}