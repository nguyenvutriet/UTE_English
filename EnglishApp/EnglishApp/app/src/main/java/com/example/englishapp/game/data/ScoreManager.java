package com.example.englishapp.game.data;

import android.content.Context;
import android.content.SharedPreferences;

public class ScoreManager {

    public static void saveScore(Context context,String name,int score){

        SharedPreferences prefs =
                context.getSharedPreferences("leaderboard",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(name,score);

        editor.apply();
    }

}