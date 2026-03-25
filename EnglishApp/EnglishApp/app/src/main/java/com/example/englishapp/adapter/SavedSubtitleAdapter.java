package com.example.englishapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.Subtitle;

import java.util.List;

public class SavedSubtitleAdapter extends RecyclerView.Adapter<SavedSubtitleAdapter.ViewHolder> {

    private List<Subtitle> list;
    private OnItemClick listener;

    public interface OnItemClick {
        void onClick(Subtitle sub);
    }

    public SavedSubtitleAdapter(List<Subtitle> list, OnItemClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.example.englishapp.R.layout.item_subtitle_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subtitle sub = list.get(position);
        holder.tvText.setText(sub.getText());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(sub);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<Subtitle> newList) {
        this.list = newList != null ? newList : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvSubtitleText);
        }
    }
}