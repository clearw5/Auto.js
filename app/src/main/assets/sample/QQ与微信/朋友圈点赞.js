"auto";

//下滑
function scroll(){
  className("ListView").scrollForward();
}

//尝试点赞
function tryFav(btn){
  btn.click();
  sleep(300);
  if(!click("赞")){
    btn.click();
  }
}

while(true){
    var c = className("ImageView").desc("评论").untilFind();
    c.each(function(btn){
        tryFav(btn);
    });
    scroll();
}