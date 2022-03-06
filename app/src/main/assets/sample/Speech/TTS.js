// SPDX-License-Identifier: GPL-3.0
// 此代码属新加JS脚本，调用了autojs相关API，原则上除了上面一行所声明的协议，也不需遵循原autojs项目的MPL2.0及非商业性使用条款。
"ui";

ui.layout(
    <vertical>
        <text id="name" text="TTS" textSize="22sp" textColor="#fbfbfe" bg="#00afff" w="*" gravity="center">
        </text>
        <input id="text" maxHeight="700" hint="请输入你最想说的一句话"/>
        <horizontal>
            <text id="pitchTxt" text="音调:1.0" textSize="12sp" gravity="center"></text>
            <seekbar id="pitch" progress="100" max="500" w="*" h="*" />
        </horizontal>
        <horizontal>
            <text id="speedTxt" text="语速:1.0" textSize="12sp" gravity="center"></text>
            <seekbar id="speed" progress="100" max="500" w="*" h="*" />
        </horizontal>
        <horizontal>
            <text id="volumeTxt" text="音量:1.0" textSize="12sp" gravity="center"></text>
            <seekbar id="volume" progress="100" max="500" w="*" h="*" />
        </horizontal>
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
 + "  let wavPath = '/sdcard/tts.mp3';// 设置保存的音频路径\n"
 + "  $speech.setEngine($speech.getEngines()[0]);// 设置引擎 返回Promise\n"
 + "  $speech.getLanguages();// 显示语言列表\n"
 + "  $speech.setLanguage('中文');// 设置语言 返回Promise\n"
 + "  $speech.setVoice($speech.getVoices()[0]);// 设置音色/发音人\n"
 + "  $speech.synthesizeToFile(text, wavPath, { pitch: 1.0, speechRate: 1.0, volume: 0.8 });// 另存为音频，格式为wav\n"
 + "  $speech.speak(text, pitch, speechRate, volume);// 播放合成语音\n"
 + "  $speech.speak(text);//简化的调用命令，默认参数：pitch = 1.0, speechRate = 1.0, volume = 0.8\n"
 + "  $speech.stop();// 停止播放\n"
 + "  $speech.shutdown();// 关闭\n"
 + "  $speech.destroy();// 关闭，释放资源，非必要，供万一出现内存泄露时使用\n"
  );

ui.play.click(function() {
    let text = ui.text.getText();
    if (text == null || text == "") text = "想说的话很多，可最后还是选择了沉默。"
    $speech.setLanguage('中文').then(resp => {
        let pitch = parseFloat(ui.pitch.getProgress().toString()) / 100
        let speed = parseFloat(ui.speed.getProgress().toString()) / 100
        let volume = parseFloat(ui.volume.getProgress().toString()) / 100
        $speech.speak(text, pitch, speed, volume)
    })
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
    let text = ui.text.getText();
    if (text == null || text == "") text = "想说的话很多，可最后还是选择了沉默。"
    let pitch = parseFloat(ui.pitch.getProgress().toString()) / 100
    let speed = parseFloat(ui.speed.getProgress().toString()) / 100
    let volume = parseFloat(ui.volume.getProgress().toString()) / 100
    $speech.synthesizeToFile(text, "/sdcard/脚本/tts.wav", { pitch: pitch, speechRate: speed, volume: volume })
    toastLog("TTS音频已保存为：/sdcard/脚本/tts.wav")
});
ui.quit.click(function() {
    $speech.shutdown();
    exit();
});

ui.pitch.setOnSeekBarChangeListener({
  onProgressChanged: function (seekbar, p, fromUser) {
    if (!fromUser) return
    value = parseFloat(ui.pitch.getProgress().toString()) / 100
    ui.post(() => {
        ui.pitchTxt.setText('音调:' + value.toFixed(2))
    })
  }
});
ui.speed.setOnSeekBarChangeListener({
  onProgressChanged: function (seekbar, p, fromUser) {
    if (!fromUser) return
    value = parseFloat(ui.speed.getProgress().toString()) / 100
    ui.post(() => {
        ui.speedTxt.setText('语速:' + value.toFixed(2))
    })
  }
});
ui.volume.setOnSeekBarChangeListener({
  onProgressChanged: function (seekbar, p, fromUser) {
    if (!fromUser) return
    value = parseFloat(ui.volume.getProgress().toString()) / 100
    ui.post(() => {
        ui.volumeTxt.setText('音量:' + value.toFixed(2))
    })
  }
});