package com.example.englishapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    List<Category> list;

    public CategoryAdapter(List<Category> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, desc;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imgCategory);
            title = itemView.findViewById(R.id.txtTitle);
            desc = itemView.findViewById(R.id.txtDesc);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Category c = list.get(position);

        holder.title.setText(c.title);
        holder.desc.setText(c.description);
        holder.image.setImageResource(c.image);

        holder.itemView.setOnClickListener(v -> {

            PodcastListFragment fragment = new PodcastListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("category", c.title);

            fragment.setArguments(bundle);

            ((AppCompatActivity)v.getContext())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}