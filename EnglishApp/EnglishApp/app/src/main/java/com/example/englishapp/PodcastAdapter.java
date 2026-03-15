package com.example.englishapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.ViewHolder>{

    List<Podcast> list;

    Handler handler = new Handler();

    public PodcastAdapter(List<Podcast> list){
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title, subtitle;
        ProgressBar progress;

        public ViewHolder(View itemView){

            super(itemView);

            image = itemView.findViewById(R.id.imgPodcast);
            title = itemView.findViewById(R.id.txtTitle);
            subtitle = itemView.findViewById(R.id.txtSubtitle);
            progress = itemView.findViewById(R.id.progressPodcast);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_podcast,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){

        Podcast p = list.get(position);

        holder.title.setText(p.getTitle());
        holder.subtitle.setText(p.getSubtitle());
        holder.image.setImageResource(p.getImage());

        Context context = holder.itemView.getContext();

        SharedPreferences pref =
                context.getSharedPreferences("podcast", Context.MODE_PRIVATE);

        int saved = pref.getInt(p.getTitle(),0);

        MediaPlayer player = PlayerManager.mediaPlayer;

        int duration;

        // Podcast đang phát
        if(player != null
                && PlayerManager.currentTitle != null
                && PlayerManager.currentTitle.equals(p.getTitle())
                && player.getDuration() > 0){

            duration = player.getDuration();
            int current = player.getCurrentPosition();

            int percent = (current * 100) / duration;

            holder.progress.setProgress(percent);
            holder.progress.setVisibility(View.VISIBLE);

        }
        else{

            MediaPlayer temp = MediaPlayer.create(context,p.getAudio());
            duration = temp.getDuration();
            temp.release();

            if(saved > 0 && duration > 0){

                int percent = (saved * 100) / duration;

                holder.progress.setProgress(percent);
                holder.progress.setVisibility(View.VISIBLE);

            }else{

                holder.progress.setVisibility(View.GONE);

            }

        }

        holder.itemView.setOnClickListener(v -> {

            if(v.getContext() instanceof PodcastActivity){

                PodcastActivity activity = (PodcastActivity) v.getContext();

                PlayerManager.currentIndex = position;

                activity.playPodcast(p);

            }

        });

    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    // cập nhật progress realtime
    public void startProgressUpdate(){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                notifyDataSetChanged();

                handler.postDelayed(this,200);
            }
        },200);
    }

}