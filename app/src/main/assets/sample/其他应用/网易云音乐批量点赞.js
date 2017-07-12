"auto";

launchApp("网易云音乐");
toast("打开一个歌曲的评论页面");

//等待当前为评论页面
waitForActivity("com.netease.cloudmusic.activity.ResourceCommentActivity");


while(true){
    var list = className("ListView").findOne();
    list.find(className("RelativeLayout")).each(function(item){
        if(item && item.childCount() >= 3){
            //点赞按钮
            var fav = item.child(2);
            if(fav){
                //获取点赞次数
                var favCount = parseInt(fav.text());
                fav.click();
                sleep(100);
                fav.refresh();
                //如果点了以后点赞数减少了，那么说明赞被取消了，再点一次
                if(parseInt(fav.text()) < favCount){
                    fav.click();
                }
                sleep(100);
            }

        }
    });
    //下滑
    list.scrollForward();
}

