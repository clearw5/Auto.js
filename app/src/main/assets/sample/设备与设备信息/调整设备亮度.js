"ui";

ui.layout(
    <vertical padding="16">
        <checkbox id="auto" text="自动亮度"/>
        <text textColor="black" textSize="16sp" margin="8">亮度</text>
        <seekbar id="brightness" max="100"/>
    </vertical>
);

//getBrightnessMode()返回亮度模式，1为自动亮度
ui.auto.setChecked(device.getBrightnessMode() == 1);
ui.auto.setOnCheckedChangeListener(function(v, checked){
    device.setBrightnessMode(checked ? 1: 0);
});

ui.brightness.setProgress(device.getBrightness());
ui.brightness.setOnSeekBarChangeListener({
    onProgressChanged: function(seekbar, p, fromUser){
        if(fromUser){
            device.setBrightness(p);
        }
    }
});