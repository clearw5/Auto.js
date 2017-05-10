"auto";

launchApp("微信");
sleep(500);
while(!click("发现"));
while(!click("朋友圈"));
descContains("我的头像").click();
toast("请点击要删除的朋友圈");

while(true){
    deletePost();
    sleep(200);
}


function deletePost(){
    var isPhoto = desc("更多").exists();
    if(isPhoto){
        desc("更多").click();
    }
    if(click("删除")){
        while(!click("确定"));
        sleep(200);
        if(isPhoto){
            while(!back());
        }
    }

}