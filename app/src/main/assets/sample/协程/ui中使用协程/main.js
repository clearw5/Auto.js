"ui";

ui.layout(
    <frame bg="#4fc3f7">
        <text textColor="white" textSize="18sp" layout_gravity="center">
            UI中使用协程
        </text>
    </frame>
);

continuation.delay(5000);
if (!requestScreenCapture()) {
    dialogs.alert("请授予软件截图权限").await();
}


// 退出应用对话框
ui.emitter.on("back_pressed", function (e) {
    e.consumed = true;
    let exit = dialogs.confirm("确定要退出程序").await();
    if (exit) {
        ui.finish();
    }
});