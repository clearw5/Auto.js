"auto";

function 下滑(){
  className("AbsListView").scrollable().scrollForward();
}

function 赞(){
  className("ImageView").desc("赞").click();
}

function 显示更多(){
  for(let i = 0; i < 2;i++){
    click("显示更多");
   } 
}

toast("请打开点赞自己的人的界面");
while(notStopped()){
  for(let i = 0; i < 10; i++){
    赞();
  }
  显示更多();
  下滑();
}