
var scriptsPath = "/sdcard/脚本/";
if(!files.exists(scriptsPath)){
    scriptsPath = "/sdcard/Scripts/";
}
var scriptFiles = files.listDir(scriptsPath, function(name){
    return name.endsWith(".js");
});
var i = dialogs.singleChoice("请选择要运行的脚本", scriptFiles);
if(i < 0){
    exit();
}
var path = files.join(scriptsPath, scriptFiles[i]);
engines.execScriptFile(path);