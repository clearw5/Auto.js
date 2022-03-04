"ui";

ui.layout(
    <vertical>
        <text id="name" text="TTS" textSize="22sp" textColor="#fbfbfe" bg="#00afff" w="*" gravity="center">
        </text>
        <input id="text" hint="请输入你最想说的一句话"/>
        <horizontal>
            <button id="play">播放</button>
            <button id="stop">停止</button>
            <button id="quit">退出</button>
        </horizontal>
        <horizontal>
            <button id="engines">引擎列表</button>
            <button id="languages">语言列表</button>
            <button id="voices">音色列表</button>
            <button id="saveToFile">存为音频</button>
        </horizontal>
        <text id="instruction" line="18"/>
    </vertical>
);

ui.instruction.setText("说明: 本功能基于系统语音服务实现(需要系统预装或自己安装TTS引擎)，相关参数可在系统TTS设置中调整\n"
 + "用法：\n"
 + "  let pitch = 1.0;// 设置音调，值越大声音越尖（女声），值越小声音越粗（男声），1.0是常规\n"
 + "  let speechRate = 1.0;// 设置语速\n"
 + "  let volume = 1.0;// 设置音量\n"
 + "  let text = 'Hello, World!';// 设置文本\n"
 + "  let wavPath = '/sdcard/tts.wav';// 设置保存的音频路径\n"
 + "  $speech.setEngine($speech.getEngines()[0]);// 设置引擎\n"
 + "  $speech.getLanguages();// 显示语言列表\n"
 + "  $speech.setLanguage('中文');// 设置语言\n"
 + "  $speech.setVoice($speech.getVoices()[0]);// 设置音色/发音人\n"
 + "  $speech.synthesizeToFile(text, pitch, speechRate, volume, wavPath);// 另存为音频，格式为WAV\n"
 + "  $speech.speak(text, pitch, speechRate, volume);// 播放合成语音\n"
 + "  $speech.speak(text);//简化的调用命令，默认参数：pitch = 1.0, speechRate = 1.0, volume = 0.8\n"
 + "  $speech.stop();// 停止播放\n"
 + "  $speech.shutdown();// 关闭\n"
 + "  $speech.destroy();// 关闭，释放资源，非必要，供万一出现内存泄露时使用\n"
  );

ui.play.click(function() {
    let text = ui.text.getText();
    if (text == null || text == "") text = "想说的话很多，可最后还是选择了沉默。"
    $speech.setLanguage('中文')
    $speech.speak(text, 1.0, 1.0, 0.8)
});
ui.stop.click(function() {
    $speech.stop();
});
ui.engines.click(function() {
    toastLog(JSON.stringify($speech.getEngines()));
});
ui.languages.click(function() {
    toastLog(JSON.stringify($speech.getLanguages()));
});
ui.voices.click(function() {
    toastLog(JSON.stringify($speech.getVoices()));
});
ui.saveToFile.click(function() {
    $speech.synthesizeToFile(text, 1.0, 1.0, 0.8, "/sdcard/tts.mp3")
    toastLog("TTS音频已保存为：/sdcard/tts.mp3")
});
ui.quit.click(function() {
    $speech.shutdown();
    exit();
});
