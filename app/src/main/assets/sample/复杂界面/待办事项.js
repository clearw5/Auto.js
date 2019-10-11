"ui";

importClass(android.graphics.Paint);

ui.layout(
    <frame>
        <vertical>
            <appbar>
                <toolbar id="toolbar" title="Todo" />
            </appbar>
            <list id="todoList">
                <card w="*" h="70" margin="10 5" cardCornerRadius="2dp"
                    cardElevation="1dp" foreground="?selectableItemBackground">
                    <horizontal gravity="center_vertical">
                        <View bg="{{this.color}}" h="*" w="10" />
                        <vertical padding="10 8" h="auto" w="0" layout_weight="1">
                            <text id="title" text="{{this.title}}" textColor="#222222" textSize="16sp" maxLines="1" />
                            <text text="{{this.summary}}" textColor="#999999" textSize="14sp" maxLines="1" />
                        </vertical>
                        <checkbox id="done" marginLeft="4" marginRight="6" checked="{{this.done}}" />
                    </horizontal>

                </card>
            </list>
        </vertical>
        <fab id="add" w="auto" h="auto" src="@drawable/ic_add_black_48dp"
            margin="16" layout_gravity="bottom|right" tint="#ffffff" />
    </frame>
);

var materialColors = ["#e91e63", "#ab47bc", "#5c6bc0", "#7e57c2", "##2196f3", "#00bcd4",
    "#26a69a", "#4caf50", "#8bc34a", "#ffeb3b", "#ffa726", "#78909c", "#8d6e63"];

var storage = storages.create("todoList");
//从storage获取todo列表
var todoList = storage.get("items", [
    {
        title: "写操作系统作业",
        summary: "明天第1～2节",
        color: "#f44336",
        done: false
    },
    {
        title: "给ui模式增加若干Bug",
        summary: "无限期",
        color: "#ff5722",
        done: false
    },
    {
        title: "发布Auto.js 5.0.0正式版",
        summary: "2019年1月",
        color: "#4caf50",
        done: false
    },
    {
        title: "完成毕业设计和论文",
        summary: "2019年4月",
        color: "#2196f3",
        done: false
    }
]);;

ui.todoList.setDataSource(todoList);

ui.todoList.on("item_bind", function (itemView, itemHolder) {
    //绑定勾选框事件
    itemView.done.on("check", function (checked) {
        let item = itemHolder.item;
        item.done = checked;
        let paint = itemView.title.paint;
        //设置或取消中划线效果
        if (checked) {
            paint.flags |= Paint.STRIKE_THRU_TEXT_FLAG;
        } else {
            paint.flags &= ~Paint.STRIKE_THRU_TEXT_FLAG;
        }
        itemView.title.invalidate();
    });
});

ui.todoList.on("item_click", function (item, i, itemView, listView) {
    itemView.done.checked = !itemView.done.checked;
});

ui.todoList.on("item_long_click", function (e, item, i, itemView, listView) {
    confirm("确定要删除" + item.title + "吗？")
        .then(ok => {
            if (ok) {
                todoList.splice(i, 1);
            }
        });
    e.consumed = true;
});

//当离开本界面时保存todoList
ui.emitter.on("pause", () => {
    storage.put("items", todoList);
});

ui.add.on("click", () => {
    dialogs.rawInput("请输入标题")
        .then(title => {
            if (!title) {
                return;
            }
            dialogs.rawInput("请输入期限", "明天")
                .then(summary => {
                    todoList.push({
                        title: title,
                        summary: summary,
                        color: materialColors[random(0, materialColors.length - 1)]
                    });
                });
        })
});
