package com.example.englishapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class TopicProgressStore {

    private static final String PREF_NAME = "topic_progress";
    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 5;
    private static final int LEARNED_LEVEL = 1;

    private static final long TWO_HOURS_MILLIS = 2L * 60L * 60L * 1000L;
    private static final long ONE_DAY_MILLIS = 24L * 60L * 60L * 1000L;

    private TopicProgressStore() {
    }

    public static final class WordProgress {
        public final int level;
        public final long lastReviewedAt;
        public final long nextReviewAt;

        WordProgress(int level, long lastReviewedAt, long nextReviewAt) {
            this.level = level;
            this.lastReviewedAt = lastReviewedAt;
            this.nextReviewAt = nextReviewAt;
        }
    }

    public static WordProgress getProgress(Context context, TopicWord word) {
        return getProgress(context, word.topicId, word.word);
    }

    public static WordProgress getProgress(Context context, String topicId, String word) {
        SharedPreferences preferences = getPreferences(context);
        String baseKey = buildBaseKey(topicId, word);
        int level = preferences.getInt(baseKey + "_level", MIN_LEVEL);
        long lastReviewedAt = preferences.getLong(baseKey + "_last_review", 0L);
        long nextReviewAt = preferences.getLong(baseKey + "_next_review", 0L);
        return new WordProgress(clampLevel(level), lastReviewedAt, nextReviewAt);
    }

    public static void review(Context context, TopicWord word, boolean remembered) {
        long now = System.currentTimeMillis();
        WordProgress current = getProgress(context, word);

        int nextLevel;
        if (remembered) {
            nextLevel = Math.min(MAX_LEVEL, current.level + 1);
        } else if (current.level <= MIN_LEVEL) {
            nextLevel = MIN_LEVEL;
        } else {
            nextLevel = Math.max(MIN_LEVEL, current.level - 1);
        }

        // Unlearned words (level 0) should stay unscheduled until first successful answer.
        if (nextLevel == MIN_LEVEL) {
            saveProgress(context, word.topicId, word.word, MIN_LEVEL, 0L, 0L);
            return;
        }

        boolean deepRemembered = remembered && current.level == MAX_LEVEL && nextLevel == MAX_LEVEL;
        long nextReviewAt = now + getDelayMillisForLevel(nextLevel, deepRemembered);
        saveProgress(context, word.topicId, word.word, nextLevel, now, nextReviewAt);
    }

    public static boolean isNew(Context context, TopicWord word) {
        return getProgress(context, word).level == MIN_LEVEL;
    }

    public static boolean isDue(Context context, TopicWord word) {
        WordProgress progress = getProgress(context, word);
        return progress.level > MIN_LEVEL
                && progress.lastReviewedAt > 0L
                && progress.nextReviewAt > 0L
                && progress.nextReviewAt <= System.currentTimeMillis();
    }

    public static List<TopicWord> buildStudySessionWords(Context context, List<TopicWord> words, int maxWords) {
        List<TopicWord> dueWords = new ArrayList<>();
        List<TopicWord> newWords = new ArrayList<>();

        for (TopicWord word : words) {
            if (isDue(context, word)) {
                dueWords.add(word);
            } else if (isNew(context, word)) {
                newWords.add(word);
            }
        }

        List<TopicWord> session = new ArrayList<>();
        if (!dueWords.isEmpty()) {
            addUpTo(session, dueWords, maxWords);
        } else {
            addUpTo(session, newWords, maxWords);
        }
        return session;
    }

    public static int getDueCount(Context context, String topicId, List<TopicWord> words) {
        int due = 0;
        long now = System.currentTimeMillis();
        for (TopicWord word : words) {
            WordProgress progress = getProgress(context, topicId, word.word);
            if (progress.level > MIN_LEVEL && progress.lastReviewedAt > 0L && progress.nextReviewAt > 0L && progress.nextReviewAt <= now) {
                due++;
            }
        }
        return due;
    }

    public static int getGlobalDueCount(Context context) {
        int due = 0;
        long now = System.currentTimeMillis();
        List<Topic> topics = TopicRepository.getTopics();
        for (Topic topic : topics) {
            for (TopicWord word : TopicRepository.getWordsForTopic(topic.id)) {
                WordProgress progress = getProgress(context, topic.id, word.word);
                if (progress.level > MIN_LEVEL && progress.lastReviewedAt > 0L && progress.nextReviewAt > 0L && progress.nextReviewAt <= now) {
                    due++;
                }
            }
        }
        return due;
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
        return getProgress(context, topicId, word).level >= LEARNED_LEVEL;
    }

    public static void setLearned(Context context, TopicWord word, boolean learned) {
        long now = System.currentTimeMillis();
        int level = learned ? LEARNED_LEVEL : MIN_LEVEL;
        long nextReviewAt = level == MIN_LEVEL ? 0L : now + getDelayMillisForLevel(level, false);
        long lastReviewedAt = level == MIN_LEVEL ? 0L : now;
        saveProgress(context, word.topicId, word.word, level, lastReviewedAt, nextReviewAt);
    }

    private static void addUpTo(List<TopicWord> target, List<TopicWord> source, int maxWords) {
        for (TopicWord word : source) {
            if (target.size() >= maxWords) {
                return;
            }
            target.add(word);
        }
    }

    private static void saveProgress(Context context,
                                     String topicId,
                                     String word,
                                     int level,
                                     long lastReviewedAt,
                                     long nextReviewAt) {
        String baseKey = buildBaseKey(topicId, word);
        getPreferences(context)
                .edit()
                .putInt(baseKey + "_level", clampLevel(level))
                .putLong(baseKey + "_last_review", lastReviewedAt)
                .putLong(baseKey + "_next_review", nextReviewAt)
                .apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String buildBaseKey(String topicId, String word) {
        return topicId + "_" + word.toLowerCase(Locale.ROOT);
    }

    private static int clampLevel(int level) {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
    }

    private static long getDelayMillisForLevel(int level, boolean deepRemembered) {
        switch (clampLevel(level)) {
            case 0:
                return 0L;
            case 1:
                return TWO_HOURS_MILLIS;
            case 2:
                return ONE_DAY_MILLIS;
            case 3:
                return 2L * ONE_DAY_MILLIS;
            case 4:
                return 3L * ONE_DAY_MILLIS;
            case 5:
                return deepRemembered ? 8L * ONE_DAY_MILLIS : 5L * ONE_DAY_MILLIS;
            default:
                return 0L;
        }
    }
}
