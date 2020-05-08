package com.zyb.ttsdemo;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnSpeech).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initTTS();
            }
        });
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private TextToSpeech textToSpeech;

    UtteranceProgressListener ttsListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            Log.e(TAG, "onStart");
        }

        @Override
        public void onDone(String utteranceId) {
            Log.e(TAG, "onDone");
            playSpeech();
        }

        @Override
        public void onError(String utteranceId) {
            Log.e(TAG, "onError");
            showToast("语音朗读出现了错误");
            stopSpeech();
        }
    };

    private void initTTS() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                //系统语音初始化成功
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setOnUtteranceProgressListener(ttsListener);
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //系统不支持中文播报
                        showToast("暂不支持中文播报");
                        stopSpeech();
                        return;
                    }
                    playSpeech();
                } else {
                    showToast("语音引擎初始化失败");
                    stopSpeech();
                }
            }
        });
    }

    private void playSpeech() {
        if (textToSpeech == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            showToast("暂不支持语音播放");
            stopSpeech();
            return;
        }
        String content = "通过联网方式进行发送短信";

        textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString());
    }

    private void stopSpeech() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSpeech();
    }
}
