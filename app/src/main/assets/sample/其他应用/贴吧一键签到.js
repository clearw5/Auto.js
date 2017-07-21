"auto";

launchApp("百度贴吧");
sleep(1000);
id("tabcontainer").untilFind().each(function(tab){
  if(tab.childCount() != 5){
    return;
  }
  var ba = tab.child(1);
  if(ba){
    ba.click();
  }
});
toast("开始签到");
var signed = {};

while(true){
    var list = id("tab_content").findOne().findOne(id("listview"));
    list.children().each(function(child){
         if(child && child.className().endsWith("LinearLayout")){
            if(child.childCount() > 1){
                sign(child.findOne(id("left_container")));
                sign(child.findOne(id("right_container")));
            }
        }
    });
    list.scrollForward();
}
    
function sign(c){
  if(!c){
    return;
  }
  var name = c.child(0).text();
  if(signed[name]){
     return;
  }
  c.click();
  sleep(400);
  for(var i = 0; i < 3; i ++){
    if(click("签到")){
       break;
    }
    sleep(200);
  }
  sleep(600);
  while(!back());
  signed[name] = true;
}