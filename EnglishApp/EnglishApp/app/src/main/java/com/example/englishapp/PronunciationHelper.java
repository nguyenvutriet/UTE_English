package com.example.englishapp;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class PronunciationHelper {

    private TextToSpeech textToSpeech;
    private boolean ready;

    public PronunciationHelper(Context context) {
        textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
            ready = status == TextToSpeech.SUCCESS;
            if (ready && textToSpeech != null) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(0.75f);
            }
        });
    }

    public void speakUk(String word) {
        speak(word, Locale.UK, "uk_voice");
    }

    public void speakUs(String word) {
        speak(word, Locale.US, "us_voice");
    }

    public void release() {
        textToSpeech.stop();
        textToSpeech.shutdown();
    }

    private void speak(String word, Locale locale, String utteranceId) {
        if (!ready || word == null || word.trim().isEmpty()) {
            return;
        }

        textToSpeech.setLanguage(locale);
        textToSpeech.setSpeechRate(0.75f);
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
