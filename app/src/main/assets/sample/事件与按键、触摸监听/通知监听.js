auto();
events.observeNotification();
events.onNotification(function(notification){
    printNotification(notification);
});
toast("监听中，请在日志中查看记录的通知及其内容");

function printNotification(notification){
    log("应用包名: " + notification.getPackageName());
    log("通知文本: " + notification.getText());
    log("通知优先级: " + notification.priority);
    log("通知目录: " + notification.category);
    log("通知时间: " + new Date(notification.when));
    log("通知数: " + notification.number);
    log("通知摘要: " + notification.tickerText);
}