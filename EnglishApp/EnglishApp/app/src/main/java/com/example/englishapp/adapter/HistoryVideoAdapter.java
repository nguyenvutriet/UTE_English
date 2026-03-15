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

import com.example.englishapp.R;
import com.example.englishapp.activity.VideoPlayerActivity;
import com.example.englishapp.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryVideoAdapter extends RecyclerView.Adapter<HistoryVideoAdapter.HistoryVideoViewHolder> {

    private final Context context;
    private final List<Video> videoList;

    public HistoryVideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public HistoryVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_video, parent, false);
        return new HistoryVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryVideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.tvTitle.setText(video.getTitle());
        Picasso.get().load(video.getThumbnail()).into(holder.ivThumbnail);
        
        float durationSec = video.getWatchedDuration();
        int minutes = (int) (durationSec / 60);
        int seconds = (int) (durationSec % 60);
        String timeString;
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes = minutes % 60;
            timeString = String.format(java.util.Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeString = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        holder.tvDuration.setText(timeString);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("videoId", video.getVideoId());
            intent.putExtra("videoTitle", video.getTitle());
            intent.putExtra("thumbnail", video.getThumbnail());
            // Optionally clear top if you don't want a massive backstack
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class HistoryVideoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvDuration;

        public HistoryVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }
    }
}
