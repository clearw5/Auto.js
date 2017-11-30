"ui";

ui.layout(
    <vertical padding="16">
        <text text="处理中..." textColor="black" textSize="16sp"/>
        <progressbar />

        <text text="直线无限进度条" textColor="black" textSize="16sp" marginTop="24"/>
        <progressbar indeterminate="true" style="@style/Base.Widget.AppCompat.ProgressBar.Horizontal"/>

        <text text="直线进度条" textColor="black" textSize="16sp" marginTop="24"/>
        <progressbar progress="30" style="@style/Base.Widget.AppCompat.ProgressBar.Horizontal"/>

        <text text="可调节进度条" textColor="black" textSize="16sp" marginTop="24"/>
        <seekbar progress="20"/>

        <horizontal gravity="center" marginTop="24">
            <text id="progress_value" textColor="black" textSize="16sp" margin="8" text="0"/>
            <progressbar id="progress" w="*" style="@style/Base.Widget.AppCompat.ProgressBar.Horizontal"/>
        </horizontal>
        <button id="download">开始下载</button>
    </vertical>
);

var downloadId = null;

ui.download.click(()=>{
    if(downloadId != null){
        stopDownload();
    }else{
        startDownload();
    }
});

function stopDownload(){
    ui.download.text("开始下载");
    clearInterval(downloadId);
    downloadId = null;
}

function startDownload(){
    if(ui.progress.getProgress() == 100){
        ui.progress.setProgress(0);
    }
    ui.download.text("停止下载");
    downloadId = setInterval(()=>{
        var p = ui.progress.getProgress();
        p++;
        if(p > 100){
            stopDownload();
            return;
        }
        ui.progress.setProgress(p);
        ui.progress_value.setText(p.toString());
    }, 200);
}