"ui";

ui.layout(
    <frame>
        <list id="list">
            <vertical>
                <text id="name" textSize="16sp" textColor="#000000" text="姓名: {{name}}"/>
                <text id="age" textSize="16sp" textColor="#000000" text="年龄: {{age}}岁"/>
                <button id="deleteItem" text="删除"/>
            </vertical>
        </list>
    </frame>
);

var items = [
    {name: "小明", age: 18}, {name: "小红", age: 30},
    {name: "小东", age: 19}, {name: "小强", age: 31},
    {name: "小满", age: 20}, {name: "小一", age: 32},
    {name: "小和", age: 21}, {name: "小二", age: 1},
    {name: "小贤", age: 22}, {name: "小三", age: 2},
    {name: "小伟", age: 23}, {name: "小四", age: 3},
    {name: "小黄", age: 24}, {name: "小五", age: 4},
    {name: "小健", age: 25}, {name: "小六", age: 5},
    {name: "小啦", age: 26}, {name: "小七", age: 6},
    {name: "小哈", age: 27}, {name: "小八", age: 7},
    {name: "小啊", age: 28}, {name: "小九", age: 8},
    {name: "小啪", age: 29}, {name: "小十", age: 9}
];

ui.list.setDataSource(items);

ui.list.on("item_click", function(item, i, itemView, listView){
    toast("被点击的人名字为: " + item.name + "，年龄为: " + item.age);
});

ui.list.on("item_bind", function(itemView, itemHolder){
    itemView.deleteItem.on("click", function(){
        let item = itemHolder.item;
        toast("被删除的人名字为: " + item.name + "，年龄为: " + item.age);
        items.splice(itemHolder.position, 1);
    });
})