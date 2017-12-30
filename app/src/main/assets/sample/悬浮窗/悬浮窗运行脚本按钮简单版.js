var path = "/sdcard/脚本/test.js";
if(!files.exists(path)){
    toast("脚本文件不存在: " + path);
    exit();
}
var window = floaty.window(
    <frame>
        <button id="action" text="开始运行" w="90" h="40" bg="#77ffffff"/>
    </frame>
);

window.exitOnClose();

var execution = null;

window.action.click(()=>{
    if(window.action.getText() == '开始运行'){
        execution = engines.execScriptFile(path);
        window.action.setText('停止运行');
    }else{
        if(execution){
            execution.getEngine().forceStop();
        }
        window.action.setText('开始运行');
    }
});

window.action.longClick(()=>{
   window.setAdjustEnabled(!window.isAdjustEnabled());
   return true;
});

setInterval(()=>{}, 1000);
