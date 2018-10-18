"ui";

ui.layout(
    <frame>
        <vertical>
            <appbar>
                <toolbar id="toolbar" title="卡片布局" />
            </appbar>
            <list id="todos">
                <card w="*" h="70" margin="10 5" cardCornerRadius="2dp"
                    cardElevation="1dp" gravity="center_vertical">
                    <vertical padding="18 8" h="auto">
                        <text text="{{this.title}}" textColor="#222222" textSize="16sp" />
                        <text text="{{this.summary}}" textColor="#999999" textSize="14sp" />
                    </vertical>
                    <View bg="{{this.color}}" h="*" w="10" />
                </card>
            </list>
        </vertical>
    </frame>
);

var todos = [
    {
        title: "",
        summary: "",
        color: ""
    },
    {
        title: "",
        summary: "",
        color: ""
    },
    {
        title: "",
        summary: "",
        color: ""
    },
    {
        title: "",
        summary: "",
        color: ""
    }
];

ui.todos.setDataSource(todos);
