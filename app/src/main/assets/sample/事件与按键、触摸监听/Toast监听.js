auto();
events.observeToast();
events.onToast(function(toast){
    var pkg = toast.getPackageName();
    log("Toast内容: " + toast.getText() +
        " 来自: " + getAppName(pkg) +
        " 包名: " + pkg);
});
toast("监听中，请在日志中查看记录的Toast及其内容");