var window = floaty.window(
    <frame gravity="center">
        <text id="text" text="点击可调整位置" textSize="16sp"/>
    </frame>
);

window.exitOnClose();

window.text.click(()=>{
    window.setAdjustEnabled(!window.isAdjustEnabled());
});

setInterval(()=>{}, 1000);