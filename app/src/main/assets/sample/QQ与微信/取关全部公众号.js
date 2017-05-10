"auto";

if (confirm("将会取关所有公众号，是否继续")) {
    unfollowAll();
}

function unfollowAll() {
    launchApp("微信");
    sleep(1000);
    while (!click("通讯录"));
    while (!click("公众号"));
    sleep(500);
    while (true) {
        className("ListView").findOne().children().each(function(item) {
            if (item.longClickable()) {
                unfollow(item);
                sleep(400);
            }
        });
    }
}

function unfollow(item) {
    item.longClick();
    while (!click("取消关注"));
    while (!click("不再关注"));
}
