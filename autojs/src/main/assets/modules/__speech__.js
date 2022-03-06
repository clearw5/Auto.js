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
        $javaSpeech.speak(text, pitch, speedRate, volume);
    }

    $speech.synthesizeToFile = function (text, wavPath, options) {
        let pitch = options.pitch || 1.0;
        let speedRate = options.speedRate || 1.0;
        let volume = options.volume || 0.8;
        wavPath = files.path(wavPath);
        $javaSpeech.synthesizeToFile(text, pitch, speedRate, volume, wavPath);
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