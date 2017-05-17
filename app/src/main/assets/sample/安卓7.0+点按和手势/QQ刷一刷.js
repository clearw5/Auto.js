"auto";

launchApp("QQ");
sleep(2000);

while(true){
    if(currentPackage() == 'com.tencent.mobileqq'){
        swipe(600, 900, 600, 1400, 400);
    }
    sleep(300);
}