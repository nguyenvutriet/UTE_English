package com.example.englishapp.game.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.game.model.Player;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>{

    ArrayList<Player> list;
    String currentPlayer;

    public LeaderboardAdapter(ArrayList<Player> list, String currentPlayer){
        this.list = list;
        this.currentPlayer = currentPlayer;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_item_leaderboard,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){

        Player p = list.get(position);

        holder.txtRank.setText(String.valueOf(position + 1));
        holder.txtName.setText(p.getName());
        holder.txtScore.setText(formatScore(p.getScore()));

        // top 3 medal
        if(position == 0){

            holder.imgMedal.setImageResource(R.drawable.medal_gold);
            holder.card.setCardBackgroundColor(Color.parseColor("#FFE082"));

        }else if(position == 1){

            holder.imgMedal.setImageResource(R.drawable.medal_silver);
            holder.card.setCardBackgroundColor(Color.parseColor("#CFD8DC"));

        }else if(position == 2){

            holder.imgMedal.setImageResource(R.drawable.medal_bronze);
            holder.card.setCardBackgroundColor(Color.parseColor("#E6A15C"));

        }else{

            holder.imgMedal.setImageResource(R.drawable.ic_leaderboard);
            holder.card.setCardBackgroundColor(Color.parseColor("#A5D6A7"));
        }

        // avatar
        holder.imgAvatar.setImageResource(R.drawable.avatar_default);

        // highlight player của mình
        if(p.getName().equals(currentPlayer)){

            holder.card.setStrokeWidth(6);
            holder.card.setStrokeColor(Color.parseColor("#FF5722"));

            holder.txtName.setTextSize(20);

            // nếu nằm trong rank 4-10 thì tô màu khác
            if(position >= 3){
                holder.card.setCardBackgroundColor(Color.parseColor("#FFCC80"));
            }
        }

        // animation item
        Animation anim = AnimationUtils.loadAnimation(
                holder.itemView.getContext(),
                R.anim.item_anim
        );

        holder.itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    // format score
    String formatScore(int score){

        if(score >= 1000){

            double k = score / 1000.0;
            return String.format("%.3fK",k);

        }else{

            return String.valueOf(score);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgAvatar;
        ImageView imgMedal;

        TextView txtName;
        TextView txtScore;
        TextView txtRank;

        MaterialCardView card;

        public ViewHolder(View itemView){

            super(itemView);

            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgMedal = itemView.findViewById(R.id.imgMedal);

            txtName = itemView.findViewById(R.id.txtName);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtRank = itemView.findViewById(R.id.txtRank);

            card = itemView.findViewById(R.id.cardLeaderboard);
        }
    }
}