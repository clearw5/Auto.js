"auto";

toast("请打开自己的资料页，点击点赞图标");
sleep(500);
waitForActivity("com.tencent.mobileqq.activity.VisitorsActivity");

while(notStopped()){
  for(var i = 0; i < 10; i++){
    className("ImageView").desc("赞").untilFind().each(function(like){
        var parent = like.parent();
        if(like.parent().findByText("(好友)")){
            like.click();
        }
    });
  }
  click("显示更多");
  click("显示更多");
  if(currentActivity() == "com.tencent.mobileqq.activity.VisitorsActivity"){
     className("AbsListView").scrollable().scrollForward();
  }
}