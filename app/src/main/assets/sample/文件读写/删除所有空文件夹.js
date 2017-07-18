if(confirm("该操作会删除SD卡目录及其子目录下所有空文件夹，是否继续？")){
    toast("请点击右上角打开日志");
    deleteAllEmptyDirs(files.getSdcardPath());
    toast("全部完成！");
}

function deleteAllEmptyDirs(dir){
    var list = files.listDir(dir);
    var len = list.length;
    if(len == 0){
        log("删除目录 " + dir + " " + (files.remove(dir) ? "成功" : "失败"));
        return;
    }
    for(let i = 0; i < len; i++){
        var child = files.join(dir, list[i]);
        if(files.isDir(child)){
            deleteAllEmptyDirs(child);
        }
    }
}