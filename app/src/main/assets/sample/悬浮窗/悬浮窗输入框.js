var window = floaty.window(
    <vertical>
        <input id="input" text="请输入你的名字" textSize="16sp" focusable="true"/>
        <button id="ok" text="确定"/>
    </vertical>
);

window.exitOnClose();

toast("长按确定键可调整位置");

window.input.on("key", function(keyCode, event){
    if(event.getAction() == event.ACTION_DOWN && keyCode == keys.back){
        window.disableFocus();
        event.consumed = true;
    }
});

window.input.on("touch_down", ()=>{
    window.requestFocus();
    window.input.requestFocus();
});

window.ok.on("click", ()=>{
    toast("傻瓜! " + window.input.text());
    window.disableFocus();
});

window.ok.on("long_click", ()=>{
    window.setAdjustEnabled(!window.isAdjustEnabled());
});

setInterval(()=>{}, 1000);
