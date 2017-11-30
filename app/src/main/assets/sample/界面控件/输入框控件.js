"ui";

ui.layout(
    <vertical padding="16">
         <text text="输入框" textColor="black" textSize="16sp" marginTop="16"/>
         <input />

         <!-- hint属性用来设置输入框的提示-->
         <text text="带提示的输入框" textColor="black" textSize="16sp" marginTop="16"/>
         <input hint="请输入一些内容"/>

         <!-- inputType属性用来设置输入类型，包括number, email, phone等-->
         <text text="数字输入框" textColor="black" textSize="16sp" marginTop="16"/>
         <input inputType="number" text="123"/>

         <!-- password属性用来设置输入框是否是密码输入框 -->
         <text text="密码输入框" textColor="black" textSize="16sp" marginTop="16"/>
         <input password="true"/>

         <!-- lines属性用来设置输入框的行数 -->
         <text text="多行输入框" textColor="black" textSize="16sp" marginTop="16"/>
         <input lines="3"/>

         <text text="设置输入框错误信息" textColor="black" textSize="16sp" marginTop="16"/>
         <input id="qq" inputType="number" hint="请输入您的QQ号码"/>
         <button id="ok" text="确定" w="auto" style="Widget.AppCompat.Button.Colored"/>
    </vertical>
);

ui.ok.click(()=>{
    var text = ui.qq.text();
    if(text.length == 0){
        ui.qq.setError("输入不能为空");
        return;
    }
    var qq = parseInt(text);
    if(qq < 10000){
        ui.qq.setError("QQ号码格式错误");
        return;
    }
    ui.qq.setError(null);
});
