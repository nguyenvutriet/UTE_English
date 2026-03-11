package com.example.englishapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

    List<Topic> list;

    public TopicAdapter(List<Topic> list){
        this.list=list;
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

        int percent=(t.learned*100)/t.total;

        holder.percent.setText(percent+"%");
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