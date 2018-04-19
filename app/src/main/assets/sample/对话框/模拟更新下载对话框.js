var releaseNotes = "版本 v7.7.7\n"
    + "更新日志:\n"
    + "* 新增 若干Bug\n";
dialogs.build({
    title: "发现新版本",
    content: releaseNotes,
    positive: "立即下载",
    negative: "取消",
    neutral: "到浏览器下载"
})
    .on("positive", download)
    .on("neutral", () => {
        app.openUrl("https://www.autojs.org");
    })
    .show();

var downloadDialog = null;
var downloadId = -1;

function download(){
    downloadDialog = dialogs.build({
        title: "下载中...",
        positive: "暂停",
        negative: "取消",
        progress: {
            max: 100,
            showMinMax: true
        },
        autoDismiss: false
    })
        .on("positive", ()=>{
            if(downloadDialog.getActionButton("positive") == "暂停"){
                stopDownload();
                downloadDialog.setActionButton("positive", "继续");
            }else{
                startDownload();
                downloadDialog.setActionButton("positive", "暂停");
            }
        })
        .on("negative", ()=>{
            stopDownload();
            downloadDialog.dismiss();
            downloadDialog = null;
        })
        .show();
    startDownload();
}

function startDownload(){
    downloadId = setInterval(()=>{
        var p = downloadDialog.getProgress();
        if(p >= 100){
            stopDownload();
            downloadDialog.dismiss();
            downloadDialog = null;
            toast("下载完成");
        }else{
            downloadDialog.setProgress(p + 1);
        }
    }, 100);
}

function stopDownload(){
    clearInterval(downloadId);
}

