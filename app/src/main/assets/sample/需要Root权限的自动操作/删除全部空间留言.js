var ra = new RootAutomator();
ra.setScreenMetrics(1080, 1920);

launchApp("QQ");

sleep(1500);

//点击动态图标
ra.tap(891, 1851);
//点击好友动态
ra.tap(192, 453);
//点击头像
ra.tap(155, 638);
//点击留言
ra.tap(747, 775);

while(true){
    if(currentPackage() == 'com.tencent.mobileqq'){
        //点击箭头图标
        ra.tap(1029, 433);
        //点击删除
        ra.tap(530, 820);
        //点击确定
        ra.tap(331, 1122);
    }
    sleep(200);
}

