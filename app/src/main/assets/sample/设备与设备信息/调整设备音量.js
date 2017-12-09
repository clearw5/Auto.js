"ui";

ui.layout(
    <vertical padding="16">
        <text textColor="black" textSize="16sp">媒体音量</text>
        <seekbar id="music"/>

        <text textColor="black" textSize="16sp">通知音量</text>
        <seekbar id="notification"/>

        <text textColor="black" textSize="16sp">闹钟音量</text>
        <seekbar id="alarm"/>
    </vertical>
);

ui.music.setMax(device.getMusicMaxVolume());
ui.music.setProgress(device.getMusicVolume());
ui.music.setOnSeekBarChangeListener({
    onProgressChanged: function(seekbar, p, fromUser){
        if(fromUser){
            device.setMusicVolume(p);
        }
    }
});

ui.notification.setMax(device.getNotificationMaxVolume());
ui.notification.setProgress(device.getAlarmVolume());
ui.notification.setOnSeekBarChangeListener({
    onProgressChanged: function(seekbar, p, fromUser){
        if(fromUser){
            device.setNotificationVolume(p);
        }
    }
});

ui.alarm.setMax(device.getAlarmMaxVolume());
ui.alarm.setProgress(device.getAlarmVolume());
ui.alarm.setOnSeekBarChangeListener({
    onProgressChanged: function(seekbar, p, fromUser){
        if(fromUser){
            device.setAlarmVolume(p);
        }
    }
});