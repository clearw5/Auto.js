"ui";

ui.layout(
    <vertical>
        <appbar>
            <toolbar id="toolbar" title="卡片布局"/>
        </appbar>
        <card w="*" h="70" margin="10 5" cardCornerRadius="2dp"
            cardElevation="1dp" gravity="center_vertical">
            <vertical padding="18 8" h="auto">
                <text text="写操作系统作业" textColor="#222222" textSize="16sp"/>
                <text text="明天第1～2节" textColor="#999999" textSize="14sp"/>
            </vertical>
            <View bg="#f44336" h="*" w="10"/>
        </card>
        <card w="*" h="70" margin="10 5" cardCornerRadius="2dp"
            cardElevation="1dp" gravity="center_vertical">
            <vertical padding="18 8" h="auto">
                <text text="修复ui模式的Bug" textColor="#222222" textSize="16sp"/>
                <text text="无限期" textColor="#999999" textSize="14sp"/>
            </vertical>
            <View bg="#ff5722" h="*" w="10"/>
        </card>
        <card w="*" h="70" margin="10 5" cardCornerRadius="2dp"
            cardElevation="1dp" gravity="center_vertical">
            <vertical padding="18 8" h="auto">
                <text text="发布Auto.js 5.0.0正式版" textColor="#222222" textSize="16sp"/>
                <text text="2019年1月" textColor="#999999" textSize="14sp"/>
            </vertical>
            <View bg="#4caf50" h="*" w="10"/>
        </card>
        <card w="*" h="70" margin="10 5" cardCornerRadius="2dp"
            cardElevation="1dp" gravity="center_vertical">
            <vertical padding="18 8" h="auto">
                <text text="完成毕业设计和论文" textColor="#222222" textSize="16sp"/>
                <text text="2019年4月" textColor="#999999" textSize="14sp"/>
            </vertical>
            <View bg="#2196f3" h="*" w="10"/>
        </card>
    </vertical>
);

