var window = floaty.window(
    <frame gravity="center">
        <text id="text" textSize="16sp" textColor="#f44336"/>
    </frame>
);

window.exitOnClose();

window.text.click(()=>{
    window.setAdjustEnabled(!window.isAdjustEnabled());
});

setInterval(()=>{
    //对控件的操作需要在UI线程中执行
    ui.run(function(){
        window.text.setText(dynamicText());
    });
}, 1000);

function dynamicText(){
    var date = new Date();
    var str = util.format("时间: %d:%d:%d\n", date.getHours(), date.getMinutes(), date.getSeconds());
    str += util.format("内存使用量: %d%%\n", getMemoryUsage());
    str += "当前活动: " + currentActivity() + "\n";
    str += "当前包名: " + currentPackage();
    return str;
}

//获取内存使用率
function getMemoryUsage(){
    var usage = (100 * device.getAvailMem() / device.getTotalMem());
    //保留一位小数
    return Math.round(usage * 10) / 10;
}