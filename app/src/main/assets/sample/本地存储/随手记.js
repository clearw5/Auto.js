"ui";
ui.layout(
    <vertical padding="16">
        <horizontal>
            <text textColor="black" textSize="18sp" layout_weight="1">随手记</text>
            <button id="save" text="保存" w="auto" style="Widget.AppCompat.Button.Borderless.Colored"/>
        </horizontal>
        <input id="content" h="*" gravity="top"/>
    </vertical>
);
var storage = storages.create("Auto.js例子:随手记");
var content = storage.get("content");
if(content != null){
    ui.content.setText(content);
}
ui.save.click(()=>{
    storage.put("content", ui.content.text());
});



