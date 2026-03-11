package com.example.englishapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity {

    RecyclerView popularRecycler, adventureRecycler, childrenRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        popularRecycler = findViewById(R.id.popularRecycler);
        adventureRecycler = findViewById(R.id.adventureRecycler);
        childrenRecycler = findViewById(R.id.childrenRecycler);

        setupRecycler(popularRecycler, getPopularBooks());
        setupRecycler(adventureRecycler, getAdventureBooks());
        setupRecycler(childrenRecycler, getChildrenBooks());
    }

    private void setupRecycler(RecyclerView recyclerView, List<Book> list){

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        recyclerView.setAdapter(new BookAdapter(this,list));
    }

    private List<Book> getPopularBooks(){

        List<Book> list = new ArrayList<>();

        list.add(new Book("Uncle Tom's Cabin",R.drawable.book1));
        list.add(new Book("Leviathan",R.drawable.book2));
        list.add(new Book("Sherlock Holmes",R.drawable.book3));
        list.add(new Book("Persuasion",R.drawable.book4));

        return list;
    }

    private List<Book> getAdventureBooks(){

        List<Book> list = new ArrayList<>();

        list.add(new Book("Wisdom Daughter",R.drawable.book5));
        list.add(new Book("White Jacket",R.drawable.book6));
        list.add(new Book("Bulldog Drummond",R.drawable.book7));
        list.add(new Book("When the World Shook",R.drawable.book8));

        return list;
    }

    private List<Book> getChildrenBooks(){

        List<Book> list = new ArrayList<>();

        list.add(new Book("Tom Swift",R.drawable.book9));
        list.add(new Book("Pinocchio",R.drawable.book10));
        list.add(new Book("Squirrel Nutkin",R.drawable.book11));
        list.add(new Book("Alice's Adventures",R.drawable.book12));

        return list;
    }
}