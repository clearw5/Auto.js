module.exports = function(runtime, global){

    var $javaSpeech = Object.create(runtime.speech);
    var $speech = {}

    $speech.getEngines = function () {
        return $javaSpeech.getEngines();
    }

    $speech.getLanguages = function () {
        return $javaSpeech.getLanguages();
    }

    $speech.getVoices = function () {
        return $javaSpeech.getVoices();
    }

    $speech.setEngine = function (engine) {
        return new Promise(resolve => {
            $javaSpeech.setEngine(engine, {
                resolve: function (result) {
                    resolve(result);
                }
            });
        })
    }

    $speech.setLanguage = function (language) {
        return new Promise(resolve => {
            $javaSpeech.setLanguage(language, {
                resolve: function (result) {
                    resolve(result);
                }
            });
        });
    }

    $speech.setVoice = function (voice) {
        return new Promise(resolve => {
            $javaSpeech.setVoice(voice, {
                resolve: function (result) {
                    resolve(result);
                }
            });
        });
    }

    $speech.speak = function (text, pitch, speedRate, volume) {
        pitch = pitch || 1.0;
        speedRate = speedRate || 1.0;
        volume = volume || 0.8;
        return new Promise(function (resolve, reject) {
            $javaSpeech.speak(text, pitch, speedRate, volume, {
                onStart: function () {},
                onDone: function () {
                    threads.start(function() {
                        resolve('朗读完毕');
                    });
                },
                onError: function () {
                    threads.start(function() {
                        reject('朗读失败');
                    });
                }
            });
        });
    }

    $speech.synthesizeToFile = function (text, wavPath, options) {
        let pitch = options.pitch || 1.0;
        let speedRate = options.speedRate || 1.0;
        let volume = options.volume || 0.8;
        wavPath = files.path(wavPath);
        return new Promise(function (resolve, reject) {
            $javaSpeech.synthesizeToFile(text, pitch, speedRate, volume, wavPath, {
                onStart: function () {},
                onDone: function () {
                    threads.start(function () {
                        resolve('保存文件成功');
                    });
                },
                onError: function () {
                    threads.start(function () {
                        reject('保存文件失败');
                    });
                }
            });
        });
    }

    $speech.isSpeaking = function () {
        return $javaSpeech.isSpeaking();
    }

    $speech.stop = function () {
        $javaSpeech.stop();
    }

    $speech.shutdown = function () {
        $javaSpeech.shutdown();
    }

    $speech.destroy = function () {
        $javaSpeech.destroy();
    }

    global.$speech = $speech;
};