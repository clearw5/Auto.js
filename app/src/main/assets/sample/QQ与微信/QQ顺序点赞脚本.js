"auto";

var 延迟 = 200;

function 下滑(){
  className("AbsListView").scrollable().scrollForward();
}

function 赞(){
  className("ImageView").desc("赞").untilFind()
    .each(function(item){
      for(var i = 0; i < 10; i++){
        item.click();
        sleep(延迟);
      }
    });
}

function 显示更多(){
  for(let i = 0; i < 2;i++){
    click("显示更多");
   } 
}

toast("请打开自己的资料页，点击点赞图标");
sleep(1000);

while(notStopped()){
  赞();
  显示更多();
  下滑();
}