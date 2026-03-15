package com.example.englishapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.activity.ViewAllVideosActivity;
import com.example.englishapp.model.Channel;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {
    private Context context;
    private List<Channel> channelList;

    public ChannelAdapter(Context context, List<Channel> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Channel channel = channelList.get(position);
        holder.channelName.setText(channel.getName());
        
        if (channel.getIconUrl() != null && !channel.getIconUrl().isEmpty()) {
            Picasso.get().load(channel.getIconUrl()).into(holder.channelIcon);
        } else {
            holder.channelIcon.setImageResource(R.mipmap.ic_launcher_round);
        }

        HorizontalVideoAdapter videoAdapter = new HorizontalVideoAdapter(context, channel.getVideos());
        holder.videoRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.videoRecyclerView.setAdapter(videoAdapter);

        holder.btnViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewAllVideosActivity.class);
            intent.putExtra("channelId", channel.getId());
            intent.putExtra("channelName", channel.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView channelIcon;
        TextView channelName;
        TextView btnViewAll;
        RecyclerView videoRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            channelIcon = itemView.findViewById(R.id.channelIcon);
            channelName = itemView.findViewById(R.id.channelName);
            btnViewAll = itemView.findViewById(R.id.btnViewAll);
            videoRecyclerView = itemView.findViewById(R.id.videoHorizontalRecyclerView);
        }
    }
}