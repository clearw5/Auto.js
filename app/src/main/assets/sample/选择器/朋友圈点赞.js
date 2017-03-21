function 下滑(){
  className("ListView").scrollForward();
}

function 尝试点赞(btn){
  btn.click();
  sleep(300);
  if(!click("赞"))
    btn.click();
}

while(true){
var c = className("ImageView").desc("评论").untilFind();
c.each(function(btn){
  尝试点赞(btn);
});
下滑();
}