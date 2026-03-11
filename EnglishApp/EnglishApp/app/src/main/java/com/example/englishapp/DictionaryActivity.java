package com.example.englishapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity {

    RecyclerView recycler;
    DictionaryAdapter adapter;
    List<Word> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        recycler=findViewById(R.id.dictionaryRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        list=getWords();

        adapter=new DictionaryAdapter(this,list);

        recycler.setAdapter(adapter);

        EditText search=findViewById(R.id.edtSearch);

        search.addTextChangedListener(new TextWatcher(){

            public void onTextChanged(CharSequence s,int start,int before,int count){

                List<Word> filtered=new ArrayList<>();

                for(Word w:list){

                    if(w.word.toLowerCase().contains(s.toString().toLowerCase()))
                        filtered.add(w);

                }

                recycler.setAdapter(new DictionaryAdapter(DictionaryActivity.this,filtered));

            }

            public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            public void afterTextChanged(Editable s){}

        });

    }

    private List<Word> getWords(){

        List<Word> list=new ArrayList<>();

        list.add(new Word("American","adjective","/əˈmer.ɪ.kən/","/əˈmer.ɪ.kən/","thuộc nước Mỹ"));
        list.add(new Word("facilitate","verb","/fəˈsɪlɪteɪt/","/fəˈsɪlɪteɪt/","làm cho dễ dàng"));
        list.add(new Word("abandon","verb","/əˈbændən/","/əˈbændən/","từ bỏ"));
        list.add(new Word("ability","noun","/əˈbɪləti/","/əˈbɪləti/","khả năng"));
        list.add(new Word("academic","adj","/ˌækəˈdemɪk/","/ˌækəˈdemɪk/","thuộc học thuật"));
        list.add(new Word("access","noun","/ˈækses/","/ˈækses/","quyền truy cập"));
        list.add(new Word("achieve","verb","/əˈtʃiːv/","/əˈtʃiːv/","đạt được"));
        list.add(new Word("acquire","verb","/əˈkwaɪər/","/əˈkwaɪər/","đạt được"));
        list.add(new Word("adapt","verb","/əˈdæpt/","/əˈdæpt/","thích nghi"));
        list.add(new Word("advance","verb","/ədˈvɑːns/","/ədˈvæns/","tiến bộ"));

        return list;
    }
}