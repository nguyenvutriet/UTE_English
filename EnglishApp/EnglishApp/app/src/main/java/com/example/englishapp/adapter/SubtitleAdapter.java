package com.example.englishapp.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
    private boolean isPlaying = false;
    private OnSubtitleClickListener listener;

    public interface OnSubtitleClickListener {
        void onPauseClick();
        void onResumeClick();
        void onSubtitleClick(Subtitle subtitle);
        void onWordClick(String word);
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

    public void setPlaying(boolean playing) {
        if (isPlaying != playing) {
            isPlaying = playing;
            if (currentActiveIndex != -1) {
                notifyItemChanged(currentActiveIndex);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.example.englishapp.R.layout.item_subtitle_video, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Subtitle subtitle = subtitleList.get(position);

        String text = subtitle.getText();
        SpannableString spannableString = new SpannableString(text);

        // Split words and make them clickable
        String[] words = text.split("\\s+");
        int lastIndex = 0;
        for (String word : words) {
            final String cleanWord = word.replaceAll("[^a-zA-Z]", "");
            if (cleanWord.isEmpty()) continue;

            int start = text.indexOf(word, lastIndex);
            if (start == -1) continue;
            int end = start + word.length();
            lastIndex = end;

            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (listener != null) {
                        listener.onWordClick(cleanWord);
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false); // No underline to keep it clean
                    if (position == currentActiveIndex) {
                        ds.setColor(Color.WHITE);
                    } else {
                        ds.setColor(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
                    }
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.tvText.setText(spannableString);
        holder.tvText.setMovementMethod(LinkMovementMethod.getInstance());

        holder.btnTranslateLine.setVisibility(View.VISIBLE);
        holder.btnPauseSync.setVisibility(View.VISIBLE);

        if (position == currentActiveIndex) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.bg_active_item)); // Nổi bật dòng đang phát
            holder.tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            if (isPlaying) {
                holder.btnPauseSync.setImageResource(R.drawable.ic_pause_circle_video);
            } else {
                holder.btnPauseSync.setImageResource(R.drawable.ic_play_circle_video);
            }
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Màu nền mặc định
            holder.tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            holder.btnPauseSync.setImageResource(R.drawable.ic_play_circle_video);
        }

        holder.btnPauseSync.setOnClickListener(v -> {
            if (listener != null) {
                if (position == currentActiveIndex) {
                    if (isPlaying) {
                        listener.onPauseClick();
                    } else {
                        listener.onResumeClick();
                    }
                } else {
                    listener.onSubtitleClick(subtitle);
                }
            }
        });

        holder.btnTranslateLine.setOnClickListener(v -> {
            if (listener != null)
                listener.onWordClick(subtitle.getText());
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
        ImageView btnTranslateLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvText = itemView.findViewById(R.id.tvSubtitleText);
            btnPauseSync = itemView.findViewById(R.id.btnPauseSync);
            btnTranslateLine = itemView.findViewById(R.id.btnTranslateLine);
        }
    }
}
