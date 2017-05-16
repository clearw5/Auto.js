"ui";

ui.statusBarColor("#03a9f4");

ui.layout(
    <vertical>
        <text w="*" h="56" bg="#03a9f4" paddingLeft="16" gravity="center_vertical" color="#fff" size="18sp">QQ打开聊天窗口</text>
        <vertical h="*" w="*" margin="16 50">
            <input id="qq" h="50" hint="请输入QQ号"/>
            <button id="ok" text="确定" />
        </vertical>
    </vertical>
)

ui.ok.click(function(){
    app.startActivity({
        action: "android.intent.action.VIEW",
        data: "mqqwpa://im/chat?chat_type=wpa&uin=" + ui.qq.text()
    });
    ui.finish();
});