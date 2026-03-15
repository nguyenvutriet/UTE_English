package com.example.englishapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.activity.VideoPlayerActivity;
import com.example.englishapp.model.Video;
import com.example.englishapp.utils.HistoryDatabaseHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final Context context;
    private final List<Video> videoList;
    private final HistoryDatabaseHelper dbHelper;
    private boolean isTopicMode = false;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
        this.dbHelper = new HistoryDatabaseHelper(context);
    }

    public void setTopicMode(boolean topicMode) {
        this.isTopicMode = topicMode;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Chọn layout dựa trên chế độ
        int layoutRes = isTopicMode ? R.layout.item_video_topic : R.layout.item_video;
        View view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video video = videoList.get(position);

        holder.title.setText(video.getTitle());
        Picasso.get().load(video.getThumbnail()).into(holder.thumbnail);

        // Chỉ xử lý dấu tích nếu đang ở chế độ Chủ đề và layout có View này
        if (isTopicMode && holder.ivWatchedCheck != null) {
            boolean isWatched = dbHelper.isVideoInHistory(video.getVideoId());
            if (isWatched) {
                holder.ivWatchedCheck.setColorFilter(0xFF4CAF50); // Màu xanh (Material Green)
                holder.ivWatchedCheck.setAlpha(1.0f);
            } else {
                holder.ivWatchedCheck.setColorFilter(0xFFCCCCCC); // Màu xám (Light Gray)
                holder.ivWatchedCheck.setAlpha(1.0f);
            }
        } else if (holder.ivWatchedCheck != null) {
            holder.ivWatchedCheck.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("videoId", video.getVideoId());
            intent.putExtra("videoTitle", video.getTitle());
            intent.putExtra("thumbnail", video.getThumbnail());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, duration;
        ImageView ivWatchedCheck;

        public VideoViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.videoTitle);
            duration = itemView.findViewById(R.id.tvDuration);
            ivWatchedCheck = itemView.findViewById(R.id.ivWatchedCheck);
        }
    }
}
