"auto";

var 延迟 = 0;

function 点赞(){
  className("AbsListView").findOne()
   .children().each(function(ss){
      try{
        var 一行 = 找到图标那一行(ss);
        if(一行){
           一行.child(1).click();
           if(延迟 > 0){
             sleep(延迟);
           }
        }
      }catch(e){}
   });
}
        
        
function 找到图标那一行(s){
  for(let i = 0; i < s.getChildCount(); i++){
    var child = s.child(i);
    if(child && child.className()
       .endsWith("RelativeLayout") &&
       child.getChildCount() >= 4){
       if(是图片(child.child(1)) &&
           是图片(child.child(2)) &&
           是图片(child.child(3))){
         return child;
       }
    }
  }
}
           
function 是图片(item){ 
  return item && 
      item.className().endsWith("ImageView");
}

function 下滑(){
  className("AbsListView").findOne()
     .scrollForward();
}

while(true){
  点赞();
  下滑();
}