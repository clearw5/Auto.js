auto();
events.observeNotification();
events.onNotification(function(info, notification){
    printNotification(info, notification);
});
toast("监听中，请在日志中查看记录的通知及其内容");

function printNotification(info, notification){
    log("应用包名: " + info.getPackageName());
    log("通知文本: " + info.getTexts());
    log("通知优先级: " + notification.priority);
    log("通知目录: " + notification.category);
    log("通知创建时间: " + new Date(notification.creationTime));
    log("通知时间: " + new Date(notification.when));
    log("通知数: " + notification.number);
    log("通知摘要: " + notification.tickerText);
    for(var i = 0; i < notification.actions.length; i++){
        var action = notification.actions[i];
        log("通知" + (i + 1) + ": " + action.title);
    }
}