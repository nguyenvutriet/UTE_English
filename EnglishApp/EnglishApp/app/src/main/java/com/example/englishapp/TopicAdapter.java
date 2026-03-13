package com.example.englishapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

    interface OnTopicClickListener {
        void onTopicClick(Topic topic);
    }

    List<Topic> list;
    private final OnTopicClickListener listener;

    public TopicAdapter(List<Topic> list, OnTopicClickListener listener){
        this.list = new ArrayList<>(list);
        this.listener = listener;
    }

    public void updateTopics(List<Topic> topics) {
        this.list = new ArrayList<>(topics);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){

        Topic t=list.get(position);

        holder.title.setText(t.name);
        holder.image.setImageResource(t.image);
        holder.progress.setText(t.learned+"/"+t.total);

        int percent = t.total == 0 ? 0 : (t.learned * 100) / t.total;
        holder.percent.setText(percent+"%");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTopicClick(t);
            }
        });
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title,progress,percent;

        public ViewHolder(View itemView){
            super(itemView);

            image=itemView.findViewById(R.id.topicImage);
            title=itemView.findViewById(R.id.topicTitle);
            progress=itemView.findViewById(R.id.topicProgress);
            percent=itemView.findViewById(R.id.topicPercent);
        }
    }
}