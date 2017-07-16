"auto";

function 下滑(){
  className("AbsListView").scrollable().scrollForward();
}

function 赞(){
  var like = className("ImageView").desc("赞").find();
  if(like){
    like.click();
    return true;
  }
  return false;
}

function 显示更多(){
  for(let i = 0; i < 2;i++){
    click("显示更多");
  }
}

toast("请打开自己的资料页，点击点赞图标");
sleep(100);
waitForActivity("com.tencent.mobileqq.activity.VisitorsActivity");

while(notStopped()){
  var  i = 0;
  while(i < 10){
    i += 赞() ? 1 : 0;
    click("取消");
  }
  显示更多();
  if(currentActivity() == "com.tencent.mobileqq.activity.VisitorsActivity"){
     下滑();
  }
}