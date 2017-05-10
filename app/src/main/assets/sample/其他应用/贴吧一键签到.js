"auto";

launchApp("百度贴吧");
sleep(1000);
while(!click("进吧"));
toast("开始签到");
var signed = {};

while(true){
    var list = id("fragment_pager").findOne().findOne(id("listview"));
    list.children().each(function(child){
        if(child && child.className().endsWith("RelativeLayout")){
            if(child.childCount() > 1){
                sign(child);
            }
        }
    });
    list.scrollForward();
}

function sign(child){
    var li = child.child(1);
    if(li && li.childCount() > 0){
        var name = li.child(0).text();
        if(signed[name]){
            return;
        }
        child.click();
        sleep(400);
        for(var i = 0; i < 3; i ++){
          if(click("签到")){
            break;
          }
          sleep(200);
        }
        sleep(400);
        while(!back());
        signed[name] = true;
    }
}