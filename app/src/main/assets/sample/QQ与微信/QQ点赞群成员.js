"auto";

launchApp("QQ");
sleep(500);
if(currentActivity() != "com.tencent.mobileqq.activity.TroopMemberListActivity"){
     toast("请打开要点赞的群的聊天窗口");
     openGroupMemberList();
}

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
        like();
        while(!click("返回"));
        while(!click("成员资料"));
        while(!click("返回"));
        sleep(500);
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

function like(){
	while(!click("更多"));
	while(!click("查看个人资料卡"));
	var likeBtn = descEndsWith("点击可赞").findOne();
	for(let i = 0; i < 10; i++){
		likeBtn.click();
		sleep(100);
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