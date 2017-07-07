"auto";
var liked = {};
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
		like();
		while(!click("返回"));
		while(!click("成员资料"));
		while(!click("返回"));
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

function like(){
	var qq = getQQ();
	if(liked[qq]){
		while(!click("返回"));
		return;
	}
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