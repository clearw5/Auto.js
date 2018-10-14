"ui";

//这个自定义控件是一个勾选框checkbox，能够保存自己的勾选状态，在脚本重新启动时能恢复状态
var PrefCheckBox = (function() {
    //继承至ui.Widget
    util.extends(PrefCheckBox, ui.Widget);

    function PrefCheckBox() {
        //调用父类构造函数
        ui.Widget.call(this);
        //自定义属性key，定义在配置中保存时的key
        this.defineAttr("key");
    }
    PrefCheckBox.prototype.render = function() {
        return (
            <checkbox />
        );
    }
    PrefCheckBox.prototype.onViewCreated = function(view) {
        view.setChecked(PrefCheckBox.getPref().get(this.getKey(), false));
        view.on("check", (checked) => {
            PrefCheckBox.getPref().put(this.getKey(), checked);
        });
    }
    PrefCheckBox.prototype.getKey = function() {
        return this.key || view.attr("id").replace("@+id/", "");
    }
    PrefCheckBox.setPref = function(pref) {
        PrefCheckBox._pref = pref;
    }
    PrefCheckBox.getPref = function(){
        if(!PrefCheckBox._pref){
            PrefCheckBox._pref = storages.create("pref_pref");
        }
        return PrefCheckBox._pref;
    }
    ui.registerWidget("pref-checkbox", PrefCheckBox);
    return PrefCheckBox;
})();

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


