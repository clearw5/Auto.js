"auto";

//漏赞过多请稍微调大延迟
var 延迟 = 200;

launchApp("QQ");
//点击联系人图标
className("TabWidget").findOne().child(1).click();
toast("请展开要点赞的分组");
var fail = 0;

while(true){

  var list = id("elv_buddies").className("AbsListView").findOne();
  var count = list.childCount();
  print("count" + list.childCount());
  
  for(let i = 0; i < count; i++){
    try{
      let child = list.child(i);
      print(i);
      if(isFriend(child)){
        //点进好友名片
        child.click();
        fav(10);
        backToList();
        sleep(延迟);
      }
    }catch(e){
      fail++;
      toast("可能漏赞:" + fail);
    }
  }
  list.scrollForward();
}

//点赞
function fav(times){
  var i = 0;
  var btn = descEndsWith("点击可赞").findOne();
  while(i < times){
    i += btn.click()? 1 : 0;
  }
}

//判断是否是好友的视图
function isFriend(child){
  return child && child.className().endsWith("LinearLayout") && child.childCount() == 2 && child.child(0).className().endsWith("FrameLayout");
}

//返回好友列表
function backToList(){
  id("ivTitleBtnLeft").click();
}