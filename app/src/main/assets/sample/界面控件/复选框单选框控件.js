"ui";

ui.layout(
    <vertical padding="16">
        <checkbox text="复选框"/>
        <checkbox checked="true" text="勾选的复选框"/>
        <radiogroup>
            <radio text="单选框1"/>
            <radio text="单选框2"/>
            <radio text="单选框3"/>
        </radiogroup>
        <radiogroup mariginTop="16">
            <radio text="单选框1"/>
            <radio text="单选框2"/>
            <radio text="勾选的单选框3" checked="true"/>
        </radiogroup>
    </vertical>
);