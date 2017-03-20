"auto";

function 取消关注(){
  id("af9").longClickable().longClick();
  click("取消关注");
  click("不再关注");
}

toast("请在10秒内打开订阅号界面");
toast("10秒后如果没有反应请稍微滑动一下页面");
sleep(10 * 1000);

while(true){
  取消关注();
}
