package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity {

    RecyclerView popularRecycler, adventureRecycler, childrenRecycler;
    AutoCompleteTextView searchBook;

    TextView btnAllPopular, btnAllAdventure, btnAllChildren;

    List<Book> allBooks = new ArrayList<>();

    List<Book> popular;
    List<Book> adventure;
    List<Book> children;

    ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        popularRecycler = findViewById(R.id.popularRecycler);
        adventureRecycler = findViewById(R.id.adventureRecycler);
        childrenRecycler = findViewById(R.id.childrenRecycler);

        searchBook = findViewById(R.id.searchBook);

        btnAllPopular = findViewById(R.id.btnAllPopular);
        btnAllAdventure = findViewById(R.id.btnAllAdventure);
        btnAllChildren = findViewById(R.id.btnAllChildren);

        popular = getPopularBooks();
        adventure = getAdventureBooks();
        children = getChildrenBooks();

        setupRecycler(popularRecycler, popular);
        setupRecycler(adventureRecycler, adventure);
        setupRecycler(childrenRecycler, children);

        allBooks.addAll(popular);
        allBooks.addAll(adventure);
        allBooks.addAll(children);

        setupSearch();
        setupCategoryButtons();
    }

    private void setupRecycler(RecyclerView recyclerView, List<Book> list){
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerView.setAdapter(new BookAdapter(this,list));
    }

    private void setupCategoryButtons(){

        btnAllPopular.setOnClickListener(v -> openCategory("Popular", popular));
        btnAllAdventure.setOnClickListener(v -> openCategory("Adventure", adventure));
        btnAllChildren.setOnClickListener(v -> openCategory("Children", children));
    }

    private void openCategory(String title, List<Book> books){

        Intent intent = new Intent(this, SearchActivity.class);

        intent.putExtra("title", title);
        intent.putExtra("results", new ArrayList<>(books));

        startActivity(intent);
    }

    private void setupSearch(){

        List<String> titles = new ArrayList<>();

        for(Book b : allBooks){
            titles.add(b.getTitle());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        titles);

        searchBook.setAdapter(adapter);
        searchBook.setThreshold(1);

        searchBook.setOnItemClickListener((parent, view, position, id) -> {

            String name = parent.getItemAtPosition(position).toString();

            searchBook.setText("");
            searchBook.dismissDropDown();

            openBook(name);
        });

        searchBook.setOnEditorActionListener((v, actionId, event) -> {

            String keyword = searchBook.getText().toString();

            ArrayList<Book> result = new ArrayList<>();

            for(Book b : allBooks){

                if(b.getTitle().toLowerCase().contains(keyword.toLowerCase())){
                    result.add(b);
                }
            }

            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("title","Tìm kiếm sách");
            intent.putExtra("results", result);
            startActivity(intent);

            return true;
        });
    }

    private void openBook(String bookName){

        for(Book b : allBooks){

            if(b.getTitle().equalsIgnoreCase(bookName)){

                Intent intent = new Intent(this, ReaderActivity.class);

                intent.putExtra("book", b.getFile());
                intent.putExtra("title", b.getTitle());
                intent.putExtra("image", b.getImage());

                startActivity(intent);

                break;
            }
        }
    }

    private List<Book> getPopularBooks(){

        List<Book> list = new ArrayList<>();

        list.add(new Book("Uncle Tom's Cabin",R.drawable.book1,"uncle_tom"));
        list.add(new Book("Leviathan",R.drawable.book2,"leviathan"));
        list.add(new Book("Sherlock Holmes",R.drawable.book3,"sherlock_holmes"));
        list.add(new Book("Persuasion",R.drawable.book4,"persuasion"));

        return list;
    }

    private List<Book> getAdventureBooks(){

        List<Book> list = new ArrayList<>();

        list.add(new Book("Wisdom Daughter",R.drawable.book5,"wisdom_daughter"));
        list.add(new Book("White Jacket",R.drawable.book6,"white_jacket"));
        list.add(new Book("Bulldog Drummond",R.drawable.book7,"bulldog_drummond"));
        list.add(new Book("When the World Shook",R.drawable.book8,"when_the_world_shook"));

        return list;
    }

    private List<Book> getChildrenBooks(){

        List<Book> list = new ArrayList<>();

        list.add(new Book("Tom Swift",R.drawable.book9,"tom_swift"));
        list.add(new Book("Pinocchio",R.drawable.book10,"pinocchio"));
        list.add(new Book("Squirrel Nutkin",R.drawable.book11,"squirrel_nutkin"));
        list.add(new Book("Alice's Adventures",R.drawable.book12,"alice"));

        return list;
    }
}