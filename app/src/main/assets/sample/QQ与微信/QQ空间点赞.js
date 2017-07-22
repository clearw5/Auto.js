"auto";

var 延迟 = 0;

if(!requestScreenCapture()){
    toast("请求截图失败");
    stop();
}

var capture;

function like(){
  capture = captureScreen();
  className("AbsListView").findOne()
   .children().each(function(ss){
      try{
        var iconLine = findIconLine(ss);
        if(iconLine){
           var likeIcon = iconLine.child(1).child(0);          
           if(notLiked(likeIcon)){
               likeIcon.click();
           }
           if(延迟 > 0){
             sleep(延迟);
           }
        }
      }catch(e){
        log(e);
      }
   });
}
        
        
function findIconLine(s){
  for(let i = 0; i < s.getChildCount(); i++){
    var child = s.child(i);
    if(child && child.className()
       .endsWith("RelativeLayout") &&
       child.getChildCount() >= 4){
       if(isImage(child.child(1)) &&
           isImage(child.child(2)) &&
           isImage(child.child(3))){
         return child;
       }
    }
  }
}
           
function isImage(item){
  if(!item){
    return false;
  }
  if(item.className().endsWith("ImageView")){
    return true;
  }
  if(item.childCount() == 1){
    return isImage(item.child(0));
  }
  return false;
}

function notLiked(likeIcon){
    var x = likeIcon.bounds().centerX();
    var y = likeIcon.bounds().centerY();
    return images.detectsColor(capture, 0x767886, x, y);
}

toast("请打开QQ空间并慢慢下滑");
while(true){
  like();
}