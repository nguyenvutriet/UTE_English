package com.example.englishapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView recycler;
    DictionaryAdapter adapter;

    List<Word> list;
    List<Word> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recycler = findViewById(R.id.dictionaryRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        list = getFavoriteWords(); // 🔥 chỉ lấy từ đã lưu
        filteredList = new ArrayList<>(list);

        adapter = new DictionaryAdapter(this, filteredList);
        recycler.setAdapter(adapter);

        EditText search = findViewById(R.id.edtSearch);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            @Override public void afterTextChanged(Editable s){}
        });

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // 🔥 lọc từ đã lưu
    private List<Word> getFavoriteWords() {

        List<Word> result = new ArrayList<>();

        SharedPreferences pref = getSharedPreferences("fav", MODE_PRIVATE);

        try {
            InputStream is = getResources().openRawResource(R.raw.dictionary_1);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer);
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);
                String word = obj.getString("word");

                // ⭐ chỉ lấy từ đã lưu
                if (pref.getBoolean(word, false)) {

                    result.add(new Word(
                            word,
                            obj.getString("type"),
                            obj.getString("uk"),
                            obj.getString("us"),
                            obj.getString("meaning")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void filter(String key) {

        filteredList.clear();

        if (key.isEmpty()) {
            filteredList.addAll(list);
        } else {
            for (Word w : list) {
                if (w.word.toLowerCase().contains(key.toLowerCase())) {
                    filteredList.add(w);
                }
            }
        }

        adapter.updateList(filteredList);
    }
}