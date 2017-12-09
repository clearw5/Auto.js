"ui";

ui.layout(
    <scroll>
        <vertical padding="16">
            <text text="日历样式日期选择" textColor="black" textSize="16sp" marginTop="16"/>
            <datepicker />

            <text text="滑动日期选择" textColor="black" textSize="16sp" marginTop="16"/>
            <datepicker datePickerMode="spinner"/>

            <text text="时钟样式时间选择" textColor="black" textSize="16sp" marginTop="16"/>
            <timepicker />

            <text text="滑动时间选择" textColor="black" textSize="16sp" marginTop="16"/>
            <timepicker timePickerMode="spinner"/>

        </vertical>
    </scroll>
)