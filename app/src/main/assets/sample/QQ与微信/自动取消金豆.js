"auto";

toast("本脚本需要和名片点赞脚本同时运行才有效");

while(true){
    if(currentActivity() == "com.tencent.mobileqq.activity.VisitorsActivity"){
        click("取消");
        sleep(300);
    }
}