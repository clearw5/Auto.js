"ui";

var ColoredButton = (function() {
    //继承ui.Widget
    util.extend(ColoredButton, ui.Widget);

    function ColoredButton() {
        //调用父类构造函数
        ui.Widget.call(this);
        //自定义属性color，定义按钮颜色
        this.defineAttr("color", (view, name, defaultGetter) => {
            return this._color;
        }, (view, name, value, defaultSetter) => {
            this._color = value;
            view.attr("backgroundTint", value);
        });
        //自定义属性onClick，定义被点击时执行的代码
        this.defineAttr("onClick", (view, name, defaultGetter) => {
            return this._onClick;
        }, (view, name, value, defaultSetter) => {
            this._onClick = value;
        });
    }
    ColoredButton.prototype.render = function() {
        return (
            <button textSize="16sp" style="Widget.AppCompat.Button.Colored" w="auto"/>
        );
    }
    ColoredButton.prototype.onViewCreated = function(view) {
        view.on("click", () => {
            if (this._onClick) {
                eval(this._onClick);
            }
        });
    }
    ui.registerWidget("colored-button", ColoredButton);
    return ColoredButton;
})();

ui.layout(
    <vertical>
        <colored-button text="第一个按钮" color="#ff5722"/>
        <colored-button text="第二个按钮" onClick="hello()"/>
    </vertical>
);

function hello() {
    alert("Hello ~");

}
