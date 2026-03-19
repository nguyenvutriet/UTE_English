package com.example.englishapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.englishapp.model.QuizResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String PREF_NAME = "QuizHistory";
    private static final String KEY_HISTORY = "history_list";

    public static void saveResult(Context context, QuizResult result) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        List<QuizResult> list = getHistory(context);
        list.add(0, result); // Bài mới nhất lên đầu
        pref.edit().putString(KEY_HISTORY, new Gson().toJson(list)).apply();
    }

    public static List<QuizResult> getHistory(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = pref.getString(KEY_HISTORY, "[]");
        Type type = new TypeToken<ArrayList<QuizResult>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void updateTitle(Context context, String id, String newTitle) {
        List<QuizResult> list = getHistory(context);
        for (QuizResult r : list) {
            if (r.id.equals(id)) {
                r.title = newTitle;
                break;
            }
        }
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_HISTORY, new Gson().toJson(list)).apply();
    }
}