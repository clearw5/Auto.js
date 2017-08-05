auto();

var 好友验证信息 = "AutoJs自动添加群好友";
var 延迟 = 500;

launchApp("QQ");
sleep(500);
if(currentActivity() != "com.tencent.mobileqq.activity.TroopMemberListActivity"){
    toast("请打开要加的群的聊天窗口");
    openGroupMemberList();
}

var added = {};
while(true){
    var list = className("AbsListView").findOne();
	var count = list.childCount();
	for(var i = 0; i < count; i++){
	    var child = list.child(i);
        if(!child || child.className() != "android.widget.FrameLayout"){
            continue;
        }
        if(!isGroupMember(child) || isMyself(child)){
            continue;
        }
        child.child(0).click();
        sleep(500);
        addAsFriend();
        sleep(延迟);
	}
	list.scrollForward();
}

function isGroupMember(child){
    var tvName = child.findOne(id("tv_name"));
    if(!tvName){
      return false;
    }
    log(tvName.text());
	return tvName.text() != "Baby Q";
}

function isMyself(child){
	var i = child.findOne(text("我"));
	if(!i){
	  return false;
	}
	return i.id() && !i.id().endsWith("tv_name");
}

function addAsFriend(){
	var qq = getQQ();
	toast(qq);
	if(added[qq]){
		while(!click("返回"));
		return;
	}
	added[qq] = true;
	if(click("加好友")){
        sleep(800);
        setText(0, 好友验证信息);
        while(!click("发送"));
        sleep(800);
        if(click("取消")){
          sleep(400);
        }
        while(!back());
    }else{
        while(!back());
    }
}

function getQQ(){
	var qq = textMatches("\\d{5,12}").findOne().text();
	return qq;
}

function openGroupMemberList(){
    desc("群资料卡").click();
    var groupMemberCountView = textEndsWith("名成员").findOne();
    var groupMemberCount = parseInt(/\d+/.exec(groupMemberCountView.text())[0]);
    groupMemberCountView.parent().click();
    sleep(groupMemberCount * 4);
}