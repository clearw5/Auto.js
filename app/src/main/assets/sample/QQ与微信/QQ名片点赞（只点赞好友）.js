"auto";

var 延迟 = 100;

launchApp("QQ");
toast("请打开自己的资料页，点击点赞图标");
sleep(500);
waitForActivity("com.tencent.mobileqq.activity.VisitorsActivity");

while(notStopped()){
  var list = className("AbsListView").findOne();
  list.children().each(function(child){
      if(!child)
        return;
      var l = child.findByText("(好友)");
      if(l.size() > 0){
         var like = child.findOne(className("ImageView").desc("赞"));
         for(let i = 0; i < 10; i++){
            like.click();
            sleep(延迟);
         }
      }
  });
  click("显示更多");
  click("显示更多");
  if(currentActivity() == "com.tencent.mobileqq.activity.VisitorsActivity"){
     list.scrollForward();
  }
}