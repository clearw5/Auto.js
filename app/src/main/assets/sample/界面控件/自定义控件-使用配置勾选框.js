"ui";

var PrefCheckBox = require('./自定义控件-模块-配置勾选框.js');

ui.layout(
    <vertical>
        <pref-checkbox id="perf1" text="配置1"/>
        <pref-checkbox id="perf2" text="配置2"/>
        <button id="btn" text="获取配置"/>
    </vertical>
);

ui.btn.on("click", function(){
    toast("配置1为" + PrefCheckBox.getPref().get("perf1"));
    toast("配置2为" + PrefCheckBox.getPref().get("perf2"));
});


