package com.example.englishapp;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    RecyclerView resultRecycler;
    ImageView btnBack;
    TextView txtHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        resultRecycler = findViewById(R.id.resultRecycler);
        btnBack = findViewById(R.id.btnBack);
        txtHeader = findViewById(R.id.txtHeader);

        btnBack.setOnClickListener(v -> finish());

        String title = getIntent().getStringExtra("title");

        if(title != null){
            txtHeader.setText(title);
        }

        ArrayList<Book> results =
                (ArrayList<Book>) getIntent().getSerializableExtra("results");

        if(results == null){
            results = new ArrayList<>();
        }

        resultRecycler.setLayoutManager(new GridLayoutManager(this,3));

        resultRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent,
                                       RecyclerView.State state) {

                int spacing = 10;
                int position = parent.getChildAdapterPosition(view);
                int column = position % 3;

                outRect.left = spacing - column * spacing / 3;
                outRect.right = (column + 1) * spacing / 3;

                if(position >= 3){
                    outRect.top = spacing;
                }
            }
        });

        resultRecycler.setAdapter(new SearchBookAdapter(this, results));
    }
}