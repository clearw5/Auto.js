"auto";

var 好友验证信息 = "AutoJs自动添加群好友";
var 延迟 = 500;

toast("请打开群成员列表");

var added = {};
while(true){
	var list = className("AbsListView").findOne();
	list.children().each(function(child){
		if(child.className() != "android.widget.FrameLayout"){
			return;
		}
		if(!isGroupMember(child)){
			return;
		}
		if(isMyself(child)){
			return;
		}
		child.child(0).click();
		sleep(500);
		addAsFriend();
		sleep(延迟);
	});
	className("AbsListView").findOne().scrollForward();
}


function isGroupMember(child){
	if(child.childCount() != 1){
		return false;
	}
	return child.child(0) && child.child(0).className() == "android.widget.FrameLayout";
}

function isMyself(child){
	var l = child.findByText("我");
	return l && l.size() > 0;
}

function addAsFriend(){
	var qq = getQQ();
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
        back();
    }else{
        back();
    }
}

function getQQ(){
	var qq = textMatches("\\d{5,12}").findOne().text();
	return qq;
}