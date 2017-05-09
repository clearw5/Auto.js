//输入应用名称
var appName = rawInput('请输入要卸载的应用名称');
//获取应用包名
var packageName = getPackageName(appName);
if(!packageName){
    toast("应用不存在！");
}else{
    //卸载应用
    app.uninstall(packageName);
}