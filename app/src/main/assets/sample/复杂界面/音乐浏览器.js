"ui";

var IconView = (function() {
    //继承ui.Widget
    util.extend(IconView, ui.Widget);

    function IconView() {
        //调用父类构造函数
        ui.Widget.call(this);
        //自定义属性color，定义按钮颜色
        this.defineAttr("icon", (view, name, defaultGetter) => {
            return this._icon;
        }, (view, name, value, defaultSetter) => {
            this._icon = value;
            view.setImageResource(value);
        });
    }
    IconView.prototype.render = function() {
        return (
            <img />
        );
    }
    ui.registerWidget("icon", IconView);
    return IconView;
})();

var apps = [];

ui.layout(
    <vertical  bg="#ffffff">
        <list id="files" layout_weight="1">
            <linear bg="?selectableItemBackground">
                <icon icon="{{this.icon}}" w="50" h="70" margin="16" />
                <vertical>
                    <text id="name" textSize="16sp" textColor="#000000" text="{{this.appName}}" marginTop="16" maxLines="1" ellipsize="end"/>
                    <text id="path" textSize="13sp" textColor="#929292" text="{{this.packageName}}" marginTop="8" maxLines="1" ellipsize="end"/>
                </vertical>
            </linear>
        </list>
        <progressbar id="progressbar" indeterminate="true" style="@style/Base.Widget.AppCompat.ProgressBar.Horizontal"/>
    </vertical>
);

ui.apps.setDataSource(musicFiles);

ui.apps.on("item_click", function(item, pos){
    toast(item);
});

//启动线程来扫描音乐文件
threads.start(function () {
    listApps(apps);
    ui.run(()=> {
        ui.progressbar.setVisility(8);
    });
});

function listApps(apps) {
    var pm = context.getPackageManager();
    let list = pm.getInstalledPackages(0);
    for(let i = 0; i < list.size(); i++){
        let p = list.get(i);
        apps.push({
            appName: p.applicationInfo.loadLabel(pm).toString(),
            packageName: p.packageName,
            versionName: p.versionName,
            versionCode: p.versionCode,
            icon: p.applicationInfo.loadIcon(pm)
        });
    }
}