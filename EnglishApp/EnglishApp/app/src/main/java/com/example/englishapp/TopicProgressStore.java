package com.example.englishapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Locale;

public final class TopicProgressStore {

    private static final String PREF_NAME = "topic_progress";

    private TopicProgressStore() {
    }

    public static int getLearnedCount(Context context, String topicId, List<TopicWord> words) {
        int learned = 0;
        for (TopicWord word : words) {
            if (isLearned(context, topicId, word.word)) {
                learned++;
            }
        }
        return learned;
    }

    public static boolean isLearned(Context context, TopicWord word) {
        return isLearned(context, word.topicId, word.word);
    }

    public static boolean isLearned(Context context, String topicId, String word) {
        return getPreferences(context).getBoolean(buildKey(topicId, word), false);
    }

    public static void setLearned(Context context, TopicWord word, boolean learned) {
        getPreferences(context)
                .edit()
                .putBoolean(buildKey(word.topicId, word.word), learned)
                .apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String buildKey(String topicId, String word) {
        return topicId + "_" + word.toLowerCase(Locale.ROOT);
    }
}

