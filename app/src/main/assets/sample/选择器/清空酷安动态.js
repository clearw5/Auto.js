"auto";

//清空酷安动态
//需要酷安客户端支持
//务必谨慎使用！！！
//by dazhang32552732
toast("正在启动酷安");
launch("com.coolapk.market");
sleep(2000);
while(!click("酷市场"));
while(!click("我"));
while(!click("动态"));
toast("请注意，即将开始删除酷安动态，这将不可恢复，请谨慎操作！");
sleep(3000);
toast("操作将在十秒后自动进行，请谨慎操作！");
sleep(5000);
toast("操作将在五秒后自动进行，请谨慎操作！");
sleep(5000);
toast("操作开始！");
while(true){
    id("more_view").click();
    while(!click("删除"));
    while(!click("确定"));
    sleep(1000);
}