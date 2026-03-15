package com.example.englishapp.adapter;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.Subtitle;

import java.util.List;

public class SubtitleAdapter extends RecyclerView.Adapter<SubtitleAdapter.ViewHolder> {

    private List<Subtitle> subtitleList;
    private int currentActiveIndex = -1;
    private OnSubtitleClickListener listener;

    public interface OnSubtitleClickListener {
        void onPauseClick();
        void onSubtitleClick(Subtitle subtitle);
    }

    public SubtitleAdapter(List<Subtitle> subtitleList, OnSubtitleClickListener listener) {
        this.subtitleList = subtitleList;
        this.listener = listener;
    }

    public void updateActiveSubtitle(float currentTime) {

        int newIndex = -1;

        for (int i = 0; i < subtitleList.size(); i++) {

            Subtitle s = subtitleList.get(i);

            if (currentTime >= s.getStartTime()
                    && currentTime <= s.getEndTime()) {

                newIndex = i;
                break;
            }
        }

        if (newIndex != currentActiveIndex) {

            int oldIndex = currentActiveIndex;
            currentActiveIndex = newIndex;

            if (oldIndex != -1)
                notifyItemChanged(oldIndex);

            if (currentActiveIndex != -1)
                notifyItemChanged(currentActiveIndex);
        }
    }

    public int getCurrentActiveIndex() {
        return currentActiveIndex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subtitle, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Subtitle subtitle = subtitleList.get(position);

        holder.tvText.setText(subtitle.getText());

        if (position == currentActiveIndex) {

            holder.tvText.setTextColor(Color.WHITE);
            holder.tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            holder.btnPauseSync.setVisibility(View.VISIBLE);

        } else {

            holder.tvText.setTextColor(Color.parseColor("#88FFFFFF"));
            holder.tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            holder.btnPauseSync.setVisibility(View.GONE);
        }

        holder.btnPauseSync.setOnClickListener(v -> {

            if (listener != null)
                listener.onPauseClick();
        });

        holder.itemView.setOnClickListener(v -> {

            if (listener != null)
                listener.onSubtitleClick(subtitle);
        });
    }

    @Override
    public int getItemCount() {
        return subtitleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvText;
        ImageView btnPauseSync;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvText = itemView.findViewById(R.id.tvSubtitleText);
            btnPauseSync = itemView.findViewById(R.id.btnPauseSync);
        }
    }
}