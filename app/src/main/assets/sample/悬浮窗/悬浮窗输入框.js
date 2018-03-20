var window = floaty.window(
    <vertical>
        <input id="input" text="请输入你的名字" textSize="16sp" />
        <button id="ok" text="确定"/>
    </vertical>
);

window.exitOnClose();

toast("长按确定键可调整位置");

window.ok.on("click", ()=>{
    toast("傻瓜! " + window.input.text());
});

window.ok.on("long_click", ()=>{
    window.setAdjustEnabled(!window.isAdjustEnabled());
});

setInterval(()=>{}, 1000);