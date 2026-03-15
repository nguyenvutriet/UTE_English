package com.example.englishapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity {

    RecyclerView recycler;
    DictionaryAdapter adapter;

    List<Word> list;          // danh sách gốc
    List<Word> filteredList;  // danh sách sau khi search

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        recycler = findViewById(R.id.dictionaryRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        list = getWords();              // đọc JSON
        filteredList = new ArrayList<>(list);

        adapter = new DictionaryAdapter(this, filteredList);
        recycler.setAdapter(adapter);

        EditText search = findViewById(R.id.edtSearch);

        // 🔎 search khi đang gõ
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filterWords(s.toString());

            }

            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            @Override public void afterTextChanged(Editable s){}
        });

        // ⌨ search khi nhấn Enter
        search.setOnEditorActionListener((v, actionId, event) -> {

            // nếu nhấn nút search/done trên bàn phím
            if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE){

                filterWords(search.getText().toString());
                return true;
            }

            // nếu nhấn phím Enter vật lý
            if(event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER
                    && event.getAction() == android.view.KeyEvent.ACTION_DOWN){

                filterWords(search.getText().toString());
                return true;
            }

            return false;
        });
    }

    // 🔎 hàm lọc từ
    private void filterWords(String keyword){

        filteredList.clear();

        if(keyword.trim().isEmpty()){
            filteredList.addAll(list);
        }else{

            for(Word w : list){

                if(w.word.toLowerCase().contains(keyword.toLowerCase())){
                    filteredList.add(w);
                }

            }
        }

        adapter.updateList(filteredList);
    }

    // 📂 đọc file JSON từ res/raw
    private List<Word> getWords(){

        List<Word> list = new ArrayList<>();

        try{

            InputStream is = getResources().openRawResource(R.raw.dictionary_1);

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            String json = new String(buffer);

            JSONArray array = new JSONArray(json);

            for(int i=0;i<array.length();i++){

                JSONObject obj = array.getJSONObject(i);

                String word = obj.getString("word");
                String type = obj.getString("type");
                String uk = obj.getString("uk");
                String us = obj.getString("us");
                String meaning = obj.getString("meaning");

                list.add(new Word(word,type,uk,us,meaning));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }
}