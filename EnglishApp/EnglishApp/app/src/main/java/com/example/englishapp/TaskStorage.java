package com.example.englishapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.ArrayList;

public class TaskStorage {

    private static final String PREF = "TASK_PREF";
    private static final String KEY = "TASK_LIST";

    public static void saveTasks(Context context, ArrayList<String> tasks){

        SharedPreferences pref =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        JSONArray jsonArray = new JSONArray();

        for(String t : tasks){
            jsonArray.put(t);
        }

        pref.edit()
                .putString(KEY, jsonArray.toString())
                .apply();
    }

    public static ArrayList<String> loadTasks(Context context){

        SharedPreferences pref =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        String json = pref.getString(KEY, null);

        ArrayList<String> list = new ArrayList<>();

        if(json != null){
            try{

                JSONArray arr = new JSONArray(json);

                for(int i=0;i<arr.length();i++){
                    list.add(arr.getString(i));
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return list;
    }
}