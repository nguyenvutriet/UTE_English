package com.example.englishapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopicWordAdapter extends RecyclerView.Adapter<TopicWordAdapter.ViewHolder> {

    interface Listener {
        void onWordSelected(int position);
        void onLearnedStateChanged();
    }

    private final Context context;
    private final List<TopicWord> words;
    private final PronunciationHelper pronunciationHelper;
    private final Listener listener;
    private int selectedIndex;

    public TopicWordAdapter(Context context,
                            List<TopicWord> words,
                            PronunciationHelper pronunciationHelper,
                            Listener listener) {
        this.context = context;
        this.words = words;
        this.pronunciationHelper = pronunciationHelper;
        this.listener = listener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic_word, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TopicWord word = words.get(position);
        boolean learned = TopicProgressStore.isLearned(context, word);
        boolean selected = position == selectedIndex;

        holder.cardView.setCardBackgroundColor(Color.parseColor(selected ? "#EEF4FF" : "#FFFFFF"));
        holder.imageView.setImageResource(word.imageRes);
        holder.wordText.setText(word.word);
        holder.typeText.setText(word.type);
        holder.meaningText.setText(word.meaning);
        holder.ukText.setText(word.uk);
        holder.usText.setText(word.us);
        holder.learnedButton.setText(learned ? "Đã học" : "Đánh dấu");
        holder.learnedButton.setBackgroundResource(learned ? R.drawable.bg_topic_badge_success : R.drawable.bg_topic_badge_neutral);
        holder.learnedButton.setTextColor(Color.parseColor(learned ? "#167C3B" : "#5F6B7A"));

        holder.speakUk.setOnClickListener(v -> pronunciationHelper.speakUk(word.word));
        holder.speakUs.setOnClickListener(v -> pronunciationHelper.speakUs(word.word));

        holder.learnedButton.setOnClickListener(v -> {
            TopicProgressStore.setLearned(context, word, !learned);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onLearnedStateChanged();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int adapterPosition = holder.getBindingAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onWordSelected(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imageView;
        TextView wordText;
        TextView typeText;
        TextView meaningText;
        ImageView speakUk;
        ImageView speakUs;
        TextView ukText;
        TextView usText;
        TextView learnedButton;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.topicWordCard);
            imageView = itemView.findViewById(R.id.topicWordImage);
            wordText = itemView.findViewById(R.id.topicWordText);
            typeText = itemView.findViewById(R.id.topicWordType);
            meaningText = itemView.findViewById(R.id.topicWordMeaning);
            speakUk = itemView.findViewById(R.id.topicWordSpeakUk);
            speakUs = itemView.findViewById(R.id.topicWordSpeakUs);
            ukText = itemView.findViewById(R.id.topicWordUk);
            usText = itemView.findViewById(R.id.topicWordUs);
            learnedButton = itemView.findViewById(R.id.topicWordLearnedButton);
        }
    }
}

