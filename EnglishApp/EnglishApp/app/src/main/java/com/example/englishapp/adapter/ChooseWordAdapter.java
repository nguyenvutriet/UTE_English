package com.example.englishapp.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChooseWordAdapter extends RecyclerView.Adapter<ChooseWordAdapter.ViewHolder> {

    public static class GameSubtitle {
        public Subtitle subtitle;
        public String hiddenWord;       // The word that was removed
        public int hiddenWordIndex;     // Index in words array
        public boolean isRevealed;      // Whether the answer has been revealed
        public boolean isCorrect;       // Whether user answered correctly
        public boolean isSkipped;       // Whether user skipped

        public GameSubtitle(Subtitle subtitle, String hiddenWord, int hiddenWordIndex) {
            this.subtitle = subtitle;
            this.hiddenWord = hiddenWord;
            this.hiddenWordIndex = hiddenWordIndex;
            this.isRevealed = false;
            this.isCorrect = false;
            this.isSkipped = false;
        }
    }

    private List<GameSubtitle> gameSubtitles;
    private int currentActiveIndex = -1;
    private OnGameActionListener listener;
    private boolean isPlayerPlaying = false;

    public interface OnGameActionListener {
        void onToggleSentencePlayback(int position, Subtitle subtitle);
        void onTranslateClick(String text);
    }

    public ChooseWordAdapter(List<GameSubtitle> gameSubtitles, OnGameActionListener listener) {
        this.gameSubtitles = gameSubtitles;
        this.listener = listener;
    }

    public void setCurrentActiveIndex(int index) {
        int oldIndex = currentActiveIndex;
        currentActiveIndex = index;
        if (oldIndex != -1) notifyItemChanged(oldIndex);
        if (currentActiveIndex != -1) notifyItemChanged(currentActiveIndex);
    }

    public int getCurrentActiveIndex() {
        return currentActiveIndex;
    }

    public void revealWord(int index, boolean correct) {
        if (index >= 0 && index < gameSubtitles.size()) {
            GameSubtitle gs = gameSubtitles.get(index);
            gs.isRevealed = true;
            gs.isCorrect = correct;
            gs.isSkipped = !correct;
            notifyItemChanged(index);
        }
    }

    public List<GameSubtitle> getGameSubtitles() {
        return gameSubtitles;
    }

    public void updateActiveByTime(float currentTime) {
        int newIndex = -1;
        for (int i = 0; i < gameSubtitles.size(); i++) {
            Subtitle s = gameSubtitles.get(i).subtitle;
            if (currentTime >= s.getStartTime() && currentTime <= s.getEndTime()) {
                newIndex = i;
                break;
            }
        }
        if (newIndex != currentActiveIndex) {
            setCurrentActiveIndex(newIndex);
        }
    }

    public void setPlayerPlaying(boolean playing) {
        if (isPlayerPlaying == playing) return;
        isPlayerPlaying = playing;
        if (currentActiveIndex != -1) {
            notifyItemChanged(currentActiveIndex);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.example.englishapp.R.layout.item_choose_word_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameSubtitle gs = gameSubtitles.get(position);
        Subtitle subtitle = gs.subtitle;
        String originalText = subtitle.getText();
        String[] words = originalText.split("\\s+");

        // Build display text
        StringBuilder displayBuilder = new StringBuilder();
        int hiddenStart = -1;
        int hiddenEnd = -1;

        for (int i = 0; i < words.length; i++) {
            if (i > 0) displayBuilder.append(" ");

            if (i == gs.hiddenWordIndex) {
                hiddenStart = displayBuilder.length();
                if (gs.isRevealed) {
                    displayBuilder.append(gs.hiddenWord);
                } else {
                    displayBuilder.append(" ● ");
                }
                hiddenEnd = displayBuilder.length();
            } else {
                displayBuilder.append(words[i]);
            }
        }

        String displayText = displayBuilder.toString();
        SpannableString spannable = new SpannableString(displayText);

        // Style the hidden/revealed word
        if (hiddenStart >= 0 && hiddenEnd > hiddenStart && hiddenEnd <= displayText.length()) {
            if (gs.isRevealed) {
                if (gs.isCorrect) {
                    // Correct answer - show underlined in highlight color
                    spannable.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources().getColor(R.color.highlight_green)),
                            hiddenStart, hiddenEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new UnderlineSpan(),
                            hiddenStart, hiddenEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    // Skipped - show in orange/yellow
                    spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FFB74D")),
                            hiddenStart, hiddenEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new UnderlineSpan(),
                            hiddenStart, hiddenEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                // Hidden - show dot in blue
                spannable.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources().getColor(R.color.target_blue)),
                        hiddenStart, hiddenEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        holder.tvText.setText(spannable);

        // Active state styling
        boolean isActive = (position == currentActiveIndex);
        if (isActive) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.bg_active_item));
            holder.tvText.setTextColor(Color.WHITE);
            holder.tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            holder.ivIndicator.setVisibility(View.VISIBLE);
            holder.btnTranslate.setColorFilter(Color.WHITE);
            holder.btnPlay.setColorFilter(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.tvText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
            holder.tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            holder.ivIndicator.setVisibility(View.GONE);
            holder.btnTranslate.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
            holder.btnPlay.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
        }

        if (isActive) {
            holder.btnPlay.setImageResource(isPlayerPlaying
                    ? R.drawable.ic_pause_circle_video
                    : R.drawable.ic_play_circle_video);
        } else {
            holder.btnPlay.setImageResource(R.drawable.ic_play_circle_video);
        }

        holder.btnPlay.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleSentencePlayback(position, subtitle);
            }
        });

        holder.btnTranslate.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTranslateClick(subtitle.getText());
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameSubtitles.size();
    }

    // Generate game data from subtitles
    public static List<GameSubtitle> generateGameData(List<Subtitle> subtitles) {
        List<GameSubtitle> result = new ArrayList<>();
        Random random = new Random();

        for (Subtitle sub : subtitles) {
            String text = sub.getText();
            String[] words = text.split("\\s+");
            if (words.length < 2) {
                // Too short, just add without hiding
                result.add(new GameSubtitle(sub, "", -1));
                continue;
            }

            // Pick a word to hide (prefer longer words)
            String hiddenWord = "";
            int hiddenIndex = -1;
            for (int attempt = 0; attempt < 15; attempt++) {
                int idx = random.nextInt(words.length);
                String clean = words[idx].replaceAll("[^a-zA-Z]", "");
                if (clean.length() > 2) {
                    hiddenWord = words[idx];
                    hiddenIndex = idx;
                    break;
                }
            }

            // Fallback: pick any word
            if (hiddenIndex == -1) {
                hiddenIndex = random.nextInt(words.length);
                hiddenWord = words[hiddenIndex];
            }

            result.add(new GameSubtitle(sub, hiddenWord, hiddenIndex));
        }

        return result;
    }

    // Generate answer options for a specific game subtitle
    public static List<String> generateOptions(GameSubtitle target, List<GameSubtitle> allItems) {
        List<String> options = new ArrayList<>();
        String correct = target.hiddenWord.replaceAll("[^a-zA-Z']", "");
        if (correct.isEmpty()) correct = target.hiddenWord;
        options.add(correct);

        Random random = new Random();
        int maxAttempts = 100;
        int attempts = 0;

        while (options.size() < 4 && attempts < maxAttempts) {
            attempts++;
            GameSubtitle other = allItems.get(random.nextInt(allItems.size()));
            String[] words = other.subtitle.getText().split("\\s+");
            if (words.length == 0) continue;
            String w = words[random.nextInt(words.length)].replaceAll("[^a-zA-Z']", "");
            if (w.length() > 1 && !options.contains(w) && !w.equalsIgnoreCase(correct)) {
                options.add(w);
            }
        }

        // Fill remaining if needed
        while (options.size() < 4) {
            options.add("word" + options.size());
        }

        Collections.shuffle(options);
        return options;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        ImageView ivIndicator;
        ImageView btnTranslate;
        ImageView btnPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvGameSubtitleText);
            ivIndicator = itemView.findViewById(R.id.ivActiveIndicator);
            btnTranslate = itemView.findViewById(R.id.btnGameTranslate);
            btnPlay = itemView.findViewById(R.id.btnGamePlaySentence);
        }
    }
}
