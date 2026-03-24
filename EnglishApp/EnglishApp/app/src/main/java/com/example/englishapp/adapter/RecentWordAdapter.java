package com.example.englishapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.PronunciationHelper;
import com.example.englishapp.R;
import com.example.englishapp.TopicWord;

import java.util.List;

public class RecentWordAdapter extends RecyclerView.Adapter<RecentWordAdapter.ViewHolder> {

    private final List<TopicWord> recentWords;
    private final PronunciationHelper pronunciationHelper;

    public RecentWordAdapter(PronunciationHelper pronunciationHelper, List<TopicWord> recentWords) {
        this.pronunciationHelper = pronunciationHelper;
        this.recentWords = recentWords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_word, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopicWord word = recentWords.get(position);
        
        holder.tvRecentWord.setText(word.word);
        holder.tvRecentUk.setText(word.uk != null && !word.uk.isEmpty() ? word.uk : "/.../");
        holder.tvRecentUs.setText(word.us != null && !word.us.isEmpty() ? word.us : "/.../");
        holder.tvRecentMeaning.setText(word.meaning);

        holder.btnSpeakUk.setOnClickListener(v -> {
            if (pronunciationHelper != null) pronunciationHelper.speakUk(word.word);
        });
        holder.btnSpeakUs.setOnClickListener(v -> {
            if (pronunciationHelper != null) pronunciationHelper.speakUs(word.word);
        });

        // Just an example action for the edit/detail button
        holder.btnRecentDetail.setOnClickListener(v -> {
            // Could open dictionary or word detail
        });
    }

    @Override
    public int getItemCount() {
        return recentWords.size();
    }

    public void updateData(List<TopicWord> newWords) {
        recentWords.clear();
        recentWords.addAll(newWords);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecentWord, tvRecentUk, tvRecentUs, tvRecentMeaning;
        ImageView btnRecentDetail;
        View btnSpeakUk, btnSpeakUs; // Assuming the volume icons are children of the linear layouts

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecentWord = itemView.findViewById(R.id.tvRecentWord);
            tvRecentUk = itemView.findViewById(R.id.tvRecentUk);
            tvRecentUs = itemView.findViewById(R.id.tvRecentUs);
            tvRecentMeaning = itemView.findViewById(R.id.tvRecentMeaning);
            btnRecentDetail = itemView.findViewById(R.id.btnRecentDetail);

            btnSpeakUk = itemView.findViewById(R.id.btnSpeakUk);
            btnSpeakUs = itemView.findViewById(R.id.btnSpeakUs);
        }
    }
}
