// SPDX-License-Identifier: GPL-3.0
// 此代码属全新文件，除上面一行所声明的协议（商业使用需要另外授权），无需遵循原autojs项目协议（MPL2.0及非商业性使用条款 ）
package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class Speech extends UtteranceProgressListener {

    private Context mContext;
    private static Speech mSpeech;
    private TextToSpeech mTextToSpeech;
    private boolean isSuccess = true;

    public Speech(Context context) {
        this.mContext = context.getApplicationContext();
        mTextToSpeech = new TextToSpeech(mContext, i -> {
            // TTS初始化
            if (i == TextToSpeech.SUCCESS) {
                int result = mTextToSpeech.setLanguage(Locale.CHINA);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    result = mTextToSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        isSuccess = false;
                    }
                }
                mTextToSpeech.setOnUtteranceProgressListener(Speech.this);
            }
        });
    }

    public static Speech getInstance(Context context) {
        if (mSpeech == null) {
            synchronized (Speech.class) {
                if (mSpeech == null) {
                    mSpeech = new Speech(context);
                }
            }
        }
        return mSpeech;
    }

    public String[] getEngines() {
        String[] result = new String[mTextToSpeech.getEngines().toArray().length];
        int i = 0;
        for (TextToSpeech.EngineInfo item : mTextToSpeech.getEngines()) {
            result[i] = item.name;
            i += 1;
        }
        return result;
    }

    public int setEngine(String enginePackageName) {
        return mTextToSpeech.setEngineByPackageName(enginePackageName);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String[] getVoices() {
        String[] result = new String[mTextToSpeech.getVoices().toArray().length];
        int i = 0;
        for (Voice item : mTextToSpeech.getVoices()) {
            result[i] = item.getName();
            i += 1;
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int setVoice(String voiceName) {
        for (Voice item : mTextToSpeech.getVoices()) {
            if (voiceName.equals(item.getName())) {
                return mTextToSpeech.setVoice(item);
            }
        }
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String[] getLanguages() {
        String[] result = new String[mTextToSpeech.getAvailableLanguages().toArray().length];
        int i = 0;
        for (Locale item : mTextToSpeech.getAvailableLanguages()) {
            result[i] = item.getDisplayName();
            i += 1;
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int setLanguage(String language) {
        for (Locale item : mTextToSpeech.getAvailableLanguages()) {
            if (language.equals(item.getDisplayName())) {
                return mTextToSpeech.setLanguage(item);
            }
        }
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speak(String text, Float pitch, Float speechRate, Float volume) {
        if (!isSuccess) {
            Toast.makeText(mContext, "TTS暂不支持该语言", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mTextToSpeech != null) {
            mTextToSpeech.setPitch(pitch);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            mTextToSpeech.setSpeechRate(speechRate);
            //设置音量
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume.toString());
            params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, "STREAM_MUSIC");
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_ADD, params);
        } else {
            Log.e("Speech", "mTextToSpeech is null");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speak(String text) {
        speak(text, 1.0f, 1.0f, 0.8f);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void synthesizeToFile(String text, Float pitch, Float speechRate, Float volume, String fileName) {
        if (!isSuccess) {
            Toast.makeText(mContext, "TTS暂不支持该语言", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mTextToSpeech != null) {
            mTextToSpeech.setPitch(pitch);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            mTextToSpeech.setSpeechRate(speechRate);
            //设置音量
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume.toString());
            mTextToSpeech.synthesizeToFile(text, params, fileName);
        } else {
            Log.e("Speech", "mTextToSpeech is null");
        }
    }


    public boolean isSpeaking() {
        return mTextToSpeech.isSpeaking();
    }

    public void shutdown() {
        if (mTextToSpeech != null) {
            mTextToSpeech.shutdown();
        }
    }

    public void stop() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
        }
    }

    public void destroy() {
        stop();
        if (mTextToSpeech != null) {
            mTextToSpeech.shutdown();
        }
    }

    @Override
    public void onStart(String s) {

    }

    @Override
    public void onDone(String s) {

    }

    @Override
    public void onError(String s) {

    }
}
