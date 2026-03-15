package com.example.englishapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import com.example.englishapp.R;
import com.example.englishapp.model.TopicVideo;

import java.util.List;

public class TopicVideoAdapter extends RecyclerView.Adapter<TopicVideoAdapter.ViewHolder> {

    private final Context context;
    private final List<TopicVideo> topicList;
    private final OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(TopicVideo topic);
    }

    public TopicVideoAdapter(Context context, List<TopicVideo> topicList, OnTopicClickListener listener) {
        this.context = context;
        this.topicList = topicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopicVideo topic = topicList.get(position);
        holder.tvTopicName.setText(topic.getName());
        holder.tvTopicProgress.setText(topic.getWatchedCount() + "/" + topic.getTotalVideos());
        
        if (topic.getImageUrl() != null && !topic.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(topic.getImageUrl())
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .into(holder.ivTopicImage);
        } else {
            holder.ivTopicImage.setImageResource(android.R.color.darker_gray);
        }

        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic));
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTopicImage;
        TextView tvTopicName, tvTopicProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTopicImage = itemView.findViewById(R.id.ivTopicImage);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            tvTopicProgress = itemView.findViewById(R.id.tvTopicProgress);
        }
    }
}
