package com.example.englishapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.ViewHolder>{

    Context context;
    List<Book> list;

    public SearchBookAdapter(Context context,List<Book> list){
        this.context=context;
        this.list=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        View view= LayoutInflater.from(context)
                .inflate(R.layout.item_book_grid,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){

        Book book=list.get(position);

        holder.title.setText(book.title);
        holder.image.setImageResource(book.image);

        holder.itemView.setOnClickListener(v -> {

            Intent intent=new Intent(context,ReaderActivity.class);

            intent.putExtra("book",book.file);
            intent.putExtra("title",book.title);
            intent.putExtra("image",book.image);

            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title;

        public ViewHolder(View itemView){
            super(itemView);

            image=itemView.findViewById(R.id.bookImage);
            title=itemView.findViewById(R.id.bookTitle);
        }
    }
}