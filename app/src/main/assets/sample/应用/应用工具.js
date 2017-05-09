var i = dialogs.select("请选择工具", "获取应用包名", "打开应用详情页", "卸载应用");

if(i == -1){
    alert("没有选择任何工具！");
}

switch(i){
case 0:
    //获取应用包名
    appName = rawInput("请输入应用名称", "QQ");
    packageName = getPackageName(appName);
    toast(packageName);
    setClip(packageName);
    toast("已复制到剪贴板");
    break;
case 1:
    //打开应用详情页
    appName = rawInput("请输入应用名称", "微信");
    openAppSetting(getPackageName(appName));
    break;
case 2:
    //卸载应用
    appName = rawInput("请输入应用名称");
    packageName = getPackageName(appName);
    if(packageName == ""){
        toast("应用不存在");
    }else if(confirm("确定卸载应用" + packageName + "吗？")){
        app.uninstall(packageName);
    }
    break;
}