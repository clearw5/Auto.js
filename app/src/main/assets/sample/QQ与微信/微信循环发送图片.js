"auto";
while (true) {
	className("ImageButton").descStartsWith("更多功能按钮").click();
	while(!click("相册"));
	sleep(500);
	className("GridView").findOne().child(1).click();
	sleep(200);
	while(!click("发送"));
	sleep(300);
}

