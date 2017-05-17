"auto";

setScreenMetrics(1080, 1920);
launchApp("QQ");

sleep(1500);

click(891, 1851);
sleep(1000);
//点击好友动态
click(192, 453);
sleep(1000);
//点击头像
click(155, 638);
sleep(1000);
//点击留言
click(747, 775);
sleep(1000);

while(true){
    if(currentPackage() == 'com.tencent.mobileqq'){
        //点击箭头图标
        click(1029, 433);
        sleep(300);
        //点击删除
        click(530, 820);
        sleep(300);
        //点击确定
        click(331, 1122);
        sleep(200);
    }
    sleep(200);
}
